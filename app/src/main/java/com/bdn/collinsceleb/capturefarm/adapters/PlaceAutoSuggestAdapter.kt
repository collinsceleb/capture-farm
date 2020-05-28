package com.bdn.collinsceleb.capturefarm.adapters

import android.content.Context
import android.widget.ArrayAdapter
import android.widget.Filter
import android.widget.Filterable
import com.bdn.collinsceleb.capturefarm.models.PlaceApi
import java.util.*

class PlaceAutoSuggestAdapter(context: Context?, resId: Int) :
    ArrayAdapter<Any?>(context!!, resId), Filterable {
    private var results: ArrayList<String>? = null
    private val placeApi = PlaceApi()
    override fun getCount(): Int {
        return results!!.size
    }

    override fun getItem(pos: Int): String? {
        return results!![pos]
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence): FilterResults {
                val filterResults = FilterResults()
                results = placeApi.autoComplete(constraint.toString())
                filterResults.values = results
                filterResults.count = results!!.size
                return filterResults
            }

            override fun publishResults(
                constraint: CharSequence,
                results: FilterResults
            ) {
                if (results.count > 0) {
                    notifyDataSetChanged()
                } else {
                    notifyDataSetInvalidated()
                }
            }
        }
    }
}