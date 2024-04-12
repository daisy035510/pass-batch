package com.fastcampus.pass.repository.statistics;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface StatisticsRepository extends JpaRepository<StatisticsEntity, Integer> {

    /**
     * SUM(s.allCount), SUM(s.attendedCount), SUM(s.cancelledCount) 를 list로 가져오려던 것을
     * new com.fastcampus.pass.repository.statistics.AggregatedStatistics 로 객체화 시켜서 가져옴
     */
    @Query(value = "SELECT new com.fastcampus.pass.repository.statistics.AggregatedStatistics(s.statisticsAt, SUM(s.allCount), SUM(s.attendedCount), SUM(s.cancelledCount)) " +
            "         FROM StatisticsEntity s " +
            "        WHERE s.statisticsAt BETWEEN :from AND :to " +
            "     GROUP BY s.statisticsAt")
    List<AggregatedStatistics> findByStatisticsAtBetweenAndGroupBy(@Param("from") LocalDateTime from, @Param("to") LocalDateTime to);

}
