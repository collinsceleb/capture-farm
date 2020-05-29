package com.bdn.collinsceleb.capturefarm.databases

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.bdn.collinsceleb.capturefarm.databases.dao.FarmersInformationDao
import com.bdn.collinsceleb.capturefarm.models.FarmersInformation
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch


@Database(entities = [FarmersInformation::class], version = 1, exportSchema = false)
abstract class FarmersInformationDatabase : RoomDatabase() {
    abstract fun farmersInformationDao(): FarmersInformationDao

    private class FarmersInformationDatabaseCallback(
        private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {
        override fun onOpen(db: SupportSQLiteDatabase) {
            super.onOpen(db)
            INSTANCE.let { database ->
                scope.launch {
                    var farmersInformationDao = database?.farmersInformationDao()
                    farmersInformationDao?.deleteAll()
                }
            }
        }
    }

    companion object {

        @Volatile
        private var INSTANCE: FarmersInformationDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): FarmersInformationDatabase {
            val tempInstance =
                INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    FarmersInformationDatabase::class.java,
                    "capture_farm"
                ).build()
                INSTANCE = instance
                return instance
            }
        }
    }
}