package com.minuminu.haruu.wheremyhome.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.minuminu.haruu.wheremyhome.dao.HomeInfoDao
import com.minuminu.haruu.wheremyhome.dao.PictureDao
import com.minuminu.haruu.wheremyhome.dao.QandaDao
import com.minuminu.haruu.wheremyhome.dummy.DummyContent

@Database(
    entities = [DummyContent.HomeInfo::class, DummyContent.Qanda::class, DummyContent.Picture::class],
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