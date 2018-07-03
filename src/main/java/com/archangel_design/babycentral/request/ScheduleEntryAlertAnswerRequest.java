package com.archangel_design.babycentral.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class ScheduleEntryAlertAnswerRequest {

    private final String userUuid;

    @JsonCreator
    public ScheduleEntryAlertAnswerRequest(
            @JsonProperty("userUuid") final String userUuid
    ) {
        this.userUuid = userUuid;
    }
}
