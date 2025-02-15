package com.rahul.rediscover.common;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ByteCodeRequestUtils {

    public static final byte GET = 0x01;
    public static final byte SET = 0x02;
    public static final byte DELETE = 0x03;

}
