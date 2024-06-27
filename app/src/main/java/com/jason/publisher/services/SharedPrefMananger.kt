package com.jason.publisher.services

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.jason.publisher.Constant
import com.jason.publisher.model.Message

/**
 * Class responsible for managing shared preferences.
 *
 * @param context The application context.
 */
class SharedPrefMananger(context: Context) {
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences(Constant.sharedPrefsKey, Context.MODE_PRIVATE)

    /**
     * Saves a string value in shared preferences.
     *
     * @param key The key for the value to be stored.
     * @param value The string value to be stored.
     */
    fun saveString(key: String, value: String) {
        val editor = sharedPreferences.edit()
        editor.putString(key, value)
        editor.apply()
    }

    /**
     * Retrieves a string value from shared preferences.
     *
     * @param key The key for the value to be retrieved.
     * @param defaultValue The default value to return if the key does not exist.
     * @return The string value associated with the key, or the default value if the key does not exist.
     */
    fun getString(key: String, defaultValue: String): String? {
        return sharedPreferences.getString(key, defaultValue)
    }

    /**
     * Saves a list of messages in shared preferences.
     *
     * @param key The key for the value to be stored.
     * @param messageList The list of messages to be stored.
     */
    fun saveMessageList(key: String, messageList: ArrayList<Message>) {
        val editor = sharedPreferences.edit()
        val gson = Gson()
        val json = gson.toJson(messageList)
        editor.putString(key, json)
        editor.apply()
    }

    /**
     * Retrieves a list of messages from shared preferences.
     *
     * @param key The key for the value to be retrieved.
     * @return The list of messages associated with the key, or an empty list if the key does not exist.
     */
    fun getMessageList(key: String): ArrayList<Message> {
        val gson = Gson()
        val json = sharedPreferences.getString(key, null)
        val type = object : TypeToken<ArrayList<Message>>() {}.type
        return gson.fromJson(json, type) ?: ArrayList()
    }
}