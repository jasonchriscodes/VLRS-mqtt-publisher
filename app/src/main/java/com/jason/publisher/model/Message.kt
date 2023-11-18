package com.jason.publisher.model

data class Message(
    val message: String,
    val isRead: Boolean,
    val date: Long
)

