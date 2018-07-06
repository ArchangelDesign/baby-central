/*
 * Baby Central
 * Copyright (c) 2018.
 * Rafal Martinez-Marjanski
 */

package com.archangel_design.babycentral.entity;

import com.archangel_design.babycentral.enums.Gender;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Table(name = "babies")
@JsonIgnoreProperties(value = {"id", "avatar", "schedules"})
public class BabyEntity {

    @Id
    @GeneratedValue
    private Long id;

    @Column(length = 36)
    private String uuid = UUID.randomUUID().toString();

    @Column(length = 80)
    private String name;

    private Date birthday;

    private Gender gender;

    @Lob
    @Column(columnDefinition = "mediumblob")
    private byte[] avatar = new byte[0];

    @OneToMany(
            targetEntity = ScheduleEntity.class,
            cascade = CascadeType.ALL,
            mappedBy = "baby"
    )
    private List<ScheduleEntity> schedules = new ArrayList<>();

    public BabyEntity setName(String name) {
        this.name = name;
        return this;
    }

    public BabyEntity setBirthday(Date birthday) {
        this.birthday = birthday;
        return this;
    }

    public BabyEntity setGender(Gender gender) {
        this.gender = gender;
        return this;
    }

    public void setAvatar(byte[] avatar) {
        this.avatar = avatar;
    }
}
