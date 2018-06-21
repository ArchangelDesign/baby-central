package com.archangel_design.babycentral.request;

import lombok.Getter;

@Getter
public class CreateShoppingCardEntryRequest {
    private String articleName;
    private Integer quantity;
}
