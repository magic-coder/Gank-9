package com.wazing.gank.ui.adapter

import android.view.ViewGroup
import com.wazing.gank.R
import com.wazing.gank.base.adapter.BaseMultiRecyclerViewAdapter
import com.wazing.gank.base.adapter.BaseViewHolder
import com.wazing.gank.bean.Gank
import com.wazing.gank.utils.GlideApp
import com.wazing.gank.utils.getTimestampString

class GankAdapter : BaseMultiRecyclerViewAdapter<Gank>() {

    companion object Constant {
        private const val TYPE_ITEM = 1
        private const val TYPE_IMAGE = 2
    }

    override fun addItemViewType(position: Int): Int {
        return if (getItem(position).type == "福利") TYPE_IMAGE else TYPE_ITEM
    }

    override fun onCreateBaseViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        return when (viewType) {
            TYPE_IMAGE -> {
                BaseViewHolder.get(parent, R.layout.item_recycler_gank_image)
            }
            TYPE_ITEM -> {
                BaseViewHolder.get(parent, R.layout.item_recycler_gank_common)
            }
            else -> throw NullPointerException("Cannot find the layout corresponding to viewType.")
        }
    }

    override fun convert(holder: BaseViewHolder, item: Gank, position: Int) {
        when (holder.itemViewType) {
            TYPE_IMAGE -> {
                GlideApp.with(holder.itemView.context)
                        .load(item.url)
                        .centerCrop()
                        .into(holder.getView(R.id.item_image))
                holder.setText(R.id.item_desc, item.desc)
            }
            else -> {
                holder.setText(R.id.item_desc, item.desc)
                        .setText(R.id.item_type, item.type)
                        .setText(R.id.item_who, item.who ?: "佚名")
                        .setText(R.id.item_date, getTimestampString(item.publishedAt))
            }
        }
    }

}