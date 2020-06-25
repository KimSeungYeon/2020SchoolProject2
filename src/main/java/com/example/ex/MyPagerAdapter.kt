package com.example.ex

import android.util.Log
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
class MyPagerAdapter(fm:FragmentManager) : FragmentStatePagerAdapter(fm) {
    val mData = mutableListOf<Fragment>()
    init {
        mData.add(Fragment1())//지도
        mData.add(Fragment2())//채팅창
        mData.add(Fragment3())//투자게시판
    }
    override fun getItem(position: Int): Fragment {
        return mData.get(position)
    }
    override fun getCount(): Int {
        return mData.size
    }
    override fun getPageTitle(position: Int): CharSequence? {
        when(position){
            0 -> return "매물정보"
            1 -> return "채팅창"
            2 -> return "게시판"
            else -> return ""
        }
    }
}