package com.archangel_design.babycentral.service.onesignal;

import com.archangel_design.babycentral.enums.Language;
import com.archangel_design.babycentral.enums.ScheduleEntryPriority;
import com.archangel_design.babycentral.enums.ScheduleEntryRepeatType;
import com.archangel_design.babycentral.service.onesignal.notificationfilters.NotificatonFilter;
import com.archangel_design.babycentral.service.onesignal.notificationfilters.TagFilter;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
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

    private OneSignalPushNotification(final String appId) {
        this.appId = appId;
        this.contents.put(Language.en, "");
    }

    public static OneSignalPushNotification createNotificationForOrganization(
            final String appId,
            final String organizationId
    ) {
        OneSignalPushNotification notification = new OneSignalPushNotification(appId);
        notification.filters.add(new TagFilter("organization-id", organizationId));

        return notification;
    }

    public static OneSignalPushNotification createNotificationForUser(
            final String appId,
            final String userId
    ) {
        OneSignalPushNotification notification = new OneSignalPushNotification(appId);
        notification.filters.add(new TagFilter("user-id", userId));

        return notification;
    }

    public void addMessage(final Language language, final String message) {
        contents.put(language, message);
    }
}
