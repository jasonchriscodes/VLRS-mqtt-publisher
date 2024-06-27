package com.jason.publisher.Chats

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

/**
 * A simple [Fragment] subclass representing the main content with tabs for Chat, Call, and Profile.
 */
class ContentFragment : Fragment() {
    private var _binding: FragmentContentBinding? = null
    private val binding get() = _binding!!
    private var isSearch = false

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
        _binding = FragmentContentBinding.inflate(inflater, container, false)
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

    /**
     * Called when the view previously created by onCreateView has been detached from the fragment.
     * This is where you should clean up resources related to the binding.
     */
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private val TAB_TITLES = listOf("Chat", "Call", "Profile")
    }

}