package com.jakubbone.version_info_service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@SpringBootTest
@AutoConfigureMockMvc
class VersionInfoServiceApplicationTests {

	@Autowired
	MockMvc mockMvc;

	@Value("${spring.application.version}")
	private String version;

	@Test
	void shouldReturnApplicationVersion() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.get("/api/info"))
				.andDo(MockMvcResultHandlers.print())
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.content().contentType(
						"application/json"))
				.andExpect(MockMvcResultMatchers.content().json("{\"version\":\"" + version + "\"}"));
	}

}
