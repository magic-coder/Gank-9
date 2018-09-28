package com.wazing.gank.ui.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.view.View
import com.wazing.gank.R
import com.wazing.gank.base.BaseInjectorActivity
import com.wazing.gank.ui.fragment.HomeFragment
import com.wazing.gank.ui.fragment.TabLayoutFragment
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : BaseInjectorActivity() {

    private var tempFragmentTag: String? = null

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        return@OnNavigationItemSelectedListener when (item.itemId) {
            R.id.navigation_home -> {
                fab.visibility = View.VISIBLE
                tab_layout.visibility = View.GONE
                switchFragment(HomeFragment::class.java.name)
                true
            }
            R.id.navigation_dashboard -> {
                fab.visibility = View.GONE
                tab_layout.visibility = View.VISIBLE
                switchFragment(TabLayoutFragment::class.java.name)
                true
            }
            else -> false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSupportActionBar(toolbar)
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)

        // default init setting
        fab.visibility = View.VISIBLE
        switchFragment(HomeFragment::class.java.name)

        search_card_view.setOnClickListener {
            startActivity(Intent(this, SearchActivity::class.java))
        }
    }

    private fun switchFragment(tag: String) {
        val ft = supportFragmentManager.beginTransaction()
        supportFragmentManager.findFragmentByTag(tempFragmentTag)?.let { ft.hide(it) }
        var targetFragment = supportFragmentManager.findFragmentByTag(tag)
        targetFragment ?: when (tag) {
            HomeFragment::class.java.name -> targetFragment = HomeFragment()
            TabLayoutFragment::class.java.name -> targetFragment = TabLayoutFragment()
        }
        targetFragment?.let {
            if (it.isAdded) ft.show(it)
            else ft.add(R.id.container, it, tag)
            ft.commitNow()
            tempFragmentTag = tag
        }
    }

    @SuppressLint("MissingSuperCall")
    override fun onSaveInstanceState(outState: Bundle?) {
//        super.onSaveInstanceState(outState)
    }

}