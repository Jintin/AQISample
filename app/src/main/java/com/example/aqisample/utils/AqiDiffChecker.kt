package com.example.aqisample.utils

import androidx.recyclerview.widget.DiffUtil
import com.example.aqisample.data.AqiData

object AqiDiffChecker : DiffUtil.ItemCallback<AqiData>() {
    override fun areItemsTheSame(oldItem: AqiData, newItem: AqiData): Boolean {
        return oldItem.siteId == newItem.siteId
    }

    override fun areContentsTheSame(oldItem: AqiData, newItem: AqiData): Boolean {
        return oldItem == newItem
    }
}