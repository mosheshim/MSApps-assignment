package mosh.com.assignment.api

import mosh.com.assignment.models.ArticleResponse
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface NewsService {

    @GET("top-headlines?country=us")
    suspend fun getNewsByCategory(
        @Query("apiKey") apiKey: String,
        @Query("category") category: String
    ): Response<ArticleResponse>

    companion object {
        fun create(): NewsService {
            return Retrofit.Builder()
                .baseUrl("https://newsapi.org/v2/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(NewsService::class.java)
        }
    }
}