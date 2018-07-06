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
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.sql.Time;
import java.time.Instant;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;

@Entity
@Getter
@Setter
@Table(name = "schedule_entries")
public class ScheduleEntryEntity {

    @GeneratedValue
    @Id
    @Setter(AccessLevel.NONE)
    private Long id;

    @Column(length = 36)
    @Setter(AccessLevel.NONE)
    private String uuid = UUID.randomUUID().toString();

    @Column(length = 120)
    private String title;

    @Enumerated(value = EnumType.STRING)
    private ScheduleEntryType type = ScheduleEntryType.BREAKFAST;

    @Setter(AccessLevel.NONE)
    private Time start;

    @Setter(AccessLevel.NONE)
    private Time stop;

    @Setter(AccessLevel.NONE)
    private Date startDate;

    @Setter(AccessLevel.NONE)
    private Date endDate;

    @Enumerated(EnumType.STRING)
    private ScheduleEntryPriority priority;

    @Enumerated(EnumType.STRING)
    private ScheduleEntryRepeatType repeatType;

    @JsonIgnore
    @JoinColumn(name = "schedule_id")
    @ManyToOne(targetEntity = ScheduleEntity.class, optional = false)
    private ScheduleEntity schedule;

    private long ilePrzedEventemMaMniePoinformowac = 600000; // millis

    private boolean isHighPriorityAlertActive = false;

    @Setter(AccessLevel.NONE)
    private Instant lastNotificationDate;

    @Setter(AccessLevel.NONE)
    private Time notificationStart;

    @Setter(AccessLevel.NONE)
    private Instant notificationStartDate;

    @Setter(AccessLevel.NONE)
    private Instant notificationEndDate;

    public Function<GenericRepository, ScheduleEntryEntity> recordNotificationSend() {
        this.lastNotificationDate = Instant.now();

        return genericRepository -> genericRepository.save(this);
    }

    public OrganizationEntity getCorrespondingUserOrganization() {
       return getCorrespondingUser().getOrganization();
    }

    public UserEntity getCorrespondingUser() {
        return schedule.getUser();
    }

    public void setIlePrzedEventemMaMniePoinformowac(
            final long ilePrzedEventemMaMniePoinformowac
    ) {
        this.ilePrzedEventemMaMniePoinformowac = ilePrzedEventemMaMniePoinformowac;
        updateNotificationTimestamp();
    }

    public void updateTimestamp(
            final Optional<Time> start,
            final Optional<Time> stop,
            final Optional<Date> startDate,
            final Optional<Date> endDate
    ) {
        start.ifPresent(s -> this.start = s);
        stop.ifPresent(s -> this.stop = s);
        startDate.ifPresent(s -> this.startDate = s);
        endDate.ifPresent(e -> this.endDate = e);

        updateNotificationTimestamp();
    }

    private void updateNotificationTimestamp() {
        final LocalTime notificationStartAsLocalTime = start.toLocalTime()
                .minus(ilePrzedEventemMaMniePoinformowac, ChronoUnit.MILLIS);

        notificationStart = Time.valueOf(notificationStartAsLocalTime);

        notificationStartDate =
                startDate.toInstant()
                        .truncatedTo(ChronoUnit.DAYS)
                        .plusSeconds(notificationStartAsLocalTime.toSecondOfDay());

        notificationEndDate =
                endDate.toInstant()
                        .truncatedTo(ChronoUnit.DAYS)
                        .plusSeconds(notificationStartAsLocalTime.toSecondOfDay());

    }
}
