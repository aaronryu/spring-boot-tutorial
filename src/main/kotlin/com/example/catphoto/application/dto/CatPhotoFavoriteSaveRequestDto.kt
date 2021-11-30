package com.example.catphoto.application.dto
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CatPhotoFavoriteSaveRequestDto(
    @SerialName("image_id")
    val imageId: String,
    @SerialName("sub_id")
    val subId: String
)