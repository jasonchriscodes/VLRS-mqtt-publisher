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


/**
 * A simple [Fragment] subclass for displaying chat details.
 */
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

        /**
         * Creates a new instance of DetailFragment with the provided contact ID.
         *
         * @param contactId The ID of the contact.
         * @return A new instance of DetailFragment.
         */
        fun newInstance(contacId: String): DetailFragment {
            val fragment = DetailFragment()
            val args = Bundle()
            args.putString(ARG_CONTACT_ID, contacId)
            fragment.arguments = args
            return fragment
        }
    }

    /**
     * Called to have the fragment instantiate its user interface view.
     *
     * @param inflater The LayoutInflater object that can be used to inflate any views in the fragment.
     * @param container If non-null, this is the parent view that the fragment's UI should be attached to.
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state as given here.
     * @return Return the View for the fragment's UI.
     */
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

    /**
     * Called immediately after onCreateView has returned, but before any saved state has been restored in to the view.
     *
     * @param view The View returned by onCreateView.
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state as given here.
     */
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

    /**
     * Sets up the RecyclerView with chat messages.
     */
    private fun setRecyclerList() {
        val mId = Settings.Secure.getString(requireContext().contentResolver, Settings.Secure.ANDROID_ID)
        binding.rvChatBubble.setHasFixedSize(true)
        binding.rvChatBubble.layoutManager = LinearLayoutManager(requireContext())
        val listData = contactList.sortedWith(compareBy { it.timestamp })
        val listChatAdapter = ContentChatAdapter(listData, mId!!)
        binding.rvChatBubble.adapter = listChatAdapter
    }

    /**
     * Retrieves the list of chat messages from Firestore.
     *
     * @param msgCollection The Firestore collection reference for the chat messages.
     */
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

    /**
     * Sends a message to the Firestore chat collection.
     *
     * @param mId The ID of the current device.
     * @param msgCollection The Firestore collection reference for the chat messages.
     */
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

    /**
     * Generates a chat room ID based on the current device ID and the contact ID.
     *
     * @param currentId The ID of the current device.
     * @param contactId The ID of the contact.
     * @return The generated chat room ID.
     */
    private fun generateChatRoomid(currentId: String?, contactId: String?): String {
        val sortedUserIds = listOf(currentId, contactId)
        val sortedList = sortedUserIds.sortedWith(compareBy { it })
        return "chat_${sortedList[0]}_${sortedList[1]}"
    }

    /**
     * Called when the view previously created by onCreateView has been detached from the fragment.
     * This is where you should clean up resources related to the binding.
     */
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}