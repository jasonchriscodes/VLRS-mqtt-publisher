package com.jason.publisher.services

import com.jason.publisher.model.AttributesData
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query
import retrofit2.http.Url

/**
 * Interface defining API endpoints for attribute-related operations.
 */
interface ApiService {
    /**
     * Sends attributes data to the specified URL.
     * @param url The URL to send the attributes data.
     * @param contentType The content type of the request.
     * @param requestBody The body of the request.
     * @return A [Call] object representing the asynchronous request.
     */
    @POST
    fun postAttributes(
        @Url url: String,
        @Header("Content-Type") contentType: String,
        @Body requestBody: Any
    ): Call<Void>

    /**
     * Retrieves attributes data from the specified URL.
     * @param url The URL to retrieve the attributes data.
     * @param contentType The content type of the request.
     * @param clientKeys The client keys.
     * @return A [Call] object representing the asynchronous request.
     */
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

/**
 * Data class representing the response containing client attributes.
 * @property client The client attributes data.
 */
data class ClientAttributesResponse(
    val client: AttributesData
)
