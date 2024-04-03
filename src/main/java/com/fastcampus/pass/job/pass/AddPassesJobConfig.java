package com.fastcampus.pass.job.pass;

import com.fastcampus.pass.repository.pass.PassRepository;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Batch 이용권 일괄 지급
 * tasklet class를 따로 빼서 구현함
 */
@Configuration
public class AddPassesJobConfig {

    // @EnabaleBatchProcessing으로 인해 Bean으로 제공된 JobBuilderFactor, StepBuilderFactory
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final AddPassesTasklet addPassesTasklet;

    private final PassRepository passRepository;

    public AddPassesJobConfig(JobBuilderFactory jobBuilderFactory, StepBuilderFactory stepBuilderFactory, AddPassesTasklet addPassesTasklet, PassRepository passRepository) {
        this.jobBuilderFactory = jobBuilderFactory;
        this.stepBuilderFactory = stepBuilderFactory;
        this.addPassesTasklet = addPassesTasklet;
        this.passRepository = passRepository;
    }


    @Bean
    public Job addPassesJob() {
        return this.jobBuilderFactory.get("addPassesJob")
                .start(addPassesStep())
                .build();
    }

    @Bean
    public Step addPassesStep() {
        return this.stepBuilderFactory.get("addPassesStep")
                .tasklet(addPassesTasklet)
                .build();
    }
}
