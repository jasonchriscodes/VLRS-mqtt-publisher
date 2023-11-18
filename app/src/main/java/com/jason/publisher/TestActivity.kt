package com.jason.publisher

import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import com.jason.publisher.databinding.ActivityTestBinding
import com.jason.publisher.services.SharedPrefMananger

class TestActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTestBinding
    private lateinit var sharedPrefMananger: SharedPrefMananger

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTestBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedPrefMananger = SharedPrefMananger(this)

        val messageList = sharedPrefMananger.getMessageList("messageKey")
        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_list_item_2,
            android.R.id.text1,
            messageList.map {
                "${it.message}\n${Helper.convertTimeToReadableFormat(it.date)}"
            })
        binding.messageList.adapter = adapter
        adapter.notifyDataSetChanged()
    }
}