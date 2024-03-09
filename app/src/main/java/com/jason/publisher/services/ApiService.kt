package com.jason.publisher.services

import com.jason.publisher.model.AttributesData
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query
import retrofit2.http.Url

interface ApiService {
    @POST
    fun postAttributes(
        @Url url: String,
        @Header("Content-Type") contentType: String,
        @Body requestBody: Any
    ): Call<Void>

    @GET
    fun getAttributes(
        @Url url: String,
        @Header("Content-Type") contentType: String,
        @Query("clientKeys") clientKeys: String
    ): Call<ClientAttributesResponse>

    companion object {
        const val BASE_URL = "http://43.226.218.94:8080/api/v1/"
    }
}

data class ClientAttributesResponse(
    val client: AttributesData
)
