package source.code.wizard.senderapp.service.strategy;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import source.code.wizard.grpc.DataRequest;
import source.code.wizard.grpc.GRPCDataTransferServiceGrpc;
import source.code.wizard.senderapp.grpc.DataResponseObserver;
import source.code.wizard.senderapp.model.DataEntity;

import java.util.List;

@Slf4j
@Component
public class GRPCDataTransferStrategy implements DataTransferStrategy {

    @Override
    public void sendDataInBatches(List<DataEntity> data) {
        final ManagedChannel receiverChannel = ManagedChannelBuilder.forAddress("localhost", 8090)
                .usePlaintext()
                .build();
        final GRPCDataTransferServiceGrpc.GRPCDataTransferServiceStub grpcDataTransferServiceStub = GRPCDataTransferServiceGrpc.newStub(receiverChannel);
        final DataResponseObserver dataResponseObserver = new DataResponseObserver();

        StreamObserver<DataRequest> dataRequestStreamObserver = grpcDataTransferServiceStub.sendData(dataResponseObserver);

        data.forEach(dataEntity -> {
            DataRequest request = toDataRequest(dataEntity);
            dataRequestStreamObserver.onNext(request);
        });

        dataRequestStreamObserver.onCompleted();

//        dataResponseObserver.awaitCompletion();
//        try {
//            receiverChannel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
//        } catch (InterruptedException e) {
//            Thread.currentThread().interrupt();
//            log.error("Interrupted while shutting down the channel", e);
//        }

        log.info("Data transfer completed. Successful entries: {}, Failed entries: {}",
                dataResponseObserver.getSuccessCount(), dataResponseObserver.getFailureCount());
    }

    private DataRequest toDataRequest(final DataEntity dataEntity) {
        return DataRequest.newBuilder()
                .setFieldOne(dataEntity.getFieldOne())
                .setFieldTwo(dataEntity.getFieldTwo())
                .setFieldThree(dataEntity.getFieldThree())
                .setFieldFour(dataEntity.getFieldFour())
                .setId(dataEntity.getId())
                .build();
    }

    @Override
    public String getKey() {
        return "gRPC";
    }
}
