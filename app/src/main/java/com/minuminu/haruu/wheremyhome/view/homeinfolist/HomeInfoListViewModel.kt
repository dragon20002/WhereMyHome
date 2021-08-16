package com.minuminu.haruu.wheremyhome.view.homeinfolist

import androidx.databinding.ObservableArrayList
import androidx.databinding.ObservableList
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.minuminu.haruu.wheremyhome.db.AppDatabase
import com.minuminu.haruu.wheremyhome.db.data.HomeInfo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HomeInfoListViewModel : ViewModel() {
    private var db: AppDatabase? = null

    // db → viewModel
    val homeInfoListLiveData = MutableLiveData<List<HomeInfo>>()

    // viewModel → view
    val homeInfoList: ObservableList<HomeInfo> = ObservableArrayList()

    fun init(db: AppDatabase) {
        this.db = db
    }

    fun loadHomeInfoList() {
        CoroutineScope(Dispatchers.IO).launch {
            db?.homeInfoDao()?.getAll()?.let { homeInfoList ->
                homeInfoListLiveData.postValue(homeInfoList)
            }
        }
    }

}