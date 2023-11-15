package com.jason.publisher.services

import android.content.Context
import android.content.SharedPreferences
import com.jason.publisher.Constant

class SharedPrefMananger(context: Context) {
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences(Constant.sharedPrefsKey, Context.MODE_PRIVATE)

    fun saveString(key: String, value: String) {
        val editor = sharedPreferences.edit()
        editor.putString(key, value)
        editor.apply()
    }

    fun getString(key: String, defaultValue: String): String? {
        return sharedPreferences.getString(key, defaultValue)
    }
}