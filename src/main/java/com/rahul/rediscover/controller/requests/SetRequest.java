package com.rahul.rediscover.controller.requests;

import lombok.Data;
import lombok.NonNull;

@Data
public class SetRequest {
    @NonNull
    private final String key;
    @NonNull
    private final String value;
}
