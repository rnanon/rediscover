package com.rahul.rediscover.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.NoSuchElementException;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Slf4j
public class KeyValueServiceImpl implements KeyValueService {

    ConcurrentHashMap<String, String> kvStore = new ConcurrentHashMap<>();

    @Override
    public void set(String key, String value) {
        kvStore.put(key, value);
        log.info("Map after set {}", kvStore);
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
        log.info("Map after delete {}", kvStore);
        kvStore.remove(key);
    }
}
