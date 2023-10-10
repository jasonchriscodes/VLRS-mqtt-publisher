package com.jason.publisher.model

import com.google.firebase.Timestamp

data class Chat(
    val message: String,
    val sender: String,
    val timestamp: Timestamp
)
