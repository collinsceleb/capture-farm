package com.bdn.collinsceleb.capturefarm.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bdn.collinsceleb.capturefarm.R
import com.bdn.collinsceleb.capturefarm.models.FarmersInformation

class FarmersListItemRecyclerAdapter internal constructor(context: Context) :
    RecyclerView.Adapter<FarmersListItemRecyclerAdapter.FarmerViewHolder>() {
    private var records = emptyList<FarmersInformation>()

    inner class FarmerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var farmersFullNameList: TextView = itemView.findViewById(R.id.farmerfullname)
        var farmersImageList: ImageView = itemView.findViewById(R.id.farmerimage)
        val farmersPhoneList: TextView = itemView.findViewById(R.id.farmerphonenumber)
        val farmersEmailList: TextView = itemView.findViewById(R.id.farmeremail)
        val farmersFarmNameList: TextView = itemView.findViewById(R.id.farmerfarmname)
        val farmersFarmAddressList: TextView = itemView.findViewById(R.id.farmerfarmaddress)
        val farmersFarmCoordinatesList: TextView = itemView.findViewById(R.id.farmerfarmcoordinates)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FarmerViewHolder {
        var listItemHolder =
            LayoutInflater.from(parent.context).inflate(R.layout.farmers_list_item, parent, false)
        return FarmerViewHolder(listItemHolder)
    }

    override fun getItemCount(): Int = records.size


    internal fun setRecords(records: List<FarmersInformation>) {
        this.records = records
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(
        holder: FarmerViewHolder,
        position: Int
    ) {

        val currentInformation: FarmersInformation = records[position]
        holder.farmersFullNameList.text = currentInformation.fullName
        holder.farmersEmailList.text = currentInformation.email
        holder.farmersFarmAddressList.text = currentInformation.farmAddress
//        holder.farmersFarmCoordinatesList.text = currentInformation.farmCoordinates.toString()
        holder.farmersFarmNameList.text = currentInformation.farmName
        holder.farmersPhoneList.text = currentInformation.phoneNumber.toString()
//        holder.farmersImageList = currentInformation.image[]
    }


}