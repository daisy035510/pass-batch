package com.fastcampus.pass.repository.pass;

import com.fastcampus.pass.repository.BaseEntity;
import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "pass")
public class PassEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int passSeq;
    private int packageSeq;
    private String userId;
    private PassStatus status;
    private int remainingCount;
    private LocalDateTime startedAt;
    private LocalDateTime  endedAt;
    private LocalDateTime  expiredAt;
}
