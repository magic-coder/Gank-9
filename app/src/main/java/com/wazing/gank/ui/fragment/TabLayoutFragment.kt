package com.wazing.gank.ui.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.view.ViewPager
import android.view.View
import com.wazing.gank.R
import com.wazing.gank.base.BaseFragment
import com.wazing.gank.ui.activity.MainActivity
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_tab_layout.*
import java.util.*
import kotlin.collections.ArrayList

class TabLayoutFragment : BaseFragment() {

    private var currentTitle: CharSequence? = null

    override fun getLayoutId(): Int = R.layout.fragment_tab_layout

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val tabLayout = (activity as MainActivity).tab_layout

        val titleList = Arrays.asList("all", "Android", "iOS", "App", "前端", "瞎推荐", "拓展资源", "福利", "休息视频")
        val fragmentList: ArrayList<Fragment> = ArrayList()
        for (i in titleList) {
            fragmentList.add(CategoryFragment.newInstance(i))
        }
        val adapter = ViewPagerAdapter(childFragmentManager, titleList, fragmentList)
        view_pager.adapter = adapter
        view_pager.currentItem = 0
        view_pager.offscreenPageLimit = fragmentList.size - 1
        tabLayout.setupWithViewPager(view_pager)

        (activity as MainActivity).toolbar.title = "全部"
        view_pager.addOnPageChangeListener(object : ViewPager.SimpleOnPageChangeListener() {
            override fun onPageSelected(position: Int) {
                (activity as MainActivity).toolbar.title = when (titleList[position]) {
                    "all" -> "全部"
                    else -> titleList[position]
                }
            }
        })
    }

    private class ViewPagerAdapter constructor(
            fragmentManager: FragmentManager,
            private val titleList: List<String>,
            private val fragmentList: List<Fragment>
    ) : FragmentPagerAdapter(fragmentManager) {

        override fun getItem(position: Int): Fragment = fragmentList[position]

        override fun getCount(): Int = fragmentList.size

        override fun getPageTitle(position: Int): CharSequence? {
            val title = titleList[position]
            return when (title) {
                "all" -> "全部"
                else -> title
            }
        }
    }

}