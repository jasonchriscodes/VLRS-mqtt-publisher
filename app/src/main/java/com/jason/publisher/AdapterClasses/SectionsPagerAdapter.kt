package com.jason.publisher.AdapterClasses

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.jason.publisher.Chats.CallFragment
import com.jason.publisher.Chats.ProfileFragment
import com.jason.publisher.Contacts.ChatFragment

/**
 * Adapter class for managing the different sections (fragments) in the ViewPager.
 *
 * @param activity The parent activity where the ViewPager is used.
 */
class SectionsPagerAdapter(activity: AppCompatActivity) : FragmentStateAdapter(activity) {

    /**
     * Returns the number of items (fragments) in the ViewPager.
     *
     * @return The total number of fragments.
     */
    override fun getItemCount(): Int {
        return 3
    }

    /**
     * Creates a new fragment based on the position.
     *
     * @param position The position of the fragment in the ViewPager.
     * @return The fragment corresponding to the given position.
     */
    override fun createFragment(position: Int): Fragment {
        var fragment: Fragment? = null
        when (position) {
            0 -> fragment = ChatFragment()
            1 -> fragment = CallFragment()
            2 -> fragment = ProfileFragment()
        }
        return fragment as Fragment
    }

}