package com.jason.publisher.Fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayoutMediator
import com.jason.publisher.AdapterClasses.SectionsPagerAdapter
import com.jason.publisher.R
import com.jason.publisher.databinding.FragmentContentBinding

class ContentFragment : Fragment() {
    private var _binding: FragmentContentBinding? = null
    private val binding get() = _binding!!
    private var isSearch = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentContentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val sectionsPagerAdapter = SectionsPagerAdapter(activity as AppCompatActivity)
        binding.viewPager.adapter = sectionsPagerAdapter
        TabLayoutMediator(binding.tabs, binding.viewPager) { tab, position ->
            tab.text = TAB_TITLES[position]
        }.attach()

        binding.btnSearch.setOnClickListener {
            isSearch = !isSearch
            if (isSearch) {
                binding.tvTitle.visibility = View.GONE
                binding.etSearch.visibility = View.VISIBLE
                binding.btnSearch.setImageResource(R.drawable.ic_blank)
            } else {
                binding.tvTitle.visibility = View.VISIBLE
                binding.etSearch.visibility = View.GONE
                binding.btnSearch.setImageResource(R.drawable.ic_search)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private val TAB_TITLES = listOf("Chat", "Call", "Profile")
    }

}