package com.jlopez.shoppinglisttestdrivendevelopmentapp.data.remote.responses

data class ImageResponse(
    val hits: List<ImageResult>,
    val total: Int,
    val totalHits: Int
)