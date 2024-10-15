package source.code.wizard.senderapp.service.strategy;

import com.google.protobuf.Empty;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import source.code.wizard.grpc.DataRequest;
import source.code.wizard.grpc.GRPCDataTransferServiceGrpc;

@GrpcService
public class GRPCDataTransferStrategy extends GRPCDataTransferServiceGrpc.GRPCDataTransferServiceImplBase {

    @Override
    public void sendData(DataRequest request, StreamObserver<Empty> responseObserver) {
        super.sendData(request, responseObserver);
    }
}
