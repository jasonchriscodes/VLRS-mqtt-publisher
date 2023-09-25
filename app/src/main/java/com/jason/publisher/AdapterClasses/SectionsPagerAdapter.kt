package com.jason.publisher.AdapterClasses

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.jason.publisher.Fragments.CallsFragment
import com.jason.publisher.Fragments.ChatsFragment
import com.jason.publisher.Fragments.ProfileFragment

class SectionsPagerAdapter(activity: AppCompatActivity) : FragmentStateAdapter(activity) {

    override fun createFragment(position: Int): Fragment {
        var fragment: Fragment? = null
        when (position) {
            0 -> fragment = ChatsFragment()
            1 -> fragment = CallsFragment()
            2 -> fragment = ProfileFragment()
        }
        return fragment as Fragment
    }

    override fun getItemCount(): Int {
        return 3
    }
}