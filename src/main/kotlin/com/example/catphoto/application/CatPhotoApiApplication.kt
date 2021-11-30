package com.example.catphoto.application

import com.example.catphoto.application.dto.CatPhotoDto
import com.example.catphoto.application.dto.CatPhotoFavoriteRetrieveDto
import com.example.catphoto.application.dto.CatPhotoFavoriteRetrieveResponseDto
import com.example.catphoto.application.dto.CatPhotoFavoriteSaveRequestDto
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.slf4j.LoggerFactory
import org.springframework.http.*
import org.springframework.stereotype.Service
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.client.RestTemplate

@Service
class CatPhotoApiApplication {
    private val log = LoggerFactory.getLogger(javaClass)

    companion object {
        const val MAX_NUMBER = 1000
        const val PAGINATION_CRITERIA = 100
        const val SINGLE_FAVOURITE_ID = "only-one-favorite"
    }

    fun retreive(number: Int): List<CatPhotoDto> {
        if (number > MAX_NUMBER) {
            throw RuntimeException("Max requestable number is ${MAX_NUMBER}, requested: $number")
        }

        val remain = number % PAGINATION_CRITERIA
        val pages = number / PAGINATION_CRITERIA
        log.info("{} {} {}", number, remain, pages)
        val pagination: MutableList<Int> = mutableListOf()
        for (i in 0..pages) {
            if (i == pages) {
                if (remain != 0) {
                    pagination.add(remain)
                }
            } else {
                pagination.add(PAGINATION_CRITERIA)
            }
        }
        log.info("{}", pagination)



        val headers = HttpHeaders()
        headers.accept = listOf(MediaType.APPLICATION_JSON);
        headers.contentType = MediaType.APPLICATION_JSON
        headers.set("X-API-KEY", "4e2f6fc0-80a7-4158-afd2-88f84fcc4e83")

        val entity = HttpEntity("", headers)

        val restTemplate = RestTemplate()

        val response = pagination.mapIndexed { index, value ->
            val uri = "https://api.thecatapi.com/v1/images/search?size=small&limit=${value}&page=${index}"
            val respEntity: ResponseEntity<Array<CatPhotoDto>> = restTemplate.exchange(
                uri, HttpMethod.GET, entity,
                Array<CatPhotoDto>::class.java
            )
            val userArray: List<CatPhotoDto> = respEntity.body?.toList()
                ?: throw RuntimeException("No result from API server")

//            val result = restTemplate.getForObject(uri, String::class.java)
            println(userArray.size)
            return userArray
        }
        return response
    }

    fun favoriteRetrieve(): List<CatPhotoFavoriteRetrieveDto> {
        val headers = HttpHeaders()
        headers.accept = listOf(MediaType.APPLICATION_JSON);
        headers.contentType = MediaType.APPLICATION_JSON
        headers.set("x-api-key", "4e2f6fc0-80a7-4158-afd2-88f84fcc4e83")

        val entity = HttpEntity("", headers)

        val restTemplate = RestTemplate()
        val uri = "https://api.thecatapi.com/v1/favourites?sub_id=${SINGLE_FAVOURITE_ID}"
        val respEntity: ResponseEntity<Array<CatPhotoFavoriteRetrieveDto>> = restTemplate.exchange(
            uri, HttpMethod.GET, entity,
            Array<CatPhotoFavoriteRetrieveDto>::class.java
        )
        val favoriteResponse: List<CatPhotoFavoriteRetrieveDto> = respEntity.body?.toList()
            ?: throw RuntimeException("No result from API server")

        println(favoriteResponse)
        return favoriteResponse
    }


    fun favoriteSave(ids: List<String>) {
        val headers = HttpHeaders()
        // headers.accept = listOf(MediaType.APPLICATION_JSON);
        headers.contentType = MediaType.APPLICATION_JSON
        headers.set("x-api-key", "4e2f6fc0-80a7-4158-afd2-88f84fcc4e83")

        val requests: List<CatPhotoFavoriteSaveRequestDto> = ids.map {
            CatPhotoFavoriteSaveRequestDto(imageId = it, subId = SINGLE_FAVOURITE_ID)
        }

        for (eachRequest: CatPhotoFavoriteSaveRequestDto in requests) {
            val entity = HttpEntity(Json.encodeToString(eachRequest), headers)

            val restTemplate = RestTemplate()

            val uri = "https://api.thecatapi.com/v1/favourites"
            val respEntity: ResponseEntity<CatPhotoFavoriteRetrieveResponseDto> = try { restTemplate.exchange(
                uri, HttpMethod.POST, entity,
                CatPhotoFavoriteRetrieveResponseDto::class.java
            ) } catch (e: Exception) {
                if (e.message?.contains("DUPLICATE_FAVOURITE") == true) {
                    throw RuntimeException("Greedy! Already exists, you can only non-exists cat on favorite.")
                } else {
                    throw RuntimeException(e.message)
                }
            }
            val response: CatPhotoFavoriteRetrieveResponseDto = respEntity.body
                ?: throw RuntimeException("No result from API server")

            println(response)
        }
    }

    fun favoriteDelete(ids: List<Int>) {
        val headers = HttpHeaders()
        // headers.accept = listOf(MediaType.APPLICATION_JSON);
        headers.contentType = MediaType.APPLICATION_JSON
        headers.set("x-api-key", "4e2f6fc0-80a7-4158-afd2-88f84fcc4e83")

        for (id in ids) {
            val entity = HttpEntity("", headers)

            val restTemplate = RestTemplate()

            val uri = "https://api.thecatapi.com/v1/favourites/${id}"
            val respEntity: ResponseEntity<String> = restTemplate.exchange(
                uri, HttpMethod.DELETE, entity,
                String::class.java
            )
            val response: String = respEntity.body
                ?: throw RuntimeException("No result from API server")

            println(response)
        }
    }
}