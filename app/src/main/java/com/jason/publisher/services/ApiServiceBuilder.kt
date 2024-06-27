package com.jason.publisher.services

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Builder object for creating instances of Retrofit service interfaces.
 */
object ApiServiceBuilder {
    private const val BASE_URL = "http://43.226.218.94:8080/api/v1/"

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    /**
     * Builds a service instance for the specified service interface.
     *
     * @param T The type of the service interface.
     * @param service The service interface class.
     * @return An instance of the specified service interface.
     */
    fun <T> buildService(service: Class<T>): T {
        return retrofit.create(service)
    }
}
