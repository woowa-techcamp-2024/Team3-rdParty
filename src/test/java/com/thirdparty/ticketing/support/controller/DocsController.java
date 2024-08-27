package com.thirdparty.ticketing.support.controller;

import java.util.HashMap;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test/docs")
public class DocsController {

    public record HelloRequest(String name) {}

    @GetMapping("/hello")
    public ResponseEntity<Map<String, String>> hello(@RequestParam("name") String name) {
        Map<String, String> map = new HashMap<>();
        map.put("hello", name);
        return ResponseEntity.ok(map);
    }

    @PostMapping("/hello/{test}")
    public ResponseEntity<Map<String, String>> hello2(
            @PathVariable("test") Long testVariable, @RequestBody HelloRequest request) {
        Map<String, String> map = new HashMap<>();
        map.put("hello", request.name);
        map.put("pathVariable", testVariable.toString());
        return ResponseEntity.ok(map);
    }
}