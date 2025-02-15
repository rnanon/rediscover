package com.rahul.rediscover.service;

import org.springframework.stereotype.Component;

import java.util.NoSuchElementException;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class KeyValueServiceImpl implements KeyValueService {

    ConcurrentHashMap<String, String> kvStore = new ConcurrentHashMap<>();

    @Override
    public void set(String key, String value) {
        kvStore.put(key,value);
    }

    @Override
    public String get(String key) {
        if (!kvStore.containsKey(key)) {
            throw new NoSuchElementException("Key not found: " + key);
        }
        return kvStore.get(key);
    }

    @Override
    public void delete(String key) {
        if (!kvStore.containsKey(key)) {
            throw new NoSuchElementException("Key not found: " + key);
        }

        kvStore.remove(key);
    }
}
