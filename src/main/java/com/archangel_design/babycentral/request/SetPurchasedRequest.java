package com.archangel_design.babycentral.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class SetPurchasedRequest {

    boolean isPurchased;

    @JsonCreator
    public SetPurchasedRequest(@JsonProperty("isPurchased") final boolean isPurchased) {
        this.isPurchased = isPurchased;
    }
}
