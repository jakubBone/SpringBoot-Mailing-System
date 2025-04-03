package com.jakubbone.version_info_service.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class InfoController {
    @Value("${spring.application.version}")
    private String version;

    @GetMapping("/info")
    public Map<String, String> getAppInfo(){
        return Collections.singletonMap("version", version);
    }
}
