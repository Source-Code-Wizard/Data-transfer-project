package source.code.wizard.senderapp.grpc;

import io.grpc.ManagedChannel;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;
import source.code.wizard.grpc.DataResponse;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicLong;

@Slf4j
public class DataResponseObserver implements StreamObserver<DataResponse> {
    private final CountDownLatch latch = new CountDownLatch(1);
    private final AtomicLong successCount = new AtomicLong(0);
    private final AtomicLong failureCount = new AtomicLong(0);
    private final ManagedChannel communicationChannel;

    public DataResponseObserver(ManagedChannel communicationChannel) {
        this.communicationChannel = communicationChannel;
    }

    @Override
    public void onNext(DataResponse dataResponse) {
        if (dataResponse.getId() == -1) {
            // This is the summary response
            log.info("Transfer summary: {}", dataResponse.getMessage());
        } else {
            if (dataResponse.getMessage().startsWith("Failed")) {
                failureCount.incrementAndGet();
                log.error("Entry failed - ID: {}, Message: {}", dataResponse.getId(), dataResponse.getMessage());
            } else {
                successCount.incrementAndGet();
                // log.info("Entry succeeded - ID: {}, Message: {}", dataResponse.getId(), dataResponse.getMessage());
            }
        }
    }

    @Override
    public void onError(Throwable throwable) {
        log.error("Error occurred during data transfer: " + throwable.getMessage());
        latch.countDown();
    }

    @Override
    public void onCompleted() {
        log.info("Data transfer completed. Successful: {}, Failed: {}", successCount.get(), failureCount.get());
        communicationChannel.shutdown();
        latch.countDown();
    }
}