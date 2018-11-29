package com.wazing.gank.utils

import android.content.Context
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.google.android.material.snackbar.Snackbar
import android.util.AttributeSet
import android.view.View

class BottomNavigationBehavior(
        context: Context, attrs: AttributeSet
) : androidx.coordinatorlayout.widget.CoordinatorLayout.Behavior<com.google.android.material.bottomnavigation.BottomNavigationView>(context, attrs) {

    private var isSnackBarShowing = false
    private var snackbar: com.google.android.material.snackbar.Snackbar.SnackbarLayout? = null

    override fun onLayoutChild(parent: androidx.coordinatorlayout.widget.CoordinatorLayout, child: com.google.android.material.bottomnavigation.BottomNavigationView, layoutDirection: Int): Boolean {
        (child.layoutParams as androidx.coordinatorlayout.widget.CoordinatorLayout.LayoutParams).topMargin = parent.measuredHeight - child.measuredHeight
        return super.onLayoutChild(parent, child, layoutDirection)
    }

    override fun layoutDependsOn(parent: androidx.coordinatorlayout.widget.CoordinatorLayout, child: com.google.android.material.bottomnavigation.BottomNavigationView, dependency: View): Boolean {
        return dependency is com.google.android.material.appbar.AppBarLayout || dependency is com.google.android.material.snackbar.Snackbar.SnackbarLayout
    }

    override fun onStartNestedScroll(coordinatorLayout: androidx.coordinatorlayout.widget.CoordinatorLayout, child: com.google.android.material.bottomnavigation.BottomNavigationView,
                                     directTargetChild: View, target: View, axes: Int, type: Int): Boolean = true

    override fun onNestedPreScroll(coordinatorLayout: androidx.coordinatorlayout.widget.CoordinatorLayout, child: com.google.android.material.bottomnavigation.BottomNavigationView,
                                   target: View, dx: Int, dy: Int, consumed: IntArray, type: Int) {
        if (isSnackBarShowing) {
            if (snackbar != null) {
                updateSnackBarPaddingByBottomNavigationView(child)
            }
        }
        super.onNestedPreScroll(coordinatorLayout, child, target, dx, dy, consumed, type)
    }

    override fun onDependentViewChanged(parent: androidx.coordinatorlayout.widget.CoordinatorLayout, child: com.google.android.material.bottomnavigation.BottomNavigationView, dependency: View): Boolean {
        return when (dependency) {
            is com.google.android.material.appbar.AppBarLayout -> {
//                val bottom = dependency.bottom.toFloat()
//                val height = dependency.height.toFloat()
//                val hidingRate = (height - bottom) / height
//                child.translationY = child.height * hidingRate
//                return true
                val top = ((dependency.layoutParams as androidx.coordinatorlayout.widget.CoordinatorLayout.LayoutParams)
                        .behavior as com.google.android.material.appbar.AppBarLayout.Behavior).topAndBottomOffset
                // 因为BottomNavigation的滑动与ToolBar是反向的，所以取-top值
                child.translationY = -top.toFloat()
                true
            }
            is com.google.android.material.snackbar.Snackbar.SnackbarLayout -> {
                if (isSnackBarShowing) return true
                isSnackBarShowing = true
                snackbar = dependency
                updateSnackBarPaddingByBottomNavigationView(child)
                true
            }
            else -> false
        }
    }

    override fun onDependentViewRemoved(parent: androidx.coordinatorlayout.widget.CoordinatorLayout, child: com.google.android.material.bottomnavigation.BottomNavigationView, dependency: View) {
        if (dependency is com.google.android.material.snackbar.Snackbar.SnackbarLayout) {
            isSnackBarShowing = false
            snackbar = null
        }
        super.onDependentViewRemoved(parent, child, dependency)
    }

    private fun updateSnackBarPaddingByBottomNavigationView(view: com.google.android.material.bottomnavigation.BottomNavigationView) {
        if (snackbar != null) {
            val bottomTranslate = (view.height - view.translationY).toInt()
            snackbar!!.setPadding(snackbar!!.paddingLeft, snackbar!!.paddingTop, snackbar!!.paddingRight, bottomTranslate)
            snackbar!!.requestLayout()
        }
    }
}