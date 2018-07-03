package com.archangel_design.babycentral.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class UnreachableResourceException extends Exception {

    public UnreachableResourceException(final String message) {
        super(message);
    }
}
