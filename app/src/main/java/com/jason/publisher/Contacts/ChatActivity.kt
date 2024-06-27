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

/**
 * Activity for displaying chat interface with a list of contacts and detailed chat view.
 */
class ChatActivity : AppCompatActivity(), ContactClickListener {

    private lateinit var binding: ActivityChatBinding

    /**
     * Called when the activity is starting. This is where most initialization should go.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously being shut down, this contains the data it most recently supplied in onSaveInstanceState(Bundle).
     */
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

    /**
     * Called when a contact is clicked.
     *
     * @param contactId The ID of the clicked contact.
     */
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