package com.minuminu.haruu.wheremyhome

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.minuminu.haruu.wheremyhome.db.AppDatabase
import com.minuminu.haruu.wheremyhome.dummy.DummyContent

class ItemDetailsViewModel : ViewModel() {
    lateinit var db: AppDatabase

    var isSaved = false
    val itemLiveData = MutableLiveData<DummyContent.HomeInfoWithQandas>()
    val picDirLiveData = MutableLiveData<String>()

    fun setDatabase(db: AppDatabase) {
        this.db = db
    }

    fun setItemId(itemId: String) {
        Thread {
            db.homeInfoDao().loadAllByIds(arrayOf(itemId)).takeIf {
                it.isNotEmpty()
            }?.get(0)?.let { homeInfo ->
                itemLiveData.postValue(homeInfo)
            }
        }.start()
    }
}