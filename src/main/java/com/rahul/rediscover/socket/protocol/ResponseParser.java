package com.rahul.rediscover.socket.protocol;


import com.rahul.rediscover.common.ByteCodeResponseUtils;
import com.rahul.rediscover.socket.protocol.dto.RequestResponse;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Objects;

@Slf4j
public class ResponseParser {

    public static byte[] parse(RequestResponse response) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);

        try {
            dos.write(response.opcode());

            if (Objects.nonNull(response.value())) {
                dos.write(response.key().length());
                dos.write(response.value().getBytes());
            }

            dos.flush();
            return baos.toByteArray();
        } catch (IOException e) {
            log.error("Error in parsing response", e);
            return new byte[]{ByteCodeResponseUtils.INTERNAL_ERROR};
        }
    }
}
