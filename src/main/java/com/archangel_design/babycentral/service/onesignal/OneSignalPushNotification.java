package com.archangel_design.babycentral.service.onesignal;

import com.archangel_design.babycentral.enums.Language;
import com.archangel_design.babycentral.enums.ScheduleEntryPriority;
import com.archangel_design.babycentral.enums.ScheduleEntryRepeatType;
import com.archangel_design.babycentral.request.ScheduleEntryAlertAnswerRequest;
import com.archangel_design.babycentral.service.onesignal.notificationfilters.NotificatonFilter;
import com.archangel_design.babycentral.service.onesignal.notificationfilters.TagFilter;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.sql.Time;
import java.util.*;

@Getter
public class OneSignalPushNotification {

    @JsonProperty("app_id")
    private final String appId;
    private final List<NotificatonFilter> filters = new ArrayList<>();
    private final HashMap<Language, String> contents = new HashMap<>();
    @JsonProperty("included_segments")
    private final List<String> includedSegments = Arrays.asList("All");
    private final NotificationData data;

    private OneSignalPushNotification(
            final String appId,
            final ScheduleEntryPriority priority,
            final String scheduleEntryUuid
    ) {
        this.appId = appId;
        this.contents.put(Language.en, "");
        this.data = new NotificationData(priority, scheduleEntryUuid);
    }

    public static OneSignalPushNotification createNotificationForOrganization(
            final String appId,
            final String organizationId,
            final ScheduleEntryPriority priority,
            final String scheduleEntryUuid
    ) {
        OneSignalPushNotification notification =
                new OneSignalPushNotification(appId, priority, scheduleEntryUuid);
        notification.filters.add(new TagFilter("organization-id", organizationId));

        return notification;
    }

    public static OneSignalPushNotification createNotificationForUser(
            final String appId,
            final String userId,
            final ScheduleEntryPriority priority,
            final String scheduleEntryUuid
    ) {
        OneSignalPushNotification notification =
                new OneSignalPushNotification(appId, priority, scheduleEntryUuid);
        notification.filters.add(new TagFilter("user-id", userId));

        return notification;
    }

    public void addMessage(final Language language, final String message) {
        contents.put(language, message);
    }

    @Getter
    @NoArgsConstructor
    public static class NotificationData {

        private ScheduleEntryPriority priority;
        private String scheduleEntryUuid;

        private NotificationData(
                final ScheduleEntryPriority priority,
                final String scheduleEntryUuid
        ) {
            this.priority = priority;
            this.scheduleEntryUuid = scheduleEntryUuid;
        }
    }
}
