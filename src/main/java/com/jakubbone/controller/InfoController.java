package com.jakubbone.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.lang.management.ManagementFactory;
import java.util.Collections;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class InfoController {
    @Value("${spring.application.version}")
    private String version;

    @GetMapping("/info")
    public Map<String, String> getVersion(){
        return Collections.singletonMap("version", version);
    }

    @GetMapping("/uptime")
    public Map<String, Long> getUptime(){
        long uptimeInMillis = ManagementFactory.getRuntimeMXBean().getUptime();
        long uptimeInSeconds = uptimeInMillis / 1000;
        return Collections.singletonMap("uptime", uptimeInSeconds);
    }
}
