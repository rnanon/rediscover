package com.rahul.rediscover.common;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ByteCodeResponseUtils {

    public static final byte SUCCESS = 0x00;
    public static final byte KEY_NOT_FOUND = 0x01;
    public static final byte INVALID_REQUEST = 0x02;
    public static final byte INTERNAL_ERROR = 0x03;

}
