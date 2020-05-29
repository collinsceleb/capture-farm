package com.bdn.collinsceleb.capturefarm.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bdn.collinsceleb.capturefarm.R
import com.bdn.collinsceleb.capturefarm.adapters.FarmersListItemRecyclerAdapter
import com.bdn.collinsceleb.capturefarm.viewmodels.FarmerViewModel

class MainActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var farmerViewModel: FarmerViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerView = findViewById(R.id.recycler)
        val adapter = FarmersListItemRecyclerAdapter(this)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        farmerViewModel = ViewModelProvider(this).get(FarmerViewModel::class.java)
        farmerViewModel.allFarmersInformation?.observe(this, Observer { records ->
            records.let { adapter.setRecords(it) }
        })
    }

//    companion object {
//        private const val farmersActivityRequestCode = 1
//    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
    }
}
