package com.fastcampus.pass.job.pass;


import com.fastcampus.pass.repository.booking.BookingEntity;
import com.fastcampus.pass.repository.booking.BookingRepository;
import com.fastcampus.pass.repository.booking.BookingStatus;
import com.fastcampus.pass.repository.pass.PassEntity;
import com.fastcampus.pass.repository.pass.PassRepository;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.integration.async.AsyncItemProcessor;
import org.springframework.batch.integration.async.AsyncItemWriter;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JpaCursorItemReader;
import org.springframework.batch.item.database.builder.JpaCursorItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;

import javax.persistence.EntityManagerFactory;
import java.time.LocalDateTime;
import java.util.Map;

@Configuration
public class UsePassesJobConfig {
    private final int CHUNK_SIZE = 10;

    // @EnableBatchProcessing으로 인해 Bean으로 제공된 JobBuilderFactory, StepBuilderFactory
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final EntityManagerFactory entityManagerFactory;
    private final PassRepository passRepository;
    private final BookingRepository bookingRepository;

    public UsePassesJobConfig(JobBuilderFactory jobBuilderFactory, StepBuilderFactory stepBuilderFactory, EntityManagerFactory entityManagerFactory, PassRepository passRepository, BookingRepository bookingRepository) {
        this.jobBuilderFactory = jobBuilderFactory;
        this.stepBuilderFactory = stepBuilderFactory;
        this.entityManagerFactory = entityManagerFactory;
        this.passRepository = passRepository;
        this.bookingRepository = bookingRepository;
    }

    @Bean
    public Job usePassesJob(){
        return this.jobBuilderFactory.get("usePassesJob")
                .start(usePassesStep())
                .build();
    }

    @Bean
    public Step usePassesStep() {
        return this.stepBuilderFactory.get("usePassesStep")
                .<BookingEntity, BookingEntity>chunk(CHUNK_SIZE)
                .reader(usePassesItemReader())
                .processor(usePassesItemProcessor())
                .writer(usePassesItemWriter())
                .build();

    }

    @Bean
    public JpaCursorItemReader<BookingEntity> usePassesItemReader() {

        return new JpaCursorItemReaderBuilder<BookingEntity>()
                .name("usePassesItemReader")
                .entityManagerFactory(entityManagerFactory)
                // 상태(status)가 완료이며, 종료 일시(endedAt)이 과거인 예약이 이용권 차감 대상이 됩니다.
                .queryString("select b from BookingEntity b join fetch b.passEntity where b.status = :status and b.usedPass = false and b.endedAt < :endedAt")
                .parameterValues(Map.of("status", BookingStatus.COMPLETED, "endedAt", LocalDateTime.now()))
                .build();
    }

    /**
     * AsyncItemProcessor 사용하기 위해서 Build.gradle을 추가해야함
     * // 이 프로젝트에서는 적합하지 않지만, ItemProcessor의 수행이 오래걸려 병목이 생기는 경우에 AsyncItemProcessor, AsyncItemWriter를 사용하면 성능을 향상시킬 수 있습니다.
     */
    @Bean
    public AsyncItemProcessor<BookingEntity, BookingEntity>  usePassesAsyncItemProcessor(){
        AsyncItemProcessor<BookingEntity, BookingEntity> asyncItemProcessor = new AsyncItemProcessor<>();
        asyncItemProcessor.setDelegate(usePassesItemProcessor());
        asyncItemProcessor.setTaskExecutor(new SimpleAsyncTaskExecutor());
        return asyncItemProcessor;
    }

    /**
     * AsyncItemProcessor를 정의하면 ItemProcessor를 정의해야함
     */
    @Bean
    public ItemProcessor<BookingEntity, BookingEntity> usePassesItemProcessor() {

        return bookingEntity -> {
            PassEntity passEntity = bookingEntity.getPassEntity();
            passEntity.setRemainingCount(passEntity.getRemainingCount() - 1);
            bookingEntity.setPassEntity(passEntity);

            bookingEntity.setUsedPass(true);
            return bookingEntity;
        };
    }

    /**
     * AsyncItemWriter 사용하기 위해서 Build.gradle을 추가해야함
     */
    @Bean
    public AsyncItemWriter<BookingEntity> usePassesAsyncItemWriter(){
        AsyncItemWriter<BookingEntity> asyncItemWriter = new AsyncItemWriter<>();
        asyncItemWriter.setDelegate(usePassesItemWriter());
        return asyncItemWriter;
    }

    @Bean
    public ItemWriter<BookingEntity> usePassesItemWriter() {
        return bookingEntities -> {

            for(BookingEntity bookingEntity : bookingEntities) {
                int updatedCount = passRepository.updateRemainingCount(bookingEntity.getPassSeq(), bookingEntity.getPassEntity().getRemainingCount());

                if(updatedCount > 0) {
                    bookingRepository.updateUsedPasS(bookingEntity.getPassSeq(), bookingEntity.isUsedPass());
                }
            }
        };
    }
}
