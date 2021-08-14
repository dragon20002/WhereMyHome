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

    fun loadQuestionList(questionGroupId: Long) {
        CoroutineScope(Dispatchers.IO).launch {
            val questionList = db?.questionDao()?.getAllByQuestionInfoId(questionGroupId)

            qandaListLiveData.postValue(
                questionList?.map { question ->
                    QandaViewData(
                        question.id,
                        null,
                        question.category,
                        question.num.toString(),
                        question.content,
                        question.type,
                        "",
                        ""
                    )
                }
            )
        }
    }

    fun loadAnswerList(homeInfoId: Long) {
        CoroutineScope(Dispatchers.IO).launch {
            val answerList = db?.answerDao()?.getAllByHomeInfoId(homeInfoId)

            qandaListLiveData.postValue(
                answerList?.filter { answer -> answer.questionId != null }?.map { answer ->
                    val question = db?.questionDao()?.getOneById(answer.questionId!!)

                    if (question != null) {
                        QandaViewData(
                            question.id,
                            answer.id,
                            question.category,
                            question.num.toString(),
                            question.content,
                            question.type,
                            answer.content,
                            answer.remark,
                        )
                    } else {
                        QandaViewData(
                            null,
                            answer.id,
                            "",
                            "",
                            "",
                            "",
                            answer.content,
                            answer.remark
                        )
                    }
                })
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

        val homeInfoWithQandas = HomeInfoWithAnswers(
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
            answers = qandaList.map {
                Answer(
                    it.answerId, it.strAnswer, it.remark, it.questionId, null
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
                        for (qanda in answers) {
                            qanda.homeInfoId = id
                            val answerIds = db?.answerDao()?.insertAll(qanda)
                            Log.d(javaClass.name, "inserted answer ${answerIds?.get(0)}")
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
                        for (answer in answers) {
                            answer.homeInfoId = homeInfo.id
                            val answerCnt = db?.answerDao()?.updateAll(answer)
                            Log.d(javaClass.name, "updated answer $answerCnt")
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