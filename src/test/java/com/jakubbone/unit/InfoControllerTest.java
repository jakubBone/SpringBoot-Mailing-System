package com.jakubbone.unit;

import com.jakubbone.controller.InfoController;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;

import java.lang.management.ManagementFactory;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
public class InfoControllerTest {
    @InjectMocks
    private InfoController infoController;

    @Mock
    private Authentication authentication;

    @Test
    void shouldReturnCorrectVersion() {
        ReflectionTestUtils.setField(infoController, "version", "1.0.0-TEST");

        Map<String, String> result = infoController.getVersion();

        assertNotNull(result);
        assertEquals("1.0.0-TEST", result.get("version"));
    }

    @Test
    void shouldReturnUptime() {
        long uptimeInMillis = ManagementFactory.getRuntimeMXBean().getUptime();
        long uptimeInSeconds = uptimeInMillis / 1000;

        Map<String, Long> result = infoController.getUptime();

        assertNotNull(result);
        assertEquals(uptimeInSeconds, result.get("uptime"));
    }



}
