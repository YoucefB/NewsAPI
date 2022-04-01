package com.ybouidjeri.newsapi.models

data class Source(
    val id: String,
    val name: String,
    val description: String?,
    val url: String?,
    val category: String?,
    val language: String?,
    val country: String?
) {
    constructor(id: String, name: String) :
            this(id, name, null, null, null, null, null) {
    }
}