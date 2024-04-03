package com.fastcampus.pass;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableBatchProcessing
@SpringBootApplication
public class PassBatchApplication {

	/*// Job을 생성할 Builder
	private final JobBuilderFactory jobBuilderFactory;

	// Step을 생성할 Builder
	private final StepBuilderFactory stepBuilderFactory;

	public PassBatchApplication(JobBuilderFactory jobBuilderFactory, StepBuilderFactory stepBuilderFactory) {
		this.jobBuilderFactory = jobBuilderFactory;
		this.stepBuilderFactory = stepBuilderFactory;
	}

	// 실제 step 선언
	@Bean
	public Step passStep() {
		return this.stepBuilderFactory.get("passStep") // step 이름
				.tasklet((contribution, chunkContext) -> {
					System.out.println("Execute PassStep");
					return RepeatStatus.FINISHED;
				}).build();
	}

	// 실제 Job 선언
	@Bean
	public Job passJob() {
		return this.jobBuilderFactory.get("passJob") // job 이름
				.start(passStep()) // 선언된 step 함수 호출
				.build();
	}*/

	public static void main(String[] args) {
		SpringApplication.run(PassBatchApplication.class, args);
	}
}
