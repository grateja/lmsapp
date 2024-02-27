package com.vag.lmsapp.util

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class FragmentsAdapter(fragment: FragmentActivity) : FragmentStateAdapter(fragment) {
    private var fragments: List<Fragment> = emptyList()
    override fun getItemCount(): Int {
        return fragments.size
    }

    override fun createFragment(position: Int): Fragment {
        return fragments[position]
    }

    fun setData(fragments: ArrayList<Fragment>) {
        this.fragments = fragments
    }
}