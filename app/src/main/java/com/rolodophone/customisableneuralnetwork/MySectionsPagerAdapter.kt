package com.rolodophone.customisableneuralnetwork

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

private val TAB_TITLES = arrayOf(
    R.string.tab_text_1,
    R.string.tab_text_2,
    R.string.tab_text_3
)

/**
 * A [FragmentPagerAdapter] that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
class MySectionsPagerAdapter(private val context: Context, fm: FragmentManager) : FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
    var noOfTabs = 1

    override fun getItem(position: Int): Fragment {
        // getItem is called to instantiate the fragment for the given page.

        return when (position) {
            0 -> FragCreate()
            1 -> FragTrain(context as MainActivity)
            else -> FragTest(context as MainActivity)
        }
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return context.resources.getString(TAB_TITLES[position])
    }

    override fun getCount() = noOfTabs
}