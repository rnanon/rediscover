package com.rahul.rediscover.controller;

import com.rahul.rediscover.controller.requests.SetRequest;
import com.rahul.rediscover.service.KeyValueService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/key-value")
@RequiredArgsConstructor
public class KeyValueController {

    private final KeyValueService service;

    @PostMapping
    public ResponseEntity<Void> setValue(@RequestBody SetRequest request){
        service.set(request.getKey(), request.getValue());
        return ResponseEntity.ok().build();
    }


    @GetMapping
    public ResponseEntity<String> getValue(@RequestParam String key){
        String value = service.get(key);
        return ResponseEntity.ok(value);
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteValue(@RequestParam String key){
        service.delete(key);
        return ResponseEntity.ok().build();
    }
}
