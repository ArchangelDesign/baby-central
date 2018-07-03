/*
 * Baby Central
 * Copyright (c) 2018.
 * Rafal Martinez-Marjanski
 */

package com.archangel_design.babycentral.entity;

import com.archangel_design.babycentral.enums.ScheduleEntryPriority;
import com.archangel_design.babycentral.enums.ScheduleEntryRepeatType;
import com.archangel_design.babycentral.enums.ScheduleEntryType;
import com.archangel_design.babycentral.repository.GenericRepository;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.sql.Time;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;
import java.util.function.Function;

@Entity
@Table(name = "schedule_entries")
@Getter
public class ScheduleEntryEntity {

    @Id
    @GeneratedValue
    private Long id;

    @Column(length = 36)
    private String uuid = UUID.randomUUID().toString();

    @Setter
    @Column(length = 120)
    private String title;

    @Setter
    @Enumerated(value = EnumType.STRING)
    private ScheduleEntryType type;

    @Setter
    private Time start;

    @Setter
    private Time stop;

    @Setter
    @Enumerated(EnumType.STRING)
    private ScheduleEntryPriority priority;

    @Setter
    @Enumerated(EnumType.STRING)
    private ScheduleEntryRepeatType repeatType;

    @Setter
    private Date startDate;

    @Setter
    private Date endDate;

    @Setter
    @JsonIgnore
    @ManyToOne(targetEntity = ScheduleEntity.class, optional = false)
    @JoinColumn(name = "schedule_id")
    private ScheduleEntity owner;

    // LOGIKA NOTYFIKACJI
    private Instant lastNotificationDate;
    ///

    // LOGIKA HIGH PRIOTITY ALERTÓW
    @Setter
    private boolean isHighPriorityAlertActive = false;
    ///

    public Function<GenericRepository, ScheduleEntryEntity> recordNotificationSend() {
        this.lastNotificationDate = Instant.now();

        return genericRepository -> genericRepository.save(this);
    }
}
