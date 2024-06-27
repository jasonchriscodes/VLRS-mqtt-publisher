package com.jason.publisher.model

/**
 * Data class representing a message.
 *
 * @property message The content of the message.
 * @property isRead Boolean indicating if the message has been read.
 * @property date The timestamp of when the message was sent, in milliseconds since epoch.
 */
data class Message(
    val message: String,
    val isRead: Boolean,
    val date: Long
)
