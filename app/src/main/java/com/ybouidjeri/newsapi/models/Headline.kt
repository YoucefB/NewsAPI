package com.ybouidjeri.newsapi.models

import java.io.Serializable

data class Headline(
    val title: String,
    val description: String?,
    val url: String,
    val urlToImage: String?,
    val content: String?
    ): Serializable{

}