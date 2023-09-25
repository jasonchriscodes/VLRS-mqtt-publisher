package com.jason.publisher.chat

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.jason.publisher.R

class ChatActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        val fragmentManager = supportFragmentManager
        val chatFragment = ChatFragment()
        //val fragment = fragmentManager.findFragmentByTag(ChatFragment::class.java.simpleName)

        fragmentManager.beginTransaction()
            .add(R.id.chat_fragment, chatFragment, ChatFragment::class.java.simpleName)
            .commit()

        val fragmentManager2 = supportFragmentManager
        val detailFragment = DetailFragment()
        //val fragment = fragmentManager.findFragmentByTag(ChatFragment::class.java.simpleName)

        fragmentManager2.beginTransaction()
            .add(R.id.detail_fragment, detailFragment, DetailFragment::class.java.simpleName)
            .commit()
    }
}