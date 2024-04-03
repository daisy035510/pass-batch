package com.fastcampus.pass.repository.pass;

import com.fastcampus.pass.repository.packaze.PackageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PassRepository extends JpaRepository<PassEntity, Integer> {
}
