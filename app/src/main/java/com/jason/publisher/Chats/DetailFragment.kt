package com.jason.publisher.Chats

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.Timestamp
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.jason.publisher.AdapterClasses.ContentChatAdapter
import com.jason.publisher.model.Chat
import com.jason.publisher.model.Contact
import com.jason.publisher.databinding.FragmentDetailBinding
import java.util.Objects

class DetailFragment() : Fragment() {
    private var _binding: FragmentDetailBinding? = null
    private val binding get() = _binding!!
    private var contactList = ArrayList<Chat>()
    private val db = Firebase.firestore
    private var contactId: String? = null
    var name = "Bus A"

    companion object {
        private const val ARG_CONTACT_ID = "contactId"

        fun newInstance(contacId: String): DetailFragment {
            val fragment = DetailFragment()
            val args = Bundle()
            args.putString(ARG_CONTACT_ID, contacId)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        arguments?.let {
            contactId = it.getString(ARG_CONTACT_ID)
        }
        _binding = FragmentDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (contactId != null) {
            binding.tvNoSelectedChat.visibility = View.GONE
            binding.customToolbar.visibility = View.VISIBLE
            binding.rvChatBubble.visibility = View.VISIBLE
            binding.layoutSendChat.visibility = View.VISIBLE
            getListChat()
        }

        binding.imageviewsendmessage.setOnClickListener {
            sendMessage()
        }
    }

    private fun setRecyclerList() {
        binding.rvChatBubble.setHasFixedSize(true)
        binding.rvChatBubble.layoutManager = LinearLayoutManager(requireContext())
        val listChatAdapter = ContentChatAdapter(contactList)
        binding.rvChatBubble.adapter = listChatAdapter
    }

    private fun getListChat() {
        db.collection("chats").document(contactId!!).addSnapshotListener { value, error ->
            if (error != null) {
                return@addSnapshotListener
            }
            val listChat = value?.data!!["chats"] as ArrayList<Map<String, Any>>
            contactList.clear()
            for (chat in listChat) {
                val sender = chat["sender"].toString()
                contactList.add(
                    Chat(
                        message =  chat["message"].toString(),
                        sender = sender,
                        timestamp = chat["timestamp"] as Timestamp
                    )
                )
            }
            setRecyclerList()
        }
    }

    private fun sendMessage() {
        val textMsg = binding.etSendChat.text.toString()
        val docRef= db.collection("chats").document(contactId!!)
        docRef.get().addOnSuccessListener {docSnapshot ->
            val data = docSnapshot.data
            val newChat = mapOf("message" to textMsg, "sender" to name, "timestamp" to Timestamp.now())
            val currentChat = data?.get("chats") as ArrayList<Map<String, Any>>
            currentChat.add(newChat)
            docRef.update("chats", currentChat)
                .addOnSuccessListener {
                    binding.etSendChat.text.clear()
                }
                .addOnFailureListener {
                    Toast.makeText(context, "Failed to send Message, try again", Toast.LENGTH_LONG).show()
                }
        }
    }

//    override fun onDestroyView() {
//        super.onDestroyView()
//        _binding = null
//    }

}