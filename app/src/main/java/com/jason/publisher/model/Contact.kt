package com.jason.publisher.model

/**
 * Data class representing a contact message.
 *
 * @property message The content of the contact message.
 * @property timestamp The timestamp of the contact message.
 * @property id The ID of the contact.
 * @property isSender Boolean indicating if the message was sent by the current user.
 */
data class Contact(
    val message: String,
    val timestamp: String,
    val id: String,
    val isSender: Boolean = false
)
