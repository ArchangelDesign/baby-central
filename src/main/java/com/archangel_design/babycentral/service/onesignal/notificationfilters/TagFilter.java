package com.archangel_design.babycentral.service.onesignal.notificationfilters;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class TagFilter implements NotificatonFilter {
    private final String field = "tag";
    private final String relation = "=";
    private final String key;
    private final String value;
}
