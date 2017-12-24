package com.shaheer_ahsan.hullo

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter


class SectionsPagerAdapter (fm: FragmentManager?) : FragmentPagerAdapter(fm) {


    override fun getItem(position: Int): Fragment? {

        when (position) {
            0 -> {
                var requestFragment = RequestFragment()
                return requestFragment
            }
            1 -> {
                var chatFragment = ChatsFragment()
                return chatFragment
            }
            2 -> {
                var friendsFragment = FriendsFragment()
                return friendsFragment
            }
            else -> {
                return null
            }
        }

    }

    override fun getCount(): Int {

        return 3
    }

    override fun getPageTitle(position: Int): CharSequence? {

        when (position) {
            0 -> {
                return "REQUESTS"
            }
            1 -> {
                return "CHATS"
            }
            2 -> {
                return "FRIENDS"
            }
            else -> {
                return null
            }

        }
    }


}
