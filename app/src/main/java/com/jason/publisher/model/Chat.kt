package com.jason.publisher.model

import com.google.firebase.Timestamp


/**
 * Data class representing a chat message.
 *
 * @property message The content of the chat message.
 * @property sender The sender of the chat message.
 * @property timestamp The timestamp of when the chat message was sent.
 */
data class Chat(
    val message: String,
    val sender: String,
    val timestamp: Timestamp
)
