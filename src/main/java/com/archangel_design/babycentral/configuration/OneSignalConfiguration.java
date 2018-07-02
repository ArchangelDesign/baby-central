package com.archangel_design.babycentral.configuration;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
public class OneSignalConfiguration {

    @Value("${one-signal.appId}")
    private String appId;

    @Value("${one-signal.authorizationId}")
    private String authorizationId;
}
