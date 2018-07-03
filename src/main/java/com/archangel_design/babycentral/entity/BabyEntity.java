/*
 * Baby Central
 * Copyright (c) 2018.
 * Rafal Martinez-Marjanski
 */

package com.archangel_design.babycentral.entity;

import com.archangel_design.babycentral.enums.Gender;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "babies")
@JsonIgnoreProperties(value = {"schedules"})
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
    @JsonIgnore
    private byte[] avatar = new byte[0];

    @OneToMany(
            targetEntity = ScheduleEntity.class,
            cascade = CascadeType.ALL,
            mappedBy = "baby"
    )
    private List<ScheduleEntity> schedules = new ArrayList<>();

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

    public String getUuid() {
        return uuid;
    }

    public BabyEntity setUuid(String uuid) {
        this.uuid = uuid;
        return this;
    }

    public byte[] getAvatar() {
        return avatar;
    }

    public void setAvatar(byte[] avatar) {
        this.avatar = avatar;
    }

    public List<ScheduleEntity> getSchedules() {
        return schedules;
    }
}
