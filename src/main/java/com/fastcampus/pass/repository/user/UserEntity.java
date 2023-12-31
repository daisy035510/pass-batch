package com.fastcampus.pass.repository.user;

import com.fastcampus.pass.repository.BaseEntity;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "user")
public class UserEntity extends BaseEntity {

    @Id
    private String userId;
    private String userName;
    private String status;
    private String phone;
    private String meta;

}
