/*
 * Baby Central
 * Copyright (c) 2018.
 * Rafal Martinez-Marjanski
 */

package com.archangel_design.babycentral.service.onesignal;

import com.archangel_design.babycentral.configuration.OneSignalConfiguration;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class OneSignalService{

    private final RestTemplate restTemplate;

    private final OneSignalConfiguration configuration;

    public OneSignalService(final RestTemplate restTemplate,
                            final OneSignalConfiguration configuration) {
        this.restTemplate = restTemplate;
        this.configuration = configuration;
    }
    public void sendPushNotification(final OneSignalPushNotification notification) {
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.CONTENT_TYPE, "application/json");
        headers.set(
                HttpHeaders.AUTHORIZATION,
                "Basic " + configuration.getAuthorizationId()
        );

        HttpEntity<OneSignalPushNotification> entity =
                new HttpEntity<>(notification, headers);

        restTemplate.exchange(
                "https://onesignal.com/api/v1/notifications",
                HttpMethod.POST,
                entity,
                String.class
        );
    }
}
