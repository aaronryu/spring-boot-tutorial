package com.example.catphoto.application

import com.example.catphoto.application.dto.CatPhotoGenerateResponse
import com.example.catphoto.application.dto.CatPhotoFavoriteResponse
import com.example.catphoto.application.dto.CatPhotoFavoriteRetrieveResponse
import com.example.catphoto.application.dto.CatPhotoFavoriteAddRequestDto
import com.example.catphoto.helper.HttpClientHelper
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service

@Service
class CatPhotoApiApplication {
    private val log = LoggerFactory.getLogger(javaClass)

    @Value("\${cat.api.key}")
    lateinit var apiKey: String

    companion object {
        const val MAX_NUMBER = 1000
        const val PAGINATION_CRITERIA = 100
        const val SINGLE_FAVOURITE_ID = "only-one-favorite"
    }

    fun generate(number: Int): List<CatPhotoGenerateResponse> {
        if (number > MAX_NUMBER) {
            throw RuntimeException("Max requestable number is ${MAX_NUMBER}, requested: $number")
        }
        val pagination: MutableList<Int> = generatePaginationHelperList(number, PAGINATION_CRITERIA)
        val response = pagination.mapIndexed { index, value ->
            return HttpClientHelper(apiKey).get<Array<CatPhotoGenerateResponse>, String>(
                "https://api.thecatapi.com/v1/images/search?size=small&limit=${value}&page=${index}", ""
            ).body?.toList()
                ?: throw RuntimeException("No result from API server")
        }
        return response
    }

    fun retrieveFavorite(): List<CatPhotoFavoriteResponse> =
        HttpClientHelper(apiKey).get<Array<CatPhotoFavoriteResponse>, String>(
            "https://api.thecatapi.com/v1/favourites?sub_id=${SINGLE_FAVOURITE_ID}", ""
        ).body?.toList()
            ?: throw RuntimeException("No result from API server")

    fun addToFavorite(ids: List<String>) {
        val requests: List<CatPhotoFavoriteAddRequestDto> = ids.map {
            CatPhotoFavoriteAddRequestDto(imageId = it, subId = SINGLE_FAVOURITE_ID)
        }
        for (eachRequest: CatPhotoFavoriteAddRequestDto in requests) {
            val respEntity: ResponseEntity<CatPhotoFavoriteRetrieveResponse> = try {
                HttpClientHelper(apiKey).post(
                    "https://api.thecatapi.com/v1/favourites", eachRequest
                )
            } catch (e: Exception) {
                if (e.message?.contains("DUPLICATE_FAVOURITE") == true) {
                    throw RuntimeException("Greedy! Already exists, you can only non-exists cat on favorite.")
                } else {
                    throw RuntimeException(e.message)
                }
            }
            respEntity.body ?: throw RuntimeException("No result from API server")
        }
    }

    fun deleteToFavorite(ids: List<Int>) {
        for (id in ids) {
            HttpClientHelper(apiKey).delete<String, String>(
                "https://api.thecatapi.com/v1/favourites/${id}", ""
            ).body ?: throw RuntimeException("No result from API server")
        }
    }

    private fun generatePaginationHelperList(totalNumber: Int, pageUnit: Int): MutableList<Int> {
        val remain = totalNumber % pageUnit
        val pages = totalNumber / pageUnit
        val pagination: MutableList<Int> = mutableListOf()
        for (i in 0..pages) {
            if (i == pages) {
                if (remain != 0) {
                    pagination.add(remain)
                }
            } else {
                pagination.add(pageUnit)
            }
        }
        return pagination
    }
}