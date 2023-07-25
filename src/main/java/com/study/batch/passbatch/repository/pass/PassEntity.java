package com.study.batch.passbatch.repository.pass;


import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@Entity
@Table(name="pass")
public class PassEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int  passSeq;
    private int packageSeq;
    private String userId;
    private String status;
    private int remainingCount;
    private LocalDateTime startedAt;
    private LocalDateTime endedAt;
    private LocalDateTime expiredAt;

}
