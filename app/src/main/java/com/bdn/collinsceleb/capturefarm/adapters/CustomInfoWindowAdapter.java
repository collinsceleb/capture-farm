package com.bdn.collinsceleb.capturefarm.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.bdn.collinsceleb.capturefarm.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

public class CustomInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

    private final View mWindow;

    @SuppressLint("InflateParams")
    public CustomInfoWindowAdapter(Context context) {

        mWindow = LayoutInflater.from(context).inflate(R.layout.custom_info_window,null);
    }

    private void renderedWindowText(Marker marker, View view) {
        String title = marker.getTitle();
        TextView tvTitle = (TextView) view.findViewById(R.id.title);

        if (!title.equals("")) {
            tvTitle.setText(title);
        }

        String snippet = marker.getSnippet();
        TextView tvSnippat = (TextView) view.findViewById(R.id.snippet);

        if (!snippet.equals("")) {
            tvSnippat.setText(snippet);

        }
    }

    @Override
    public View getInfoWindow(Marker marker) {
        renderedWindowText(marker,mWindow);
        return mWindow;

    }

    @Override
    public View getInfoContents(Marker marker) {
        renderedWindowText(marker,mWindow);
        return mWindow;
    }
}