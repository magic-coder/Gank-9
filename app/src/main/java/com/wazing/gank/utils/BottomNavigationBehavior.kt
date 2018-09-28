package com.wazing.gank.utils

import android.content.Context
import android.support.design.widget.AppBarLayout
import android.support.design.widget.BottomNavigationView
import android.support.design.widget.CoordinatorLayout
import android.support.design.widget.Snackbar
import android.util.AttributeSet
import android.view.View

class BottomNavigationBehavior(
        context: Context, attrs: AttributeSet
) : CoordinatorLayout.Behavior<BottomNavigationView>(context, attrs) {

    private var isSnackBarShowing = false
    private var snackbar: Snackbar.SnackbarLayout? = null

    override fun onLayoutChild(parent: CoordinatorLayout, child: BottomNavigationView, layoutDirection: Int): Boolean {
        (child.layoutParams as CoordinatorLayout.LayoutParams).topMargin = parent.measuredHeight - child.measuredHeight
        return super.onLayoutChild(parent, child, layoutDirection)
    }

    override fun layoutDependsOn(parent: CoordinatorLayout, child: BottomNavigationView, dependency: View): Boolean {
        return dependency is AppBarLayout || dependency is Snackbar.SnackbarLayout
    }

    override fun onStartNestedScroll(coordinatorLayout: CoordinatorLayout, child: BottomNavigationView,
                                     directTargetChild: View, target: View, axes: Int, type: Int): Boolean = true

    override fun onNestedPreScroll(coordinatorLayout: CoordinatorLayout, child: BottomNavigationView,
                                   target: View, dx: Int, dy: Int, consumed: IntArray, type: Int) {
        if (isSnackBarShowing) {
            if (snackbar != null) {
                updateSnackBarPaddingByBottomNavigationView(child)
            }
        }
        super.onNestedPreScroll(coordinatorLayout, child, target, dx, dy, consumed, type)
    }

    override fun onDependentViewChanged(parent: CoordinatorLayout, child: BottomNavigationView, dependency: View): Boolean {
        return when (dependency) {
            is AppBarLayout -> {
//                val bottom = dependency.bottom.toFloat()
//                val height = dependency.height.toFloat()
//                val hidingRate = (height - bottom) / height
//                child.translationY = child.height * hidingRate
//                return true
                val top = ((dependency.layoutParams as CoordinatorLayout.LayoutParams)
                        .behavior as AppBarLayout.Behavior).topAndBottomOffset
                // 因为BottomNavigation的滑动与ToolBar是反向的，所以取-top值
                child.translationY = -top.toFloat()
                true
            }
            is Snackbar.SnackbarLayout -> {
                if (isSnackBarShowing) return true
                isSnackBarShowing = true
                snackbar = dependency
                updateSnackBarPaddingByBottomNavigationView(child)
                true
            }
            else -> false
        }
    }

    override fun onDependentViewRemoved(parent: CoordinatorLayout, child: BottomNavigationView, dependency: View) {
        if (dependency is Snackbar.SnackbarLayout) {
            isSnackBarShowing = false
            snackbar = null
        }
        super.onDependentViewRemoved(parent, child, dependency)
    }

    private fun updateSnackBarPaddingByBottomNavigationView(view: BottomNavigationView) {
        if (snackbar != null) {
            val bottomTranslate = (view.height - view.translationY).toInt()
            snackbar!!.setPadding(snackbar!!.paddingLeft, snackbar!!.paddingTop, snackbar!!.paddingRight, bottomTranslate)
            snackbar!!.requestLayout()
        }
    }
}