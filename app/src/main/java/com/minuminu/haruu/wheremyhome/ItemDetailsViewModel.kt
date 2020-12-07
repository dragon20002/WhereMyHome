package com.minuminu.haruu.wheremyhome

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.minuminu.haruu.wheremyhome.db.AppDatabase
import com.minuminu.haruu.wheremyhome.dummy.DummyContent

class ItemDetailsViewModel : ViewModel() {
    lateinit var db: AppDatabase

    var isSaved = false
    var currentImageName = ""

    val itemLiveData = MutableLiveData<DummyContent.HomeInfoWithQandas>()
    val picturesLiveData = MutableLiveData<List<DummyContent.Picture>>()

    fun setDatabase(db: AppDatabase) {
        this.db = db
    }

    fun setItemId(itemId: String) {
        Thread {
            db.homeInfoDao().loadAllByIds(arrayOf(itemId)).takeIf {
                it.isNotEmpty()
            }?.get(0)?.let { homeInfo ->
                itemLiveData.postValue(homeInfo)
                picturesLiveData.postValue(homeInfo.pictures)
            }
        }.start()
    }

    fun addPicture(picture: DummyContent.Picture) {
        val list = (picturesLiveData.value ?: ArrayList()).plus(picture)
        picturesLiveData.postValue(list)
    }
}