package com.jakubbone.version_info_service.integration;

import com.jakubbone.controller.InfoController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

// @WebMvcTest -> server running no required for response properties testing
@WebMvcTest(InfoController.class)
class InfoControllerTest {
	@Test
	void shouldReturnApplicationVersion(@Autowired MockMvc mockMvc) throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.get("/api/info"))
				.andDo(MockMvcResultHandlers.print())
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.content().contentType(
						"application/json"))
				.andExpect(jsonPath("$.version").isString());
	}

	@Test
	void shouldReturnApplicationUptime(@Autowired MockMvc mockMvc) throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.get("/api/uptime"))
				.andDo(MockMvcResultHandlers.print())
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.content().contentType(
						"application/json"))
				.andExpect(jsonPath("$.uptime").isNumber())
				.andExpect(jsonPath("$.uptime", greaterThanOrEqualTo(0)));
	}
}
