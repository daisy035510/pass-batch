package com.fastcampus.pass.config;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * TO-D0
 * 각 어노테이션이 뭐 하는지 알아보느 ㄴ시간
 */
@Configuration
@EnableJpaAuditing
@EnableAutoConfiguration
@EnableBatchProcessing
@EntityScan("com.fastcampus.pass.repository")
@EnableJpaRepositories("com.fastcampus.pass.repository")
@EnableTransactionManagement
public class TestBatchConfig {
}
