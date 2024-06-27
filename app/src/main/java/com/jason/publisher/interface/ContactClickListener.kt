package com.jason.publisher.`interface`

/**
 * Interface for handling contact click events.
 */
interface ContactClickListener {

    /**
     * Called when a contact is clicked.
     *
     * @param contactId The ID of the clicked contact.
     */
    fun onContackClicked(contactId: String)
}
