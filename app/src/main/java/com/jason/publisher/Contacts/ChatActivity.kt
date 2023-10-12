package com.jason.publisher.Contacts

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.jason.publisher.Chats.ContentFragment
import com.jason.publisher.Chats.DetailFragment
import com.jason.publisher.Constant
import com.jason.publisher.R
import com.jason.publisher.databinding.ActivityChatBinding
import com.jason.publisher.`interface`.ContactClickListener

class ChatActivity : AppCompatActivity(), ContactClickListener {

    private lateinit var binding: ActivityChatBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val name = intent.getStringExtra(Constant.deviceNameKey)
        val bundle = Bundle()
        bundle.putString(Constant.deviceNameKey, name)

        val fragmentLeft = ContentFragment()
        fragmentLeft.arguments = bundle
        supportFragmentManager.beginTransaction()
            .replace(R.id.chat_fragment, fragmentLeft)
            .commit()

        val fragmentRight = DetailFragment()
        supportFragmentManager.beginTransaction()
            .replace(R.id.detail_fragment, fragmentRight)
            .commit()
    }

    override fun onContackClicked(contactId: String) {
        Log.d("Check Click", "Clicked")
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        val fragment = DetailFragment.newInstance(contactId)
        fragmentTransaction.remove(fragment)
            .add(R.id.detail_fragment, fragment)
            .commit()
    }

}