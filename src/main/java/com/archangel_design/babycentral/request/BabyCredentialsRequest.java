package com.archangel_design.babycentral.request;

import com.archangel_design.babycentral.enums.Gender;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.util.Date;

@Getter
public class BabyCredentialsRequest {

    private final String name;
    private final Date birthday;
    private final Gender gender;

    @JsonCreator
    public BabyCredentialsRequest(
            @JsonProperty("name") final String name,
            @JsonProperty("birthday") final Date birthday,
            @JsonProperty("gender") final String gender
    ) {
        this.name = name;
        this.birthday = birthday;
        this.gender = Gender.fromString(gender);
    }
}
