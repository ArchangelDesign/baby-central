package com.archangel_design.babyscheduller.entity;

import com.archangel_design.babyscheduller.enums.Gender;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "babies")
public class BabyEntity {

    @Id
    @GeneratedValue
    private Long id;

    @Column(length = 80)
    private String name;

    private Date birthday;

    private Gender gender;

    public Long getId() {
        return id;
    }

    public BabyEntity setId(Long id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public BabyEntity setName(String name) {
        this.name = name;
        return this;
    }

    public Date getBirthday() {
        return birthday;
    }

    public BabyEntity setBirthday(Date birthday) {
        this.birthday = birthday;
        return this;
    }

    public Gender getGender() {
        return gender;
    }

    public BabyEntity setGender(Gender gender) {
        this.gender = gender;
        return this;
    }
}
