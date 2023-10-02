package com.jason.publisher.Contacts

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.jason.publisher.Chats.ContentFragment
import com.jason.publisher.Chats.DetailFragment
import com.jason.publisher.R
import com.jason.publisher.databinding.ActivityChatBinding

class ChatActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChatBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val fragmentLeft = ContentFragment()
        supportFragmentManager.beginTransaction()
            .replace(R.id.chat_fragment, fragmentLeft)
            .commit()

        val fragmentRight = DetailFragment()
        supportFragmentManager.beginTransaction()
            .replace(R.id.detail_fragment, fragmentRight)
            .commit()
    }
}