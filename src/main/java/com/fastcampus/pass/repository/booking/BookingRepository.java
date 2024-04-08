package com.fastcampus.pass.repository.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;


public interface BookingRepository extends JpaRepository<BookingEntity, Integer> {

    // JPQL
    @Transactional
    @Modifying
    @Query(value = "UPDATE BookingEntity b" +
            "          SET b.usedPass = true," +
            "              b.modifiedAt = CURRENT_TIMESTAMP " +
            "        WHERE b.passSeq = :passSeq ")
    int updateUsedPasS(Integer passSeq, boolean usedPass);
}
