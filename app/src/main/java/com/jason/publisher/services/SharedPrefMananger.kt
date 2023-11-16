package com.jason.publisher.services

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.jason.publisher.Constant
import com.jason.publisher.model.Message

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

    fun saveMessageList(key: String, messageList: ArrayList<Message>) {
        val editor = sharedPreferences.edit()
        val gson = Gson()
        val json = gson.toJson(messageList)
        editor.putString(key, json)
        editor.apply()
    }

    fun getMessageList(key: String): ArrayList<Message> {
        val gson = Gson()
        val json = sharedPreferences.getString(key, null)
        val type = object : TypeToken<ArrayList<Message>>() {}.type
        return gson.fromJson(json, type) ?: ArrayList()
    }
}