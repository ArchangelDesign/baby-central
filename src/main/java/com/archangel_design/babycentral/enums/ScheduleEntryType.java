/*
 * Baby Central
 * Copyright (c) 2018.
 * Rafal Martinez-Marjanski
 */

package com.archangel_design.babycentral.enums;

public enum ScheduleEntryType {
    SLEEP("Sleep %s - %s."),
    NAP("Nap %s - %s."),
    BREAKFAST("Breakfast %s."),
    LUNCH("Lunch %s."),
    DINNER("Dinner %s."),
    SUPPER("Supper %s."),
    INDOOR_PLAY("Indoor play %s - %s."),
    OUTDOOR_PLAY("Outdoor play %s - %s."),
    SNACK("Snack %s."),
    PRESCHOOL("Preschool %s - %s."),
    SCHOOL("School %s - %s."),
    DAYCARE("Daycare %s - %s.");

    private final String messageFormat;

    ScheduleEntryType(final String messageFormat) {
        this.messageFormat = messageFormat;
    }

    public String getMessageFormat() {
        return messageFormat;
    }
}
