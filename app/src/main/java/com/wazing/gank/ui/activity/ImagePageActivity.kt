package com.wazing.gank.ui.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.wazing.gank.R
import com.wazing.gank.base.adapter.BaseRecyclerViewAdapter
import com.wazing.gank.base.adapter.BaseViewHolder
import com.wazing.gank.bean.Gank
import com.wazing.gank.utils.GlideApp
import kotlinx.android.synthetic.main.layout_refresh_recycler.*

class ImagePageActivity : AppCompatActivity() {

    companion object {
        private const val KEY_GANK = "gank"
        private const val KEY_POSITION = "position"

        fun newInstance(context: Context, gank: Gank) =
                newInstance(context, arrayListOf(gank), 0)

        fun newInstance(context: Context, list: ArrayList<Gank>, position: Int) =
                with(Intent(context, ImagePageActivity::class.java)) {
                    putExtra(KEY_POSITION, if (list.size == 1) 0 else position)
                    putParcelableArrayListExtra(KEY_GANK, list)
                }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_refresh_recycler)

        val position = intent.getIntExtra(KEY_POSITION, 0)
        val gankList = intent.getParcelableArrayListExtra<Gank>(KEY_GANK)

        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS) // 透明状态栏
        setContentView(R.layout.layout_refresh_recycler)

        swipe_refresh_layout.isEnabled = false

        val adapter = ImageAdapter()
        adapter.setNewItem(gankList)

        val linearSnapHelper = androidx.recyclerview.widget.LinearSnapHelper()
        linearSnapHelper.attachToRecyclerView(recycler_view)

        recycler_view.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(this, androidx.recyclerview.widget.LinearLayoutManager.HORIZONTAL, false)
        recycler_view.adapter = adapter

        if (position > 0) {
//            recycler_view.smoothScrollToPosition(position)
            recycler_view.scrollToPosition(position)
        }
    }

    private class ImageAdapter : BaseRecyclerViewAdapter<Gank>(R.layout.item_recycler_image_page) {
        override fun convert(holder: BaseViewHolder, item: Gank, position: Int) {
            if (getAllItem().size == 1) holder.getView<View>(R.id.page_count).visibility = View.GONE
            else holder.getView<View>(R.id.page_count).visibility = View.VISIBLE
            GlideApp.with(holder.itemView.context)
                    .load(item.url)
                    .into(holder.getView(R.id.photo_view))
            holder.setText(R.id.page_count, "${(position + 1)} / ${getAllItem().size}")
        }
    }

}