package com.minuminu.haruu.wheremyhome.view.evalformgrouplist

import androidx.databinding.ObservableArrayList
import androidx.databinding.ObservableField
import androidx.databinding.ObservableList
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.minuminu.haruu.wheremyhome.db.AppDatabase
import com.minuminu.haruu.wheremyhome.db.data.EvalFormGroup
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class EvalFormGroupListViewModel : ViewModel() {
    private var db: AppDatabase? = null

    val evalFormGroupListLiveData = MutableLiveData<List<EvalFormGroup>>()

    val evalFormGroupList: ObservableList<EvalFormGroup> = ObservableArrayList()
    val checked = ObservableField(-1L)

    fun init(db: AppDatabase) {
        this.db = db
    }

    fun loadEvalFormGroupList() {
        CoroutineScope(Dispatchers.IO).launch {
            db?.evalFormGroupDao()?.getAll()?.let { evalFormGroupList ->
                evalFormGroupListLiveData.postValue(evalFormGroupList)
            }
        }
    }
}