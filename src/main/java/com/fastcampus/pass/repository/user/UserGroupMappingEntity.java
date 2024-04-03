package com.fastcampus.pass.repository.user;

import com.fastcampus.pass.repository.BaseEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;


@Getter
@Setter
@ToString
@Table(name = "user_group_mapping")
@IdClass(UserGroupMappingId.class) // 복합키이기 때문에 따로 묶어서 선언해줌
public class UserGroupMappingEntity extends BaseEntity {

    @Id
    private String userGroupId;

    @Id
    private String userId;
    private String userGroupName;
    private String description;
}
