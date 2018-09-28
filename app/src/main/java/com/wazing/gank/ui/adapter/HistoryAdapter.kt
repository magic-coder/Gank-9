package com.wazing.gank.ui.adapter

import com.wazing.gank.R
import com.wazing.gank.base.adapter.BaseRecyclerViewAdapter
import com.wazing.gank.base.adapter.BaseViewHolder
import com.wazing.gank.bean.History

class HistoryAdapter : BaseRecyclerViewAdapter<History>(R.layout.item_recycler_history) {

    override fun convert(holder: BaseViewHolder, item: History, position: Int) {
        holder.setText(R.id.item_history, "${item.date} \t ${item.desc}")
    }
}