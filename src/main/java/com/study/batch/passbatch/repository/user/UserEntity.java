package com.study.batch.passbatch.repository.user;

import com.study.batch.passbatch.repository.BaseEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import net.bytebuddy.build.ToStringPlugin;

import javax.persistence.*;

@Getter
@Setter
@Entity
@ToString
@Table(name = "user")
public class UserEntity  extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int user_id;
    private String user_name;
    private String status;
    private String phone;
    private String meta;
}
