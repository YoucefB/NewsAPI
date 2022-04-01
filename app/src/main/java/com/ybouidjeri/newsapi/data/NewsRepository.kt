package com.ybouidjeri.newsapi.data

import com.ybouidjeri.newsapi.models.*
import io.reactivex.Single
import okhttp3.ResponseBody

interface NewsRepository {

    fun getTopHeadlines(source: String): Single<HeadlinesResponse>

    fun getSources(): Single<SourcesResponse>

    fun getFile(fileUrl: String ): Single<ResponseBody>

}