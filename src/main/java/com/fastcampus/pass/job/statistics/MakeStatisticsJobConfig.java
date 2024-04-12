package com.fastcampus.pass.job.statistics;

import com.fastcampus.pass.repository.booking.BookingEntity;
import com.fastcampus.pass.repository.statistics.StatisticsEntity;
import com.fastcampus.pass.repository.statistics.StatisticsRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.FlowBuilder;
import org.springframework.batch.core.job.flow.Flow;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JpaCursorItemReader;
import org.springframework.batch.item.database.builder.JpaCursorItemReaderBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;

import javax.persistence.EntityManagerFactory;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Configuration
public class MakeStatisticsJobConfig {
    private final int CHUNK_SIZE = 10;
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final EntityManagerFactory entityManagerFactory;

    private final StatisticsRepository statisticsRepository;
    private final MakeDailyStatisticsTasklet makeDailyStatisticsTasklet;
    private final MakeWeeklyStatisticsTasklet makeWeekilyStatisticsTasklet;

    public MakeStatisticsJobConfig(JobBuilderFactory jobBuilderFactory, StepBuilderFactory stepBuilderFactory, EntityManagerFactory entityManagerFactory, StatisticsRepository statisticsRepository, MakeDailyStatisticsTasklet makeDailyStatisticsTasklet, MakeWeeklyStatisticsTasklet makeWeekilyStatisticsTasklet) {
        this.jobBuilderFactory = jobBuilderFactory;
        this.stepBuilderFactory = stepBuilderFactory;
        this.entityManagerFactory = entityManagerFactory;
        this.statisticsRepository = statisticsRepository;
        this.makeDailyStatisticsTasklet = makeDailyStatisticsTasklet;
        this.makeWeekilyStatisticsTasklet = makeWeekilyStatisticsTasklet;
    }


    @Bean
    public Job makeStatisticsJob() {

        Flow addSatisticsFlow = new FlowBuilder<Flow>("addSatisticsFlow")
                .start(addSatisticsStep())
                .build();

        Flow makeDailyStatisticsFlow = new FlowBuilder<Flow>("makeDailyStatisticsFlow")
                .start(makeDailyStatisticsStep())
                .build();


        Flow makeWeeklyStatisticsFlow = new FlowBuilder<Flow>("makeWeeklyStatisticsFlow")
                .start(makeWeeklyStatisticsStep())
                .build();

        /**
         * 병렬로 처리
         */
        Flow parallelStatisticsFlow = new FlowBuilder<Flow>("parallelStatisticsFlow")
                .split(new SimpleAsyncTaskExecutor())
                .add(makeDailyStatisticsFlow, makeWeeklyStatisticsFlow)
                .build();

        /**
         * start(addSatisticsFlow) 첫번째로 실행된 다음
         * 두번째로 next(parallelStatisticsFlow) 가 병렬로 처리됨을 확인
         */
        return this.jobBuilderFactory.get("makeStatisticsJob")
                .start(addSatisticsFlow)
                .next(parallelStatisticsFlow)
                .build()    // Flow Job Builder
                .build();
    }


    @Bean
    public Step addSatisticsStep() {
        return this.stepBuilderFactory.get("addSatisticsStep")
                .<BookingEntity, BookingEntity>chunk(CHUNK_SIZE)
                .reader(addStatisticItemReader(null, null))
                .writer(addStatisticItemWriter())
                .build();
    }

    @Bean
    @StepScope  // 잡파라미터를 사용하려면 반드시 이 어노테이션을 붙여야한다
    public JpaCursorItemReader<BookingEntity> addStatisticItemReader(@Value("#{jobParameters[from]}") String fromString, @Value("#{jobParameters[to]}") String toString) {
        final LocalDateTime from = LocalDateTime.parse(fromString);
        final LocalDateTime to = LocalDateTime.parse(toString);

        return new JpaCursorItemReaderBuilder<BookingEntity>()
                .name("addStatisticItemReader")
                .entityManagerFactory(entityManagerFactory)
                .queryString("select b from BookingEntity b where b.endedAt between :from and :to ")
                .parameterValues(Map.of("from", from, "to", to))
                .build();
    }

    @Bean
    public ItemWriter<BookingEntity> addStatisticItemWriter() {

        return bookingEntities -> {

            Map<LocalDateTime, StatisticsEntity> statisticsEntityMap = new LinkedHashMap<>(); // 순서가 있는 map
            for(BookingEntity bookingEntity : bookingEntities) {
                final LocalDateTime statisticsAt = bookingEntity.getStatisticsAt();

                StatisticsEntity statisticsEntity = statisticsEntityMap.get(statisticsAt);
                if(statisticsAt == null) {
                    statisticsEntityMap.put(statisticsAt, StatisticsEntity.create(bookingEntity));
                } else {
                    statisticsEntity.add(bookingEntity);
                }
            }
            // TO-DO 코드 확인
            final List<StatisticsEntity> statisticsEntities = new ArrayList<>(statisticsEntityMap.values());
            statisticsRepository.saveAll(statisticsEntities);
        };
    }

    /**
     * 병렬로 처리
     * 구현부분을 따로 클래스로 처리
     */
    @Bean
    public Step makeDailyStatisticsStep() {

        return this.stepBuilderFactory.get("makeDailyStatisticsStep")
                .tasklet(makeWeekilyStatisticsTasklet)
                .build();
    }

    /**
     * 병렬로 처리
     * 구현부분을 따로 클래스로 처리
     */
    @Bean
    public Step makeWeeklyStatisticsStep() {

        return this.stepBuilderFactory.get("makeDailyStatisticsStep")
                .tasklet(makeDailyStatisticsTasklet)
                .build();
    }


}
