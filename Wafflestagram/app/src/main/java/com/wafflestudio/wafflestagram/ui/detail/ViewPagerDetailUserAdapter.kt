package com.wafflestudio.wafflestagram.ui.detail

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import timber.log.Timber

class ViewPagerDetailUserAdapter(fragmentActivity: FragmentActivity): FragmentStateAdapter(fragmentActivity) {

    private var fragmentList = listOf<Fragment>()
    private var id = -1

    override fun getItemCount(): Int {
        return fragmentList.size
    }

    override fun createFragment(position: Int): Fragment {
        val fragment = fragmentList[position]
        val bundle = Bundle()

        bundle.putInt("id", id)
        fragment.arguments = bundle

        return fragment
    }

    fun setId(id: Int){
        this.id = id
        this.notifyDataSetChanged()
    }

    fun assignFragment(fragments : List<Fragment>){
        this.fragmentList = fragments
    }
}