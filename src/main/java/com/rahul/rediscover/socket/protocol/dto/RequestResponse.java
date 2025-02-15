package com.rahul.rediscover.socket.protocol.dto;

public record RequestResponse(byte opcode, String key, String value) {
}
