package com.jason.publisher.Chats

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.jason.publisher.AdapterClasses.ChatAdapter
import com.jason.publisher.AdapterClasses.ContentChatAdapter
import com.jason.publisher.Contacts.Chat
import com.jason.publisher.databinding.FragmentDetailBinding
import java.util.Objects

class DetailFragment() : Fragment() {
    private var _binding: FragmentDetailBinding? = null
    private val binding get() = _binding!!
    private var chatList = ArrayList<Chat>()
    private val db = Firebase.firestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getListChat()
        //setRecyclerList()
    }

    private fun setRecyclerList() {
        binding.rvChatBubble.setHasFixedSize(true)
        binding.rvChatBubble.layoutManager = LinearLayoutManager(requireContext())
        val listChatAdapter = ContentChatAdapter(chatList)
        binding.rvChatBubble.adapter = listChatAdapter
    }

    private fun getListChat() {
        var name = "Bus A"
        db.collection("chats").document("gReST6Cz0S6DilRILhHP").get()
            .addOnSuccessListener {result ->
                val data = result.data
                if (data != null) {
                    val listChat = data["chats"] as ArrayList<Map<String, Objects>>
                    for (chat in listChat) {
                        val sender = chat["sender"].toString();
                        chatList.add(Chat(message =  chat["message"].toString(), timestamp = chat["timestamp"].toString(), isSender = sender == name))
                        setRecyclerList()
                    }
                }
            }
            .addOnFailureListener {
                chatList.add(Chat(message =  "Error", timestamp = "21.30", isSender = false))
                setRecyclerList()
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}