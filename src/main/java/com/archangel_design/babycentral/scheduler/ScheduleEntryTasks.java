package com.archangel_design.babycentral.scheduler;

import com.archangel_design.babycentral.processor.ScheduleEntryProcessor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class ScheduleEntryTasks {

    private final ScheduleEntryProcessor processor;

    public ScheduleEntryTasks(final ScheduleEntryProcessor processor) {
        this.processor = processor;
    }

    @Scheduled(fixedDelayString = "PT5S")
    public void proceessScheduleEntries() {
        processor.resendAlertsForHighPriorityScheduleEntries();
        processor.sendNotificationsForScheduleEntries();
    }
}