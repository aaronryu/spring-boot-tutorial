package com.example.catphoto.controller

import com.example.catphoto.application.CatPhotoApiApplication
import com.example.catphoto.application.dto.CatPhotoGenerateResponse
import com.example.catphoto.application.dto.CatPhotoFavoriteDeleteRequest
import com.example.catphoto.application.dto.CatPhotoFavoriteResponse
import com.example.catphoto.application.dto.CatPhotoFavoriteAddRequest
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*

@Controller
class CatPhotoController(
    private val application: CatPhotoApiApplication
) {

    @GetMapping("/")
    fun dashboard(model: Model): String {
        model.addAllAttributes(mapOf("name" to "Aaron"))
        return "dashboard.html"
    }

    @ResponseBody
    @GetMapping("/cat/generate/")
    fun generateCat(@RequestParam number: Int): List<CatPhotoGenerateResponse> {
        return application.generate(number)
    }

    @ResponseBody
    @PostMapping("/cat/favorite")
    fun addToFavorite(@RequestBody request: CatPhotoFavoriteAddRequest): Boolean {
        application.addToFavorite(request.ids)
        return true
    }

    @ResponseBody
    @GetMapping("/cat/favorite")
    fun retrieveFavorite(): List<CatPhotoFavoriteResponse> {
        return application.retrieveFavorite()
    }

    @ResponseBody
    @DeleteMapping("/cat/favorite")
    fun deleteToFavorite(@RequestBody request: CatPhotoFavoriteDeleteRequest): Boolean {
        application.deleteToFavorite(request.ids)
        return true
    }
}