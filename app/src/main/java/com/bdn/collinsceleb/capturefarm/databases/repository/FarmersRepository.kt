package com.bdn.collinsceleb.capturefarm.databases.repository

import androidx.lifecycle.LiveData
import com.bdn.collinsceleb.capturefarm.databases.dao.FarmersInformationDao
import com.bdn.collinsceleb.capturefarm.models.FarmersInformation

class FarmersRepository(private val farmersInformationDao: FarmersInformationDao) {

    val allRecords: LiveData<List<FarmersInformation>> = farmersInformationDao.getFarmersRecord()

    suspend fun insertFarmersInformation(farmersInformation: FarmersInformation) {
        farmersInformationDao.insertFarmerInformation(farmersInformation)
    }
}