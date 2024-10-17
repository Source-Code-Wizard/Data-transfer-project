package source.code.wizard.senderapp.service.strategy;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import source.code.wizard.senderapp.model.DataEntity;
import source.code.wizard.senderapp.service.strategy.DataTransferStrategy;

import java.util.List;
import java.util.function.Function;


@Slf4j
@Component
@RequiredArgsConstructor
public class RestDataTransferStrategy implements DataTransferStrategy {

    @Value("${app.rest.type}")
    private String integrationType;
    private static final String REST_BLOCKING = "REST_BLOCKING";
    private static final String REST_ASYNC = "REST_ASYNC";
    private static final Integer BATCH_SIZE = 100;
    private final WebClient webClient;

    @Override
    public void sendDataInBatches(final List<DataEntity> data) {
        if (integrationType.equals(REST_ASYNC)) {
            log.info(REST_ASYNC + ": Sending data...");
            asyncRestIntegration(data, BATCH_SIZE);
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
                        // Log the error and the ID of the entity
                        log.error("Failed to save entity with ID: {}. Error: {}", dataEntity.getId(), error.getMessage());
                    })
                    .block();
        });
    }

    private void asyncRestIntegration(List<DataEntity> data, int batchSize) {
        Flux.fromIterable(data)
                .buffer(batchSize)
                .flatMap(this::sendBatch, 10)
                .subscribe(
                        success -> log.info("Batch sent successfully!"),
                        error -> log.error("Error sending batch: " + error.getMessage())
                );
    }

    private Flux<Void> sendBatch(final List<DataEntity> batch) {
        return Flux.fromIterable(batch)
                .flatMap(sendData());
    }

    private Function<DataEntity, Publisher<? extends Void>> sendData() {
        return data -> webClient.post()
                .uri("/data/api/v1/save")
                .bodyValue(data)
                .retrieve()
                .bodyToMono(Void.class)
                .onErrorResume(e -> {
                    System.err.println("Error sending data: " + e.getMessage());
                    return Mono.empty();
                });
    }

    @Override
    public String getKey() {
        return "REST";
    }
}
