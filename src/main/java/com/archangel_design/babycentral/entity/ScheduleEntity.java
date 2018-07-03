/*
 * Baby Central
 * Copyright (c) 2018.
 * Rafal Martinez-Marjanski
 */

package com.archangel_design.babycentral.entity;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "schedule")
public class ScheduleEntity {
    @Id
    @GeneratedValue
    private Long id;

    @Column(length = 120)
    private String name;

    @Column(length = 36)
    private String uuid = UUID.randomUUID().toString();

    @ManyToOne(targetEntity = BabyEntity.class, optional = false)
    @JoinColumn(name = "baby_id")
    private BabyEntity baby;

    @ManyToOne(targetEntity = UserEntity.class, optional = false)
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @OneToMany(
            targetEntity = ScheduleEntryEntity.class,
            cascade = CascadeType.ALL,
            mappedBy = "schedule"
    )
    private List<ScheduleEntryEntity> entries = new ArrayList<>();

    public Long getId() {
        return id;
    }

    public ScheduleEntity setId(Long id) {
        this.id = id;
        return this;
    }

    public BabyEntity getBaby() {
        return baby;
    }

    public ScheduleEntity setBaby(BabyEntity baby) {
        this.baby = baby;
        return this;
    }

    public UserEntity getUser() {
        return user;
    }

    public ScheduleEntity setUser(UserEntity user) {
        this.user = user;
        return this;
    }

    public String getName() {
        return name;
    }

    public ScheduleEntity setName(String name) {
        this.name = name;
        return this;
    }

    public String getUuid() {
        return uuid;
    }

    public ScheduleEntity setUuid(String uuid) {
        this.uuid = uuid;
        return this;
    }

    public List<ScheduleEntryEntity> getEntries() {
        return entries;
    }

    public ScheduleEntity setEntries(List<ScheduleEntryEntity> entries) {
        this.entries = entries;
        return this;
    }
}
