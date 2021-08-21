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
    val evalInfoListLiveData = MutableLiveData<List<EvalInfoViewData>>()

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
    val area = ObservableField<String>()
    val floor = ObservableField<String>()
    val realtorTelNo = ObservableField<String>()
    val ownerTelNo = ObservableField<String>()
    val dealTypeDescription = ObservableField<String>()
    val pictureList: ObservableList<PictureViewData> = ObservableArrayList()
    val evalInfoList: ObservableList<EvalInfoViewData> = ObservableArrayList()

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

    fun loadEvalFormList(evalFormGroupId: Long) {
        CoroutineScope(Dispatchers.IO).launch {
            val evalFormList = db?.evalFormDao()?.getAllByEvalFormGroupId(evalFormGroupId)

            evalInfoListLiveData.postValue(
                evalFormList?.map { evalForm ->
                    EvalInfoViewData(
                        null,
                        evalForm.category,
                        evalForm.num.toString(),
                        evalForm.content,
                        evalForm.method,
                        evalForm.weight.toString(),
                        "",
                        ""
                    )
                }
            )
        }
    }

    fun loadEvalInfoList(homeInfoId: Long) {
        CoroutineScope(Dispatchers.IO).launch {
            val evalInfoList = db?.evalInfoDao()?.getAllByHomeInfoId(homeInfoId)

            evalInfoListLiveData.postValue(
                evalInfoList?.map { evalInfo ->
                    EvalInfoViewData(
                        evalInfo.id,
                        evalInfo.category,
                        evalInfo.num.toString(),
                        evalInfo.content,
                        evalInfo.method,
                        evalInfo.weight.toString(),
                        evalInfo.result,
                        evalInfo.remark,
                    )
                })
        }
    }

    suspend fun saveItem(): HomeInfo {
        val score: Float = evalInfoList.let { _evalInfoList ->
            var sum = 0f
            _evalInfoList.forEach { evalInfo ->
                val answer = when (evalInfo.method) {
                    "Int" -> evalInfo.result.toFloatOrNull() ?: 0f
                    "Boolean" -> when (evalInfo.blnResult) {
                        true -> 1f
                        else -> 0f
                    }
                    else -> 0f
                }
                sum += (answer.times(evalInfo.weight.toFloatOrNull() ?: 1f))
            }
            sum
        }

        val homeInfoViewData = HomeInfoViewData(
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
                area.get()?.toFloatOrNull() ?: 0f,
                floor.get()?.toIntOrNull() ?: 0,
                realtorTelNo.get(),
                ownerTelNo.get(),
                dealTypeDescription.get()?.let { desc ->
                    HomeInfoDealType.values()
                        .find { dealType -> dealType.description.contentEquals(desc) }
                }?.name ?: HomeInfoDealType.Monthly.name
            ),
            evalInfos = evalInfoList.map { evalInfo ->
                EvalInfo(
                    evalInfo.evalInfoId,
                    evalInfo.category,
                    evalInfo.num.toInt(),
                    evalInfo.content,
                    evalInfo.method,
                    evalInfo.weight.toFloatOrNull() ?: 1f,
                    evalInfo.result,
                    evalInfo.remark,
                    null
                )
            },
            pictures = pictureList.filter { !it.deleted }.map { picture ->
                Picture(picture.id, picture.name, picture.homeInfoId)
            }
        )

        return withContext(Dispatchers.IO) {
            // DB Insert
            homeInfoViewData.apply {
                if (homeInfo.id == null) { // Add
                    val ids = db?.homeInfoDao()?.insertAll(homeInfo)
                    Log.d(javaClass.name, "inserted homeInfo ${ids?.get(0)}")

                    ids?.get(0)?.let { id ->
                        for (evalInfo in evalInfos) {
                            evalInfo.homeInfoId = id
                            val evalInfoIds = db?.evalInfoDao()?.insertAll(evalInfo)
                            Log.d(javaClass.name, "inserted evalInfo ${evalInfoIds?.get(0)}")
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
                        for (evalInfo in evalInfos) {
                            evalInfo.homeInfoId = homeInfo.id
                            val evalInfoCnt = db?.evalInfoDao()?.updateAll(evalInfo)
                            Log.d(javaClass.name, "updated evalInfo $evalInfoCnt")
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

                        pictureList.filter { it.deleted }.forEach { picture ->
                            db?.pictureDao()?.delete(
                                // TODO `picture`를 바로 넣어도 되지 않나??
                                Picture(
                                    picture.id, picture.name, picture.homeInfoId
                                )
                            )
                        }
                    }
                }
            }.homeInfo
        }
    }
}