package com.fastcampus.pass.repository.booking;

import com.fastcampus.pass.repository.BaseEntity;

import javax.persistence.*;

@Entity
@Table(name = "booking")
public class BookingEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer bookingSeq;
    private Integer passSeq;
    private String userId;
    @Enumerated(EnumType.STRING)
    private BookingStatus status;
    private Integer usedPass;
    private Integer attended;
    private Integer startedAt;
    private Integer endedAt;
    private Integer cancelledAt;


}
