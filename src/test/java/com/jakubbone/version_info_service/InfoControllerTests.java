package com.jakubbone.version_info_service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc
class InfoControllerTests {
	@Autowired
	MockMvc mockMvc;

	@Test
	void shouldReturnApplicationVersion() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.get("/api/info"))
				.andDo(MockMvcResultHandlers.print())
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.content().contentType(
						"application/json"))
				.andExpect(jsonPath("$.version").isString());
	}

	@Test
	void shouldReturnApplicationUptime() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.get("/api/uptime"))
				.andDo(MockMvcResultHandlers.print())
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.content().contentType(
						"application/json"))
				.andExpect(jsonPath("$.uptime").isNumber())
				.andExpect(jsonPath("$.uptime", greaterThanOrEqualTo(0)));
	}

}
