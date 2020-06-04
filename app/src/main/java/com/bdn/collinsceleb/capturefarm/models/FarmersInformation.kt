package com.bdn.collinsceleb.capturefarm.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "farmers_information")
data class FarmersInformation (
    @PrimaryKey(autoGenerate = true) var id: Int,
    @ColumnInfo(name = "Full Name") var fullName: String,
    @ColumnInfo(name = "Phone Number") var phoneNumber : Long,
    @ColumnInfo(name = "Email Address") var email : String,
//    @ColumnInfo(name = "Farmer Picture") var image : String,
    @ColumnInfo(name = "Farm Name") var farmName : String,
    @ColumnInfo(name = "Farm Address") var farmAddress : String

//    @TypeConverters
//    var farmCoordinates : Location

//    @TypeConverters(Location::class)
//    @ColumnInfo(name = "Farm Coordinates")
//    var farmCoordinates : List<Location>
)