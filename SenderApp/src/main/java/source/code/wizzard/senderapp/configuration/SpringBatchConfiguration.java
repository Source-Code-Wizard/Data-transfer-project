package source.code.wizzard.senderapp.configuration;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.data.RepositoryItemReader;
import org.springframework.batch.item.data.builder.RepositoryItemReaderBuilder;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.PlatformTransactionManager;
import source.code.wizzard.senderapp.batch.DataTransferItemWriter;
import source.code.wizzard.senderapp.model.DataEntity;
import source.code.wizzard.senderapp.repository.DataEntityRepository;
import source.code.wizzard.senderapp.service.DataTransferService;

import java.util.Map;


@Configuration
@DependsOn("RandomDataGenerator")
public class SpringBatchConfiguration {

    private final DataEntityRepository dataEntityRepository;

    @Qualifier("RestDataTransferService")
    private final DataTransferService dataTransferService;

    private final JobRepository jobRepository;

    public SpringBatchConfiguration(@Qualifier("DataTransferService") DataTransferService dataTransferService,
                                    JobRepository jobRepository,
                                    DataEntityRepository dataEntityRepository) {
        this.dataTransferService = dataTransferService;
        this.jobRepository = jobRepository;
        this.dataEntityRepository = dataEntityRepository;
    }

    private static final int CHUNK_SIZE = 500;


    @Bean
    public Job dataTransferJob(Step dataTransferStep) {
        return new JobBuilder("dataTransferJob", jobRepository)
                .start(dataTransferStep)
                .build();
    }

    @Bean
    public Step dataTransferStep(JobRepository jobRepository,
                                 PlatformTransactionManager platformTransactionManager,
                                 RepositoryItemReader<DataEntity> reader,
                                 ItemWriter<DataEntity> writer) {
        return new StepBuilder("dataTransferStep", jobRepository)
                .<DataEntity, DataEntity>chunk(500, platformTransactionManager)
                .reader(reader)
                .processor(processor())
                .writer(writer)
                .build();

    }

    @Bean
    public ItemWriter<DataEntity> writer() {
        return new DataTransferItemWriter(dataTransferService);
    }

    @Bean
    public ItemProcessor<DataEntity, DataEntity> processor() {
        // Simple pass-through processor
        return item -> item;
    }

    @Bean
    public RepositoryItemReader<DataEntity> reader() {
        return new RepositoryItemReaderBuilder<DataEntity>()
                .name("dataEntityReader")
                .repository(dataEntityRepository)
                .methodName("findAll")
                .arguments()
                .sorts(Map.of("id", Sort.Direction.ASC))
                .pageSize(CHUNK_SIZE)
                .build();
    }

}
