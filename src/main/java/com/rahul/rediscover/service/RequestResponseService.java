package com.rahul.rediscover.service;


import com.rahul.rediscover.socket.protocol.dto.RequestResponse;

public interface RequestResponseService {
    RequestResponse getResponse(RequestResponse request);
}
