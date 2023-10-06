package com.jason.publisher.model

data class Contact(
    val message: String,
    val timestamp: String,
    val id: String,
    val isSender: Boolean = false
)
