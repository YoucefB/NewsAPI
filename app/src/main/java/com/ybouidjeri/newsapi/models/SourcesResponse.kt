package com.ybouidjeri.newsapi.models

data class SourcesResponse(
    val status: String,
    val sources: List<Source>
) {
}