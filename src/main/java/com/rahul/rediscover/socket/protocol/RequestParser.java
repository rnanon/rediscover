package com.rahul.rediscover.socket.protocol;

import com.rahul.rediscover.socket.protocol.dto.RequestResponse;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

@Slf4j
public class RequestParser {

    public static RequestResponse parse(byte[] data){
        try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(data);
             DataInputStream dataInputStream = new DataInputStream(byteArrayInputStream)) {
            byte opcode = dataInputStream.readByte();
            int keyLength = dataInputStream.readInt();

            byte[] keyBytes = new byte[keyLength];
            dataInputStream.readFully(keyBytes);
            String key = new String(keyBytes);

            int valueLength = dataInputStream.readInt();
            String value = null;
            if (valueLength > 0) {
                byte[] valueBytes = new byte[valueLength];
                dataInputStream.readFully(valueBytes);
                value = new String(valueBytes);
            }

            return new RequestResponse(opcode, key, value);

        } catch (IOException e) {
            log.error("Error parsing request", e);
            throw new RuntimeException(e);
        }
    }
}
