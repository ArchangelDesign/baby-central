/*
 * Baby Central
 * Copyright (c) 2018.
 * Rafal Martinez-Marjanski
 */

package com.archangel_design.babycentral.request;

import com.archangel_design.babycentral.enums.ScheduleEntryPriority;
import com.archangel_design.babycentral.enums.ScheduleEntryRepeatType;
import com.archangel_design.babycentral.enums.ScheduleEntryType;
import lombok.Getter;

import java.sql.Time;
import java.time.Instant;

@Getter
public class ScheduleEntryRequest {
    private String title;
    private ScheduleEntryType type;
    private Time start;
    private Time stop;
    private ScheduleEntryPriority priority;
    private ScheduleEntryRepeatType repeatType;
    private Instant startDate;
    private Instant endDate;
}
