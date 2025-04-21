package com.rahul.rediscover.socket.protocol;

import com.rahul.rediscover.common.ByteCodeResponseUtils;
import com.rahul.rediscover.socket.protocol.dto.RequestResponse;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

@Slf4j
public class RequestParser {

    public static RequestResponse parse(BufferedInputStream inputStream) {
        try {
            inputStream.mark(1024);
            int opcode = inputStream.read();

            if (opcode < 0)
                throw new IOException("Illegal Opcode");


            byte[] keyLengthBytes = new byte[4];

            if (inputStream.read(keyLengthBytes) != 4) {
                inputStream.reset();
                throw new IOException("Key length not valid");
            }

            int keyLength = ByteBuffer.wrap(keyLengthBytes).getInt();

            if (keyLength <= 0) {
                inputStream.reset();
                throw new IOException("Key length not valid");
            }

            byte[] keyBytes = new byte[keyLength];
            if (inputStream.read(keyBytes) != keyLength) {
                inputStream.reset();
                throw new IOException("Couldn't read key");
            }


            byte[] valueLengthBytes = new byte[4];

            if (inputStream.read(valueLengthBytes) != 4) {
                inputStream.reset();
                throw new IOException("Value length not valid");
            }

            int valueLength = ByteBuffer.wrap(valueLengthBytes).getInt();
            byte[] valueBytes = new byte[valueLength];
            if (inputStream.read(valueBytes) != valueLength) {
                inputStream.reset();
                throw new IOException("Couldn't read value");
            }

            return new RequestResponse((byte) opcode, new String(keyBytes), new String(valueBytes));
        } catch (IOException e) {
            log.error("Error parsing request", e);
            return new RequestResponse(ByteCodeResponseUtils.INVALID_REQUEST, null, null);
        }
    }
}
