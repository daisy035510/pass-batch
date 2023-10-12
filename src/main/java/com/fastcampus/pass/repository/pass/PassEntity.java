package com.fastcampus.pass.repository.pass;

import com.fastcampus.pass.repository.BaseEntity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.Date;

@Entity
public class PassEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int passSeq;
    private int packageSeq;
    private String userId;
    private String status;
    private int remainingCount;
    private Date startedAt;
    private Date endedAt;
    private Date expiredAt;
}
