package com.study.batch.passbatch.repository.packaze;


import com.study.batch.passbatch.repository.BaseEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

@Entity
@Getter
@Setter
@ToString
@Table(name = "package")
public class PackageEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int packageSeq;
    private String packageName;
    private int count;
    private int period;
}
