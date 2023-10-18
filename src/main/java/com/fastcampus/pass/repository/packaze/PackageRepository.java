package com.fastcampus.pass.repository.packaze;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;

public interface PackageRepository extends JpaRepository<PackageEntity, Integer> {

    List<PackageEntity> findByCreatedAtAfter(LocalDateTime dateTime, Pageable pageable);


    // JPQL
    @Modifying // 데이터가 변경이 되는 부분에서 사용이 된다, 허용하지 않으면 에러 발생
    @Transactional // update, delete 같은 쿼리는 트랜잭션이 없는 경우 트랜잭션 required 익셉션 에러가 발생
    @Query(value = " UPDATE PackageEntity p" +
            "           SET p.count = :count," +
            "           p.period = :period" +
            "         WHERE p.packageSeq = :packageSeq"
    )
    int updateCountAndPeriod(Integer packageSeq, int count, int period);
}
