package com.example.jetnews.data.service

import com.example.jetnews.model.Rss
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Url


interface ApiService {
    @GET
    fun rss(@Url url: String?): Call<Rss?>?

    companion object {
        const val BASE_URL = "https://www.sandiegouniontribune.com/news/rss2.0.xml"
    }
}