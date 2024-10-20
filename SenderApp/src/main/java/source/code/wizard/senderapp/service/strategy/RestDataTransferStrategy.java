package source.code.wizard.senderapp.service.strategy;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import source.code.wizard.senderapp.model.DataEntity;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Component
@RequiredArgsConstructor
public class RestDataTransferStrategy implements DataTransferStrategy {

    @Value("${app.rest.type}")
    private String integrationType;
    private static final String REST_BLOCKING = "REST_BLOCKING";
    private static final String REST_ASYNC = "REST_ASYNC";
    private static final int BATCH_SIZE = 100;
    private static final int CONCURRENCY = 10;
    private static final Duration TIMEOUT = Duration.ofSeconds(30);
    private final WebClient webClient;

    @Override
    public void sendDataInBatches(final List<DataEntity> data) {
        if (integrationType.equals(REST_ASYNC)) {
            log.info(REST_ASYNC + ": Sending data...");
            asyncRestIntegration(data);
        } else if (integrationType.equals(REST_BLOCKING)) {
            log.info(REST_BLOCKING + ": Sending data...");
            blockingRestIntegration(data);
        }
    }

    private void blockingRestIntegration(List<DataEntity> data) {
        data.forEach(dataEntity -> {
            webClient.post()
                    .uri("/data/api/v1/save")
                    .bodyValue(dataEntity)
                    .retrieve()
                    .bodyToMono(Void.class)
                    .doOnError(error -> {
                        log.error("Failed to save entity with ID: {}. Error: {}", dataEntity.getId(), error.getMessage());
                    })
                    .onErrorResume(error -> {
                        // Log the error and continue with the next request
                        log.warn("Error occurred while saving entity with ID: {}. Continuing with the next entity.", dataEntity.getId());
                        return Mono.empty(); // Return an empty Mono to prevent blocking the entire process
                    })
                    .block();
        });
    }

    private void asyncRestIntegration(List<DataEntity> data) {
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failureCount = new AtomicInteger(0);

        Flux.fromIterable(data)
                .buffer(BATCH_SIZE)
                .flatMap(batch -> sendBatch(batch, successCount, failureCount), CONCURRENCY)
                .doOnComplete(() -> log.info("Async integration completed. Successes: {}, Failures: {}",
                        successCount.get(), failureCount.get()))
                .blockLast(Duration.ofMinutes(1)); // This line of code waits for all 500 requests to be finished
        // otherwise the next step will start sending requests and the sender application will not handle all
        // these concurrent requests approprietly
    }

    private Flux<Void> sendBatch(List<DataEntity> batch, AtomicInteger successCount, AtomicInteger failureCount) {
        return Flux.fromIterable(batch)
                .flatMap(data -> sendData(data, successCount, failureCount)
                        .subscribeOn(Schedulers.boundedElastic()), CONCURRENCY)
                .onErrorContinue((error, obj) -> log.error("Error in batch: {}", error.getMessage()));
    }

    private Mono<Void> sendData(DataEntity data, AtomicInteger successCount, AtomicInteger failureCount) {
        return webClient.post()
                .uri("/data/api/v1/save")
                .bodyValue(data)
                .retrieve()
                .bodyToMono(Void.class)
                .timeout(TIMEOUT)
                .doOnSuccess(v -> successCount.incrementAndGet())
                .doOnError(e -> {
                    failureCount.incrementAndGet();
                    log.error("Error sending data for ID {}: {}", data.getId(), e.getMessage());
                })
                .onErrorResume(e -> Mono.empty());
    }

    @Override
    public String getKey() {
        return "REST";
    }
}