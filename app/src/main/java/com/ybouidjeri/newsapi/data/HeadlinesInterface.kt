package com.ybouidjeri.bbcnews.data

import com.ybouidjeri.newsapi.models.HeadlinesResponse
import com.ybouidjeri.newsapi.models.SourcesResponse
import io.reactivex.Single
import okhttp3.ResponseBody
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.Url

interface HeadlinesInterface {

    @GET("top-headlines")
    fun getTopHeadlinesResponseBySourcesIds(@Query("sources") sources: String): Single<HeadlinesResponse> //Call<ApiResponse>


    @GET("top-headlines/sources")
    fun getSourcesResponse(): Single<SourcesResponse>


    @GET
    fun downloadFile(@Url fileUrl: String ): Single<ResponseBody>


}