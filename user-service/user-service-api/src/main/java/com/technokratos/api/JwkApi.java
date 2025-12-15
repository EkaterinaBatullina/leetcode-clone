package com.technokratos.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;

@RequestMapping("/.well-known")
public interface JwkApi {

    @GetMapping("/jwks.json")
    Map<String, Object> keys();
}