package com.example.catphoto.application

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.springframework.http.*
import org.springframework.web.client.RestTemplate

class HttpClientHelper(
    var apiKey: String
) {

    companion object {
        val restTemplate = RestTemplate()
        val headers = HttpHeaders().apply {
            accept = listOf(MediaType.APPLICATION_JSON)
            contentType = MediaType.APPLICATION_JSON
        }
    }

    inline fun <reified K> generateHttpEntity(body: K): HttpEntity<String> {
        headers.set("x-api-key", apiKey)
        return HttpEntity(if (body is String) "" else Json.encodeToString(body), headers)
    }

    inline fun <reified T, reified K> connect(method: HttpMethod, url: String, body: K): ResponseEntity<T> {
        val httpEntity: HttpEntity<String> = this.generateHttpEntity(body = body)
        return restTemplate.exchange(
            url, method, httpEntity,
            T::class.java
        )
    }

    inline fun <reified T, reified K> get(url: String, body: K): ResponseEntity<T> {
        return connect(HttpMethod.GET, url, body)
    }

    inline fun <reified T, reified K> post(url: String, body: K): ResponseEntity<T> {
        return connect(HttpMethod.POST, url, body)
    }

    inline fun <reified T, reified K> delete(url: String, body: K): ResponseEntity<T> {
        return connect(HttpMethod.DELETE, url, body)
    }
}