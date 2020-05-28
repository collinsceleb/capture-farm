package com.bdn.collinsceleb.capturefarm.viewholders

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bdn.collinsceleb.capturefarm.R

class ListItemHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val farmersFullNameList : TextView = itemView.findViewById(R.id.farmerfullname)
    private val farmersImageList : ImageView = itemView.findViewById(R.id.farmerimage)
    private val farmersPhoneList : TextView = itemView.findViewById(R.id.farmerphonenumber)
    private val farmersEmailList : TextView = itemView.findViewById(R.id.farmeremail)
    private val farmersFarmNameList : TextView = itemView.findViewById(R.id.farmerfarmname)
    private val farmersFarmAddressList : TextView = itemView.findViewById(R.id.farmerfarmaddress)
    private val farmersFarmCoordinatesList : TextView = itemView.findViewById(R.id.farmerfarmcoordinates)
}