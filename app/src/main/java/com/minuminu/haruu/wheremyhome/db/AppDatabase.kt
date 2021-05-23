package com.minuminu.haruu.wheremyhome.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.minuminu.haruu.wheremyhome.db.dao.HomeInfoDao
import com.minuminu.haruu.wheremyhome.db.dao.PictureDao
import com.minuminu.haruu.wheremyhome.db.dao.QandaDao
import com.minuminu.haruu.wheremyhome.db.data.HomeInfo
import com.minuminu.haruu.wheremyhome.db.data.Picture
import com.minuminu.haruu.wheremyhome.db.data.Qanda

@Database(
    entities = [HomeInfo::class, Qanda::class, Picture::class],
    version = 1
)
abstract class AppDatabase : RoomDatabase() {
    companion object {
        private lateinit var context: Context
        private val database: AppDatabase by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
            Room.databaseBuilder(context, AppDatabase::class.java, "myhome.db").build()
        }

        fun getDatabase(context: Context): AppDatabase {
            this.context = context.applicationContext
            return database
        }
    }

    abstract fun homeInfoDao(): HomeInfoDao
    abstract fun qandaDao(): QandaDao
    abstract fun pictureDao(): PictureDao
}