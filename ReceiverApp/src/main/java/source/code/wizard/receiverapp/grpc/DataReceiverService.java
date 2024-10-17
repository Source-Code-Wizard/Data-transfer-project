package source.code.wizard.receiverapp.grpc;

import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.stereotype.Service;
import source.code.wizard.grpc.DataRequest;
import source.code.wizard.grpc.DataResponse;
import source.code.wizard.grpc.GRPCDataTransferServiceGrpc;
import source.code.wizard.receiverapp.repository.DataEntityRepository;

@Service
@GrpcService
@RequiredArgsConstructor
public class DataReceiverService extends GRPCDataTransferServiceGrpc.GRPCDataTransferServiceImplBase {

    private final DataEntityRepository repository;

    @Override
    public StreamObserver<DataRequest> sendData(StreamObserver<DataResponse> responseObserver) {
        return new DataReceiverObserver(repository, responseObserver);
    }
}
