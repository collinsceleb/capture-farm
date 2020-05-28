package com.bdn.collinsceleb.capturefarm.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bdn.collinsceleb.capturefarm.R
import com.bdn.collinsceleb.capturefarm.viewholders.ListItemHolder

class FarmersListItemRecyclerAdapter : RecyclerView.Adapter<ListItemHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListItemHolder {
        var listItemHolder = LayoutInflater.from(parent.context).inflate(R.layout.farmers_list_item, parent, false)
        return ListItemHolder(listItemHolder)
    }

    override fun getItemCount(): Int {
        TODO("Not yet implemented")
    }

    override fun onBindViewHolder(holder: ListItemHolder, position: Int) {
        TODO("Not yet implemented")
    }
}