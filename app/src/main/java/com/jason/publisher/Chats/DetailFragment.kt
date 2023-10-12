package com.jason.publisher.Chats

import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.Timestamp
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.jason.publisher.AdapterClasses.ContentChatAdapter
import com.jason.publisher.model.Chat
import com.jason.publisher.databinding.FragmentDetailBinding
import com.jason.publisher.services.SharedPrefMananger

class DetailFragment() : Fragment() {
    private var _binding: FragmentDetailBinding? = null
    private lateinit var sharedPrefMananger: SharedPrefMananger
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
        sharedPrefMananger = SharedPrefMananger(requireContext())
        val mId = Settings.Secure.getString(requireContext().contentResolver, Settings.Secure.ANDROID_ID)
        val chatRoomId = generateChatRoomid(mId, contactId)
        val msgCollection = db.collection("chat").document(chatRoomId).collection("message")
        if (contactId != null) {
            binding.tvNoSelectedChat.visibility = View.GONE
            binding.customToolbar.visibility = View.VISIBLE
            binding.rvChatBubble.visibility = View.VISIBLE
            binding.layoutSendChat.visibility = View.VISIBLE
            getListChat(msgCollection)
        }

        binding.imageviewsendmessage.setOnClickListener {
            sendMessage(mId, msgCollection)
        }
    }

    private fun setRecyclerList() {
        val mId = Settings.Secure.getString(requireContext().contentResolver, Settings.Secure.ANDROID_ID)
        binding.rvChatBubble.setHasFixedSize(true)
        binding.rvChatBubble.layoutManager = LinearLayoutManager(requireContext())
        val listData = contactList.sortedWith(compareBy { it.timestamp })
        val listChatAdapter = ContentChatAdapter(listData, mId!!)
        binding.rvChatBubble.adapter = listChatAdapter
    }

    private fun getListChat(msgCollection: CollectionReference) {
        msgCollection.addSnapshotListener { querySnapshot, _ ->
            contactList.clear()
            querySnapshot?.documents?.forEach { documentSnapshot ->
                val text = documentSnapshot.getString("text") ?: ""
                val sender = documentSnapshot.getString("sender")
                val timestamp = documentSnapshot.get("timestamp") as Timestamp

                contactList.add(
                    Chat(
                        message = text,
                        sender = sender!!,
                        timestamp = timestamp
                    )
                )
            }
            setRecyclerList()
        }
    }

    private fun sendMessage(mId: String, msgCollection: CollectionReference) {
        val textMsg = binding.etSendChat.text.toString()
        if (textMsg.isNotBlank()) {
            val message = hashMapOf(
                "sender" to mId,
                "text" to textMsg,
                "timestamp" to Timestamp.now()
            )
            msgCollection.add(message)
            binding.etSendChat.text.clear()
        }
    }

    private fun generateChatRoomid(currentId: String?, contactId: String?): String {
        val sortedUserIds = listOf(currentId, contactId)
        val sortedList = sortedUserIds.sortedWith(compareBy { it })
        return "chat_${sortedList[0]}_${sortedList[1]}"
    }

//    override fun onDestroyView() {
//        super.onDestroyView()
//        _binding = null
//    }

}