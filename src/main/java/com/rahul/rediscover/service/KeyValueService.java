package com.rahul.rediscover.service;

public interface KeyValueService {

    void set(String key, String value);

    String get(String key);

    void delete(String key);
}
