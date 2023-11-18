package com.jason.publisher.services

import android.content.Context
import androidx.appcompat.app.AlertDialog

class ModeSelectionDialog(private val context: Context) {

    interface ModeSelectionListener {
        fun onModeSelected(mode: String)
    }

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
