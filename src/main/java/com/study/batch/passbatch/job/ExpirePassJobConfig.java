package com.study.batch.passbatch.job;

import com.study.batch.passbatch.repository.pass.PassEntity;
import com.study.batch.passbatch.repository.pass.PassStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JpaCursorItemReader;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.database.builder.JpaCursorItemReaderBuilder;
import org.springframework.batch.item.database.builder.JpaItemWriterBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.persistence.EntityManagerFactory;

import java.time.LocalDateTime;

import java.util.Map;

@Configuration
public class ExpirePassJobConfig {
    private final int CHUNK_SIZE = 5;

    //  @EnableBatchProcessing로 인해 Bean으로 제공된 JobBuilderFactory, StepBuilderFactory
    private final JobBuilderFactory jobBuilderFacztory;
    private final StepBuilderFactory stepBuilderFactory;
    private final EntityManagerFactory entityManagerFactory;

    public ExpirePassJobConfig(JobBuilderFactory jobBuilderFacztory, StepBuilderFactory stepBuilderFactory, EntityManagerFactory entityManagerFactory) {
        this.jobBuilderFacztory = jobBuilderFacztory;
        this.stepBuilderFactory = stepBuilderFactory;
        this.entityManagerFactory = entityManagerFactory;
    }

    @Bean
    public Job expirePassesJob() {
        return this.jobBuilderFacztory.get("expirePassesJob")
                .start(expirePassesStep())
                .build();
    }

    @Bean
    public Step expirePassesStep() {
        return this.stepBuilderFactory.get("expirePassesStep")
                .<PassEntity, PassEntity>chunk(CHUNK_SIZE)//
                .reader(expirePassesItemReader())
                .processor(expirePassesItemProcessor())
                .writer(expirePassessItemWriter())
                .build();
    }


    /**
     * JpaCursorItemReader: JpaPagingItemReader만 지원하다가 Spring 4.3에서 추가되었습니다.
     * 페이징 기법보다 보다 높은 성능으로, 데이터 변경에 무관한 무결성 조회가 가능합니다.
     */
    @Bean
    @StepScope
    public JpaCursorItemReader<PassEntity> expirePassesItemReader() {
        return new JpaCursorItemReaderBuilder<PassEntity>()
                .name("expirePassesItemReader")
                .entityManagerFactory(entityManagerFactory)
                .queryString("SELECT p FROM PassEntity p WHERE p.status = :status AND p.endAt <= :endedAt")
                .parameterValues(Map.of("status", PassStatus.PROGRESSED, "endAt", LocalDateTime.now()))
                .build();
    }

    // to-do 람다식 분석
    @Bean
    public ItemProcessor<PassEntity, PassEntity> expirePassesItemProcessor() {
        return PassEntity -> {
            PassEntity.setStatus(PassStatus.EXPIRED);
            PassEntity.setExpiredAt(LocalDateTime.now());
            return PassEntity;
        };
    }

    @Bean
    public ItemWriter<PassEntity> expirePassessItemWriter() {

        return new JpaItemWriterBuilder<PassEntity>()
                .entityManagerFactory(entityManagerFactory)
                .build();
    }
}
