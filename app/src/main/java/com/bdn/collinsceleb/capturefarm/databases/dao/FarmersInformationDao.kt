package com.bdn.collinsceleb.capturefarm.databases.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.bdn.collinsceleb.capturefarm.models.FarmersInformation

@Dao
interface FarmersInformationDao {

    @Query(value = "SELECT * from farmers_information ORDER BY `Full Name` ASC")
    fun getFarmersRecord() : LiveData<List<FarmersInformation>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertFarmerInformation(farmersInformation: FarmersInformation)

    @Update(onConflict = OnConflictStrategy.IGNORE)
    suspend fun updateFarmerInformation(farmersInformation: FarmersInformation)

    @Query(value = "DELETE FROM farmers_information")
    suspend fun deleteAll()
}