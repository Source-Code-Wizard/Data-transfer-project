package source.code.wizard.senderapp.configuration;


import com.github.javafaker.Faker;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Component;
import source.code.wizard.senderapp.model.DataEntity;
import source.code.wizard.senderapp.repository.DataEntityRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadLocalRandom;


@Slf4j
@Component("RandomDataGenerator")
@RequiredArgsConstructor
public class RandomDataGenerator {

    private final DataEntityRepository dataEntityRepository;
    private final TaskExecutor taskExecutorInitDB;

    @PostConstruct
    public void initializeDatabase(){
        if(dataEntityRepository.count()>0){
            log.warn("DB initialization has already been completed!");
            return;
        }

        final long startMs = System.currentTimeMillis();
        final int totalEntries = 10000;
        final int batchSize = 1000;
        final int numOfThreads = 10;
        final int entriesPerThread = totalEntries / numOfThreads;
        List<CompletableFuture<Void>> futures = new ArrayList<>();


        for (int i = 0; i < numOfThreads; i++) {
            final int threadIndex = i;
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> generateAndSaveData(entriesPerThread, batchSize, threadIndex), taskExecutorInitDB);
            futures.add(future);
        }
        CompletableFuture.allOf(new CompletableFuture[0]).join();
        long endMs = System.currentTimeMillis();
        System.out.println("Database initialized with " + totalEntries + " entries in " + (endMs - startMs) + " ms");
    }

    @Transactional
    public void generateAndSaveData(final int totalNumOfEntries, final int batchSize, final int threadIndex) {
        final Faker faker = new Faker(ThreadLocalRandom.current());
        List<DataEntity> entities = new ArrayList<>();

        for (int i = 0; i < totalNumOfEntries; i++) {
            DataEntity entity = DataEntity.builder()
                    .fieldOne(faker.name().fullName())
                    .fieldTwo(faker.internet().emailAddress())
                    .fieldThree(faker.phoneNumber().cellPhone())
                    .fieldFour(faker.address().fullAddress())
                    .fieldFive(faker.job().title())
                    .build();
            entities.add(entity);

            if (entities.size() >= batchSize) {
                dataEntityRepository.saveAll(entities);
                entities.clear();
                System.out.println("Thread " + threadIndex + " saved " + (i + 1) + " entities");
            }
        }

        if (!entities.isEmpty()) {
            dataEntityRepository.saveAll(entities);
        }
    }
}
