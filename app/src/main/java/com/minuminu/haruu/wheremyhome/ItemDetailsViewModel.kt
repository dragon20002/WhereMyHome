package com.minuminu.haruu.wheremyhome

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.minuminu.haruu.wheremyhome.dummy.DummyContent

class ItemDetailsViewModel : ViewModel() {
    val itemLiveData = MutableLiveData<DummyContent.HomeInfo>()
    val picDirLiveData = MutableLiveData<String>()

    fun setItemId(itemId: String) {
        Handler(Looper.getMainLooper()).postDelayed({
            DummyContent.ITEM_MAP[itemId]?.let { homeInfo ->
                itemLiveData.postValue(homeInfo)
                picDirLiveData.postValue(homeInfo.picDir)
            }
        }, 1000)
    }
}