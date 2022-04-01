package com.ybouidjeri.newsapi.models

import com.google.gson.annotations.SerializedName

data class HeadlinesResponse(
    val status: String,
    val code: String?,
    val message: String?,
    val totalResults: Int?,
    @SerializedName("articles")
    val headlines: List<Headline>,

) {
}