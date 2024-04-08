package com.fastcampus.pass.job.notification;

import com.fastcampus.pass.repository.booking.BookingEntity;
import com.fastcampus.pass.repository.booking.BookingStatus;
import com.fastcampus.pass.repository.notification.NotificationEntity;
import com.fastcampus.pass.repository.notification.NotificationEvent;
import com.fastcampus.pass.repository.notification.NotificationModelMapper;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JpaCursorItemReader;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.builder.JpaCursorItemReaderBuilder;
import org.springframework.batch.item.database.builder.JpaItemWriterBuilder;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.batch.item.support.SynchronizedItemStreamReader;
import org.springframework.batch.item.support.builder.SynchronizedItemStreamReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;

import javax.persistence.EntityManagerFactory;
import java.time.LocalDateTime;
import java.util.Map;

@Configuration
public class SendNotificationBeforeClassJobConfig {

    private final int CHUNK_SIZE = 10;
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final EntityManagerFactory entityManagerFactory;
    private final SendNotificationItemWriter sendNotificationItemWriter; // REST API 때문에 class 를 따로 선언

    public SendNotificationBeforeClassJobConfig(JobBuilderFactory jobBuilderFactory, StepBuilderFactory stepBuilderFactory, EntityManagerFactory entityManagerFactory, SendNotificationItemWriter sendNotificationItemWriter) {
        this.jobBuilderFactory = jobBuilderFactory;
        this.stepBuilderFactory = stepBuilderFactory;
        this.entityManagerFactory = entityManagerFactory;
        this.sendNotificationItemWriter = sendNotificationItemWriter;
    }


    @Bean
    public Job sendNotificationBeforeClassJob() {
        return this.jobBuilderFactory.get("sendNotificationBeforeClassJob")
                .start(addNotificationStep()) // 첫번째 스텝
                .start(sendNotificationStep()) // 두번째 스텝
                .build();
    }

    /**
     * 첫번째 스텝 - 알람대상선정
     * 기본적인 싱글 쓰레드 chunk 기반 스텝
     */
    @Bean
    public Step addNotificationStep() {
        return this.stepBuilderFactory.get("addNotificationStep")
                .<BookingEntity, NotificationEntity>chunk(CHUNK_SIZE)
                .reader(addNotificationItemReader())
                .processor(addNotificationProcessor())
                .writer(addNotificationWriter())
                .build();
    }

    /**
     * JpaPagingItemRader
     * 쿼리당 pageSize만큼 가져오며 다른 pagingItemReader와 마찬가지로 Thread-safe
     */
    @Bean
    public JpaPagingItemReader<BookingEntity> addNotificationItemReader() {

        return new JpaPagingItemReaderBuilder<BookingEntity>()
                .name("addNotificationItemReader")
                .entityManagerFactory(entityManagerFactory)
                .pageSize(CHUNK_SIZE)
                .queryString("select b from BookingEntity b join fetch b.userEntity where b.status = :status and b.startedAt <= :startedAt order by b.bookingSeq")
                .parameterValues(Map.of("status", BookingStatus.READY, "startedAt", LocalDateTime.now()))
                .build();
    }
    @Bean
    public ItemProcessor<BookingEntity, NotificationEntity> addNotificationProcessor() {
        return bookingEntity -> NotificationModelMapper.INSTANCE.toNotificationEntity(bookingEntity, NotificationEvent.BEFORE_CLASS);

    }
    @Bean
    public ItemWriter<NotificationEntity> addNotificationWriter() {
        return new JpaItemWriterBuilder<NotificationEntity>()
                .entityManagerFactory(entityManagerFactory)
                .build();
    }

    /**
     * 두번째 스텝 - 알람보내기
     */
    @Bean
    public Step sendNotificationStep() {

            return this.stepBuilderFactory.get("sendNotificationStep")
                    .<NotificationEntity, NotificationEntity>chunk(CHUNK_SIZE)
                    .reader(sendNotificationItemReader())
                    .writer(sendNotificationItemWriter)  // REST API 때문에 class 를 따로 선언
                    .taskExecutor(new SimpleAsyncTaskExecutor()) // thread가 계속 생성될건지, 지정된 thread pool 내에서 사용할건지
                    .build();
    }

    /**
     *  그래서 커서로는 해야하는데 thread safe 하지 않아 어떻게 해야하느냐 ??
     *  그 때 SynchroziedItemStreamReader로 감싸서 Synchrozied하게 돌려주면 됨
     *  어쩔수없이 reader하는 부분은 순차적으로 실행됨
     *  대신 writer processor 부분은 멀티 쓰레드로 진행하게 됨
     */
    @Bean
    public SynchronizedItemStreamReader<NotificationEntity> sendNotificationItemReader() {

        JpaCursorItemReader<NotificationEntity> itemReader = new JpaCursorItemReaderBuilder<NotificationEntity>()
                .name("sendNotificationItemReader")
                .entityManagerFactory(entityManagerFactory)
                .queryString("select n from Notification n where n.event = :event and n.sent = :sent")
                .parameterValues(Map.of("event", NotificationEvent.BEFORE_CLASS, "sent", false))
                .build();


        return new SynchronizedItemStreamReaderBuilder<NotificationEntity>()
                .delegate(itemReader)
                .build();
    }

}
