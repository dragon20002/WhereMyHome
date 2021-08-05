package com.minuminu.haruu.wheremyhome.view.homeinfodetails

import android.util.Log
import androidx.databinding.ObservableArrayList
import androidx.databinding.ObservableField
import androidx.databinding.ObservableList
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.minuminu.haruu.wheremyhome.db.AppDatabase
import com.minuminu.haruu.wheremyhome.db.data.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeInfoDetailsViewModel : ViewModel() {
    private var db: AppDatabase? = null

    var currentImageName = ""

    // db - viewModel
    val homeInfoLiveData = MutableLiveData<HomeInfo>()
    val pictureListLiveData = MutableLiveData<List<PictureViewData>>()
    val qandaListLiveData = MutableLiveData<List<QandaViewData>>()

    // viewModel - view
    val isEditing = ObservableField(false)
    val name = ObservableField<String>()
    val address = ObservableField<String>()
    val deposit = ObservableField<String>()
    val rental = ObservableField<String>()
    val expense = ObservableField<String>()
    val startDate = ObservableField<String>()
    val endDate = ObservableField<String>()
    val thumbnail = ObservableField<String>()
    val pictureList: ObservableList<PictureViewData> = ObservableArrayList()
    val qandaList: ObservableList<QandaViewData> = ObservableArrayList()

    fun init(db: AppDatabase) {
        this.db = db
    }

    fun loadHomeInfo(homeInfoId: Long) {
        CoroutineScope(Dispatchers.IO).launch {
            db?.homeInfoDao()?.getOneById(homeInfoId)?.let { homeInfo ->
                homeInfoLiveData.postValue(homeInfo)
            }
        }
    }

    fun loadPictureList(homeInfoId: Long) {
        CoroutineScope(Dispatchers.IO).launch {
            db?.pictureDao()?.getAllByHomeInfoId(homeInfoId)?.let { pictureList ->
                pictureListLiveData.postValue(pictureList.map {
                    PictureViewData(
                        it.id,
                        it.name,
                        it.homeInfoId,
                        false
                    )
                })
            }
        }
    }

    fun loadQandaList(homeInfoId: Long) {
        CoroutineScope(Dispatchers.IO).launch {
            db?.qandaDao()?.getAllByHomeInfoId(homeInfoId)?.let { qandaList ->
                qandaListLiveData.postValue(qandaList.map {
                    QandaViewData(
                        it.id,
                        it.group,
                        it.num.toString(),
                        it.question,
                        it.type,
                        it.answer,
                        it.remark,
                    )
                })
            }
        }
    }

    suspend fun saveItem(): HomeInfo {
        val score: Int = qandaList.let {
            var sum = 0
            it.forEach { qanda ->
                val answer = when (qanda.type) {
                    "Int" -> qanda.strAnswer.toIntOrNull() ?: 0
                    "Boolean" -> when (qanda.blnAnswer) {
                        true -> 1
                        else -> 0
                    }
                    else -> 0
                }
                sum += answer
            }
            sum
        }

        val homeInfoWithQandas = HomeInfoWithQandas(
            homeInfo = HomeInfo(
                homeInfoLiveData.value?.id,
                name.get(),
                address.get(),
                deposit.get()?.toIntOrNull() ?: 0,
                rental.get()?.toIntOrNull() ?: 0,
                expense.get()?.toFloatOrNull() ?: 0f,
                startDate.get(),
                endDate.get(),
                score,
                thumbnail.get(),
            ),
            qandas = qandaList.map {
                Qanda(
                    it.id, it.group, it.num.toIntOrNull() ?: 0,
                    it.question, it.type, it.strAnswer, it.remark, null
                )
            },
            pictures = pictureList.filter { !it.deleted }.map {
                Picture(it.id, it.name, it.homeInfoId)
            }
        )

        return withContext(Dispatchers.IO) {
            // DB Insert
            homeInfoWithQandas.apply {
                if (homeInfo.id == null) { // Add
                    val ids = db?.homeInfoDao()?.insertAll(homeInfo)
                    Log.d(javaClass.name, "inserted homeInfo ${ids?.get(0)}")

                    ids?.get(0)?.let { id ->
                        for (qanda in qandas) {
                            qanda.homeInfoId = id
                            val qandaIds = db?.qandaDao()?.insertAll(qanda)
                            Log.d(javaClass.name, "inserted qanda ${qandaIds?.get(0)}")
                        }

                        for (picture in pictures) {
                            picture.homeInfoId = id
                            val pictureIds = db?.pictureDao()?.insertAll(picture)
                            Log.d(javaClass.name, "inserted picture ${pictureIds?.get(0)}")
                        }
                    }

                } else { // Update
                    val cnt = db?.homeInfoDao()?.updateAll(homeInfo)
                    Log.d(javaClass.name, "updated homeInfo $cnt")

                    if (cnt != null) {
                        for (qanda in qandas) {
                            qanda.homeInfoId = homeInfo.id
                            val qandaCnt = db?.qandaDao()?.updateAll(qanda)
                            Log.d(javaClass.name, "updated qanda $qandaCnt")
                        }

                        for (picture in pictures) {
                            if (picture.homeInfoId == null) {
                                picture.homeInfoId = homeInfo.id
                                val pictureIds = db?.pictureDao()?.insertAll(picture)
                                Log.d(
                                    javaClass.name,
                                    "inserted picture ${pictureIds?.get(0)}"
                                )
                            } else {
                                val pictureCnt = db?.pictureDao()?.updateAll(picture)
                                Log.d(javaClass.name, "updated picture $pictureCnt")
                            }
                        }

                        pictureList.filter { it.deleted }.forEach {
                            db?.pictureDao()?.delete(
                                Picture(
                                    it.id, it.name, it.homeInfoId
                                )
                            )
                        }
                    }
                }
            }.homeInfo
        }
    }
}