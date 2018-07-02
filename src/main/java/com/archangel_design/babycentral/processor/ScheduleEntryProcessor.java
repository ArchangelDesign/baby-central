package com.archangel_design.babycentral.processor;

import com.archangel_design.babycentral.configuration.OneSignalConfiguration;
import com.archangel_design.babycentral.entity.ScheduleEntryEntity;
import com.archangel_design.babycentral.enums.ScheduleEntryPriority;
import com.archangel_design.babycentral.repository.ScheduleRepository;
import com.archangel_design.babycentral.service.onesignal.OneSignalNotificationFactory;
import com.archangel_design.babycentral.service.onesignal.OneSignalPushNotification;
import com.archangel_design.babycentral.service.onesignal.OneSignalService;
import com.archangel_design.babycentral.service.ScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Objects;

@Service
public class ScheduleEntryProcessor {

    private final ScheduleService scheduleService;
    private final ScheduleRepository scheduleRepository;

    private final OneSignalService oneSignalService;
    private final OneSignalNotificationFactory oneSignalNotificationFactory;

    @Autowired
    public ScheduleEntryProcessor(
            final ScheduleService scheduleService,
            final ScheduleRepository scheduleRepository,
            final OneSignalService oneSignalService,
            final OneSignalConfiguration oneSignalConfiguration
    ){
        this.scheduleService = scheduleService;
        this.scheduleRepository = scheduleRepository;
        this.oneSignalService = oneSignalService;
        this.oneSignalNotificationFactory =
                new OneSignalNotificationFactory(oneSignalConfiguration);
    }

    public void sendNotificationsForScheduleEntries() {
        List<ScheduleEntryEntity> scheduleEntries =
                scheduleService.getEventsForNotificationSending();

        scheduleEntries.forEach(this::sendNotificationForScheduleEntry);
    }

    private void sendNotificationForScheduleEntry(
            final ScheduleEntryEntity scheduleEntry
    ) {
        switch (scheduleEntry.getPriority()) {
            case HIGH:
                sendPushNotificationForScheduleEntry(scheduleEntry);
                break;
            case MEDIUM:
                sendPushNotificationForScheduleEntry(scheduleEntry);
                break;
            case LOW:
                sendPushNotificationForScheduleEntry(scheduleEntry);
                break;
        }

        scheduleEntry.setLastNotificationDate(new Date());
        scheduleRepository.save(scheduleEntry);
    }

    private void sendPushNotificationForScheduleEntry(
            final ScheduleEntryEntity scheduleEntry
    ) {
        OneSignalPushNotification notification;

        if (Objects.isNull(scheduleEntry.getOwner().getUser().getOrganization()))
            notification =
                    oneSignalNotificationFactory.createPushNotificationForUser(scheduleEntry);
        else
            notification =
                oneSignalNotificationFactory.createPushNotificationForOrganization(scheduleEntry);

        try {
            oneSignalService.sendPushNotification(notification);
        } catch (Exception exception) {
            System.out.println("#################");
            exception.printStackTrace();
        }
    }
}
