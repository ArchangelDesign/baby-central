package com.archangel_design.babycentral.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.Instant;

@Entity
@Getter
@Table(name = "high_priority_alert_response")
@NoArgsConstructor
public class HighPriorityAlertResponseEntity {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    private ScheduleEntryEntity scheduleEntry;

    @ManyToOne
    private UserEntity user;

    private Instant responseDate;

    public HighPriorityAlertResponseEntity(
            final ScheduleEntryEntity scheduleEntry,
            final UserEntity user
    ) {
        this.scheduleEntry = scheduleEntry;
        this.user = user;
        this.responseDate = Instant.now();
    }
}
