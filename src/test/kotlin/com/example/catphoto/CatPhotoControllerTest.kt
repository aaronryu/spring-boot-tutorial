package com.example.catphoto

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.util.LinkedMultiValueMap
import org.springframework.web.util.NestedServletException


@AutoConfigureMockMvc
@SpringBootTest
class CatPhotoControllerTest {
    companion object {
        const val GENERATE_CAT_NUMBER = 3
        const val OVER_MAX_GENERATE_CAT_NUMBER = 1001
    }

    @Autowired
    lateinit var mockMvc: MockMvc

    @Test
    fun 야옹_고앵이_생성기_1_구동_테스트() {
        val url = "/cat/generate"
        mockMvc.perform(
            MockMvcRequestBuilders.get(url)
                .queryParams(
                    LinkedMultiValueMap<String, String>().apply {
                        add("number", GENERATE_CAT_NUMBER.toString())
                    }
                )
        )
            .andExpect(status().isOk)
            // 1. total number
            .andExpect(jsonPath("$.length()").value(GENERATE_CAT_NUMBER))
            // 2. property existence
            .andExpect(jsonPath("$..['id']").exists())
            .andExpect(jsonPath("$..['url']").exists())
            .andExpect(jsonPath("$..['width']").exists())
            .andExpect(jsonPath("$..['height']").exists())
    }

    @Test
    fun 야옹_고앵이_생성기_2_최대치_초과_테스트() {
        val url = "/cat/generate"
        val servletWrapperException = assertThrows(NestedServletException::class.java) {
            mockMvc.perform(
                MockMvcRequestBuilders.get(url)
                    .queryParams(
                        LinkedMultiValueMap<String, String>().apply {
                            add("number", OVER_MAX_GENERATE_CAT_NUMBER.toString())
                        }
                    )
            )
        }
        assertThat(servletWrapperException.message).contains("RuntimeException")
    }
}