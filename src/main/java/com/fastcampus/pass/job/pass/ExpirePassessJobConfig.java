package com.fastcampus.pass.job.pass;

import com.fastcampus.pass.repository.pass.PassEntity;
import com.fastcampus.pass.repository.pass.PassStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JpaCursorItemReader;
import org.springframework.batch.item.database.builder.JpaCursorItemReaderBuilder;
import org.springframework.batch.item.database.builder.JpaItemWriterBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.persistence.EntityManagerFactory;
import java.time.LocalDateTime;
import java.util.Map;

@Configuration
public class ExpirePassessJobConfig {

    private final int CHUNK_SIZE = 5;
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final EntityManagerFactory entityManagerFactory;

    public ExpirePassessJobConfig(JobBuilderFactory jobBuilderFactory, StepBuilderFactory stepBuilderFactory, EntityManagerFactory entityManagerFactory) {
        this.jobBuilderFactory = jobBuilderFactory;
        this.stepBuilderFactory = stepBuilderFactory;
        this.entityManagerFactory = entityManagerFactory;
    }

    @Bean
    public Job expirePassessJob(){

        return this.jobBuilderFactory.get("expirePassessJob") // expirePassessJob을 가져온다
                .start(expirePassessStep())
                .build(); // step 이 하나이기 때문에 종료처리한다
    }

    @Bean
    public Step expirePassessStep() {
        return this.stepBuilderFactory.get("expirePassessStep")
                .<PassEntity, PassEntity>chunk(CHUNK_SIZE) // chunk size 선언
                .reader(expirePassessItemReader())
                .processor(expirePassessItemProcessor())
                .writer(expirePassessItemWriter())
                .build();
    }


    /**
     * JpaCursorItemReader 는 
     * 페이징 기법보다 보다 높은 성능
     * 데이터 변경에 무관한 무결성 조회가 가능
     */
    @Bean
    @StepScope
    public JpaCursorItemReader<PassEntity> expirePassessItemReader() {

        return new JpaCursorItemReaderBuilder<PassEntity>()
                .name("expirePassessItemReader")
                .entityManagerFactory(entityManagerFactory) // jpa item reader, item write 다 마찬가지로 entityManagerFactory를 주입해줘야한다
                .queryString("select p from PassEntity p where p.status = :status and p.endedAt <= :endedAt")
                .parameterValues(Map.of("status", PassStatus.IN_PROGRESS,"endedAt", LocalDateTime.now()))
                .build();
    }

    @Bean
    public ItemProcessor<PassEntity, PassEntity> expirePassessItemProcessor() {

            return passEntity -> {
                passEntity.setStatus(PassStatus.EXPIRED);
                passEntity.setExpiredAt(LocalDateTime.now());
                return passEntity;
            };
    }

    /**
     * JpaItemWriter : JPA 영속성 관리를 위해 EntityManager를 필수로 설정해줘야 합니다
     */
    @Bean
    public ItemWriter<PassEntity> expirePassessItemWriter() {

        return new JpaItemWriterBuilder<PassEntity>()
                .entityManagerFactory(entityManagerFactory)
                .build();
    }
}
