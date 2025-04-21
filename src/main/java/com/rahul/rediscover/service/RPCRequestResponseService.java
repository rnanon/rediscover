package com.rahul.rediscover.service;


import com.rahul.rediscover.common.ByteCodeRequestUtils;
import com.rahul.rediscover.common.ByteCodeResponseUtils;
import com.rahul.rediscover.socket.protocol.dto.RequestResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.NoSuchElementException;

@Component
@RequiredArgsConstructor
@Slf4j
public class RPCRequestResponseService implements RequestResponseService {

    private final KeyValueService keyValueService;

    @Override
    public RequestResponse getResponse(RequestResponse request) {
        log.info("Request {}", request);
        try {
            return switch (request.opcode()) {
                case ByteCodeRequestUtils.GET -> get(request);
                case ByteCodeRequestUtils.SET -> set(request);
                case ByteCodeRequestUtils.DELETE -> delete(request);
                default -> new RequestResponse(ByteCodeResponseUtils.INVALID_REQUEST, null, null);
            };
        } catch (NoSuchElementException e) {
            return new RequestResponse(ByteCodeResponseUtils.KEY_NOT_FOUND, null, null);
        }
    }

    private RequestResponse get(RequestResponse request) {
        String value = keyValueService.get(request.key());
        return new RequestResponse(ByteCodeResponseUtils.SUCCESS, request.key(), value);
    }

    private RequestResponse set(RequestResponse request) {
        keyValueService.set(request.key(), request.value());
        return new RequestResponse(ByteCodeResponseUtils.SUCCESS, request.key(), null);
    }

    private RequestResponse delete(RequestResponse request) {
        keyValueService.delete(request.key());
        return new RequestResponse(ByteCodeResponseUtils.SUCCESS, request.key(), null);
    }
}
