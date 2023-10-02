package com.jason.publisher.Contacts

data class Chat(
    val message: String,
    val timestamp: String,
    val isSender: Boolean = false
)
