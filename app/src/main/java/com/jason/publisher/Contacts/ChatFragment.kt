package com.jason.publisher.Contacts

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.jason.publisher.AdapterClasses.ChatAdapter
import com.jason.publisher.Constant
import com.jason.publisher.databinding.FragmentChatBinding
import com.jason.publisher.model.Contact
import com.jason.publisher.services.SharedPrefMananger

/**
 * A simple [Fragment] subclass representing the chat list screen.
 */
class ChatFragment : Fragment() {
    private var _binding: FragmentChatBinding? = null
    private val binding get() = _binding!!
    private var contactList = ArrayList<Contact>()
    private val db = Firebase.firestore
    private lateinit var sharedPrefMananger: SharedPrefMananger

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
        _binding = FragmentChatBinding.inflate(inflater, container, false)
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
        getListChat()
        setRecyclerList()
    }

    /**
     * Sets up the RecyclerView with contact messages.
     */
    private fun setRecyclerList() {
        binding.rvChat.setHasFixedSize(true)
        binding.rvChat.layoutManager = LinearLayoutManager(requireContext())
        val listChatAdapter = ChatAdapter(contactList, requireContext())
        binding.rvChat.adapter = listChatAdapter
    }

    /**
     * Retrieves the list of chat contacts from Firestore.
     */
    private fun getListChat() {
        val deviceName = sharedPrefMananger.getString(Constant.deviceNameKey, "")
        db.collection("config").get()
            .addOnSuccessListener {result ->
                for (doc in result.documents) {
                    val data = doc.data
                    contactList.add(Contact(message = data!!["name"].toString(), timestamp = "21.30", id = data["aid"].toString()))
                }
                val index = contactList.indexOfFirst { contact -> contact.message == deviceName }
                contactList.removeAt(index)
                setRecyclerList()
            }
            .addOnFailureListener {
                contactList.add(Contact(message =  "Error", timestamp = "21.30", id = "", isSender = false))
                setRecyclerList()
            }
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