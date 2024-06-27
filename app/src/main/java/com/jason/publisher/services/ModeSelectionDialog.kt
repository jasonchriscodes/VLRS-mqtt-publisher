package com.jason.publisher.services

import android.content.Context
import androidx.appcompat.app.AlertDialog

/**
 * Class responsible for displaying a mode selection dialog.
 *
 * @param context The application context.
 */
class ModeSelectionDialog(private val context: Context) {

    /**
     * Interface for handling mode selection events.
     */
    interface ModeSelectionListener {

        /**
         * Called when a mode is selected.
         *
         * @param mode The selected mode.
         */
        fun onModeSelected(mode: String)
    }

    /**
     * Displays the mode selection dialog.
     *
     * @param listener The listener to be notified with the selected mode.
     */
    fun showModeSelectionDialog(listener: ModeSelectionListener) {
        val modes = arrayOf("Offline", "Online")

        val builder = AlertDialog.Builder(context)
        builder.setTitle("Choose Mode")
            .setItems(modes) { dialog, which ->
                val selectedMode = modes[which]
                listener.onModeSelected(selectedMode)
                dialog.dismiss()
            }

        val dialog = builder.create()
        dialog.show()
    }
}
