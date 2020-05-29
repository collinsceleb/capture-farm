package com.bdn.collinsceleb.capturefarm.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.bdn.collinsceleb.capturefarm.databases.FarmersInformationDatabase
import com.bdn.collinsceleb.capturefarm.databases.repository.FarmersRepository
import com.bdn.collinsceleb.capturefarm.models.FarmersInformation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FarmerViewModel(application: Application) : AndroidViewModel(application) {

    private var repository: FarmersRepository? = null
    var allFarmersInformation: LiveData<List<FarmersInformation>>? = null

    init {
        val farmersInformationDao =
            FarmersInformationDatabase.getDatabase(application, viewModelScope).farmersInformationDao()
        repository = FarmersRepository(farmersInformationDao)
        allFarmersInformation = repository!!.allRecords
    }

    fun insert(farmersInformation: FarmersInformation) = viewModelScope.launch( Dispatchers.IO) {
        repository?.insertFarmersInformation(farmersInformation)
    }

}