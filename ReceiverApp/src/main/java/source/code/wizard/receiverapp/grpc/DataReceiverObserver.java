//package source.code.wizard.receiverapp.grpc;
//
//import io.grpc.stub.StreamObserver;
//import jakarta.transaction.Transactional;
//import lombok.extern.slf4j.Slf4j;
//import source.code.wizard.grpc.DataRequest;
//import source.code.wizard.grpc.DataResponse;
//import source.code.wizard.receiverapp.Model.entity.DataEntity;
//import source.code.wizard.receiverapp.repository.DataEntityRepository;
//
//import java.util.Random;
//
//@Slf4j
//public class DataReceiverObserver implements StreamObserver<DataRequest> {
//
//    private final DataEntityRepository dataEntityRepository;
//
//    private final StreamObserver<DataResponse> responseObserver;
//
//    public DataReceiverObserver(DataEntityRepository dataEntityRepository, StreamObserver<DataResponse> responseObserver) {
//        this.dataEntityRepository = dataEntityRepository;
//        this.responseObserver = responseObserver;
//    }
//
//    @Override
//    @Transactional
//    public void onNext(DataRequest dataRequest) {
//        try {
//            log.info("Data with ID: " + dataRequest.getId() + " was received!");
//            final DataEntity dataEntity = DataEntity.builder()
//                    .fieldOne(dataRequest.getFieldOne())
//                    .fieldTwo(dataRequest.getFieldTwo())
//                    .fieldThree(dataRequest.getFieldThree())
//                    .fieldFour(dataRequest.getFieldFour())
//                    .id(dataRequest.getId())
//                    .build();
//
//            // Let's simulate a scenario when something goes wrong
//            Random rand = new Random();
//            if (rand.nextInt(100) < 10)
//                throw new Exception("Data: " + dataEntity.getId() + " was not saved!");
//
//            dataEntityRepository.save(dataEntity);
//            log.info("Data with ID: " + dataEntity.getId() + " was saved!");
//
//        } catch (Exception e) {
//            log.error("Failed to save data with ID: " + dataRequest.getId(), e);
//
//            // Notify the client about the error
//            responseObserver.onError(new RuntimeException("Failed to save data with ID: " + dataRequest.getId() + ". Error: " + e.getMessage()));
//        }
//    }
//
//
//    @Override
//    public void onError(Throwable throwable) {
//        // Handle error cases
//    }
//
//    @Override
//    public void onCompleted() {
//       final DataResponse dataResponse = DataResponse.newBuilder()
//               .setMessage("Transfer was successful")
//               .build();
//       responseObserver.onNext(dataResponse);
//       responseObserver.onCompleted();
//    }
//}


package source.code.wizard.receiverapp.grpc;

import io.grpc.stub.StreamObserver;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import source.code.wizard.grpc.DataRequest;
import source.code.wizard.grpc.DataResponse;
import source.code.wizard.receiverapp.Model.entity.DataEntity;
import source.code.wizard.receiverapp.repository.DataEntityRepository;

import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

@Slf4j
public class DataReceiverObserver implements StreamObserver<DataRequest> {

    private final DataEntityRepository dataEntityRepository;
    private final StreamObserver<DataResponse> responseObserver;
    private final AtomicLong successCount = new AtomicLong(0);
    private final AtomicLong failureCount = new AtomicLong(0);

    public DataReceiverObserver(DataEntityRepository dataEntityRepository, StreamObserver<DataResponse> responseObserver) {
        this.dataEntityRepository = dataEntityRepository;
        this.responseObserver = responseObserver;
    }

    @Override
    @Transactional
    public void onNext(DataRequest dataRequest) {
        try {
            log.info("Data with ID: {} was received!", dataRequest.getId());
            final DataEntity dataEntity = DataEntity.builder()
                    .fieldOne(dataRequest.getFieldOne())
                    .fieldTwo(dataRequest.getFieldTwo())
                    .fieldThree(dataRequest.getFieldThree())
                    .fieldFour(dataRequest.getFieldFour())
                    .id(dataRequest.getId())
                    .build();

            // Simulating a scenario when something goes wrong
            Random rand = new Random();
            if (rand.nextInt(100) < 3) {
                throw new Exception("Data: " + dataEntity.getId() + " was not saved!");
            }

            dataEntityRepository.save(dataEntity);
            log.info("Data with ID: {} was saved!", dataEntity.getId());
            successCount.incrementAndGet();

            // Send a success response for this entry
            sendResponse(dataRequest.getId(), "Data saved successfully");

        } catch (Exception e) {
            log.error("Failed to save data with ID: {}", dataRequest.getId(), e);
            failureCount.incrementAndGet();

            // Send an error response for this entry
            sendResponse(dataRequest.getId(), "Failed to save: " + e.getMessage());
        }
    }

    private void sendResponse(long id, String message) {
        DataResponse response = DataResponse.newBuilder()
                .setId(id)
                .setMessage(message)
                .build();
        responseObserver.onNext(response);
    }

    @Override
    public void onError(Throwable throwable) {
        log.error("Error in stream", throwable);
        // You might want to send a final error response here
    }

    @Override
    public void onCompleted() {
        log.info("Stream completed. Successful saves: {}, Failed saves: {}",
                successCount.get(), failureCount.get());

        // Send a final summary response
        DataResponse summaryResponse = DataResponse.newBuilder()
                .setId(-1) // Use a special ID to indicate summary
                .setMessage(String.format("Transfer completed. Successful: %d, Failed: %d",
                        successCount.get(), failureCount.get()))
                .build();
        responseObserver.onNext(summaryResponse);
        responseObserver.onCompleted();
    }
}