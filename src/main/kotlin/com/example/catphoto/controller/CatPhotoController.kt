package com.example.catphoto.controller

import com.example.catphoto.application.CatPhotoApiApplication
import com.example.catphoto.application.dto.CatPhotoDto
import com.example.catphoto.application.dto.CatPhotoFavoriteDeleteDto
import com.example.catphoto.application.dto.CatPhotoFavoriteRetrieveDto
import com.example.catphoto.application.dto.CatPhotoFavoriteSaveDto
import lombok.RequiredArgsConstructor
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*

@Controller
@RequiredArgsConstructor
class CatPhotoController(private val application: CatPhotoApiApplication) {


    @GetMapping("/")
    fun main(): String {
        return "main.html"
    }

    @GetMapping("/test")
    fun test(model: Model): String {
        model.addAllAttributes(mapOf("name" to "Aaron"))
        return "test.html"
    }

    @ResponseBody
    @GetMapping("/catnena")
    fun catnena(@RequestParam number: Int): List<CatPhotoDto> {
        return application.retreive(number)
    }

    @ResponseBody
    @PostMapping("/favorite/save")
    fun favoriteSave(@RequestBody request: CatPhotoFavoriteSaveDto): Boolean {
        application.favoriteSave(request.ids)
        return true
    }

    @ResponseBody
    @GetMapping("/favorite/retrieve")
    fun favoriteRetrieve(): List<CatPhotoFavoriteRetrieveDto> {
        return application.favoriteRetrieve()
    }

    @ResponseBody
    @DeleteMapping("/favorite/delete")
    fun favoriteDelete(@RequestBody request: CatPhotoFavoriteDeleteDto): Boolean {
        application.favoriteDelete(request.ids)
        return true
    }
}