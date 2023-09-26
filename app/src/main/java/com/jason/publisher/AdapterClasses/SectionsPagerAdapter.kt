package com.jason.publisher.AdapterClasses

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.jason.publisher.Fragments.CallFragment
import com.jason.publisher.Fragments.ProfileFragment
import com.jason.publisher.chat.ChatFragment

class SectionsPagerAdapter(activity: AppCompatActivity) : FragmentStateAdapter(activity) {
    override fun getItemCount(): Int {
        return 3
    }

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