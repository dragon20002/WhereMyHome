package com.minuminu.haruu.wheremyhome

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.minuminu.haruu.wheremyhome.dummy.DummyContent

class ItemDetailsViewModel : ViewModel {
    val itemLiveData = MutableLiveData<DummyContent.DummyItem>()

    constructor() : super() {
        // trigger item laod
        Log.d(javaClass.name, "TODO : load data")
    }

    fun popupGoogleMap() {
        // depending on the action, do necessary business logic calls and update the itemLiveData.
        Log.d(javaClass.name, "TODO : popup google map")
    }
}