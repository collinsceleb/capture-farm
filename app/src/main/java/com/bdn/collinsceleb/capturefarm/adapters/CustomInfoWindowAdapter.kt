package com.bdn.collinsceleb.capturefarm.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import com.bdn.collinsceleb.capturefarm.R
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter
import com.google.android.gms.maps.model.Marker

class CustomInfoWindowAdapter @SuppressLint("InflateParams") constructor(context: Context?) :
    InfoWindowAdapter {
    @SuppressLint("InflateParams")
    private val window: View =
        LayoutInflater.from(context).inflate(R.layout.custom_info_window, null)

    private fun renderedWindowText(
        marker: Marker,
        view: View
    ) {
        val title = marker.title
        val tvTitle = view.findViewById<View>(R.id.title) as TextView
        if (title != "") {
            tvTitle.text = title
        }
        val snippet = marker.snippet
        val tvSnippet = view.findViewById<View>(R.id.snippet) as TextView
        if (snippet != "") {
            tvSnippet.text = snippet
        }
    }

    override fun getInfoWindow(marker: Marker): View {
        renderedWindowText(marker, window)
        return window
    }

    override fun getInfoContents(marker: Marker): View {
        renderedWindowText(marker, window)
        return window
    }

}