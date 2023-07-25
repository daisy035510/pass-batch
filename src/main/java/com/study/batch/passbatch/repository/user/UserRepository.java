package com.study.batch.passbatch.repository.user;

import com.study.batch.passbatch.config.JpaConfig;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<UserEntity, Integer> {
}
