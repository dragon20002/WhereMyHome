package com.minuminu.haruu.wheremyhome

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.text.InputType
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.FileProvider
import androidx.core.view.get
import androidx.core.view.iterator
import androidx.core.view.setPadding
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.material.textfield.TextInputEditText
import com.minuminu.haruu.wheremyhome.db.AppDatabase
import com.minuminu.haruu.wheremyhome.dummy.DummyContent
import com.minuminu.haruu.wheremyhome.utils.Utils
import java.io.File
import java.io.IOException

class ItemDetailsFragment : Fragment(), MainActivity.OnBackPressed {

    companion object {
        private const val REQUEST_TAKE_PHOTO = 1

        fun newInstance() = ItemDetailsFragment()
    }

    private lateinit var viewModel: ItemDetailsViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.item_details_fragment, container, false)

        view.findViewById<ImageButton>(R.id.btn_camera).setOnClickListener {
            dispatchTakePictureIntent()
        }

        view.findViewById<ImageButton>(R.id.btn_location).setOnClickListener {
            // TODO : popup google map
            Log.d(javaClass.name, "TODO : popup google map")
        }

        // Q&A Form template
        val listContainer = view.findViewById<LinearLayout>(R.id.qanda_list)
        DummyContent.createQandaTemplate().forEach {
            val qandaLayout = inflater.inflate(R.layout.fragment_qanda, listContainer, false)
            with(qandaLayout) {
                findViewById<TextView>(R.id.qanda_group)?.text = it.group
                findViewById<TextView>(R.id.qanda_num)?.text = it.num.toString()
                findViewById<TextView>(R.id.qanda_question)?.text = it.question
                findViewById<LinearLayout>(R.id.qanda_answer)?.addView(when (it.type) {
                    "Int" -> EditText(context).apply {
                        setEms(4)
                        maxEms = 4
                        text.insert(0, it.answer)
                        inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_SIGNED
                        textAlignment = View.TEXT_ALIGNMENT_CENTER
                    }
                    else -> CheckBox(context).apply {
                        isChecked = it.answer.toBoolean()
                    } //"Boolean"
                })
            }
            listContainer.addView(qandaLayout)
        }

        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(ItemDetailsViewModel::class.java).apply {
            setDatabase(AppDatabase.getDatabase(requireContext()))
        }

        viewModel.itemLiveData.observe(viewLifecycleOwner, {
            Log.d(javaClass.name, it.toString())

            view?.findViewById<TextInputEditText>(R.id.et_name)?.text?.apply {
                clear()
                insert(0, it.homeInfo.name)
            }
            view?.findViewById<TextInputEditText>(R.id.et_address)?.text?.apply {
                clear()
                insert(0, it.homeInfo.address)
            }
            view?.findViewById<TextInputEditText>(R.id.et_deposit)?.text?.apply {
                clear()
                insert(0, it.homeInfo.deposit.toString())
            }
            view?.findViewById<TextInputEditText>(R.id.et_rental)?.text?.apply {
                clear()
                insert(0, it.homeInfo.rental.toString())
            }
            view?.findViewById<TextInputEditText>(R.id.et_expense)?.text?.apply {
                clear()
                insert(0, it.homeInfo.expense.toString())
            }
            view?.findViewById<TextInputEditText>(R.id.et_start_date)?.text?.apply {
                clear()
                insert(0, it.homeInfo.startDate)
            }
            view?.findViewById<TextInputEditText>(R.id.et_end_date)?.text?.apply {
                clear()
                insert(0, it.homeInfo.endDate)
            }

            val qandaLayoutIterator = view?.findViewById<LinearLayout>(R.id.qanda_list)?.iterator()
            qandaLayoutIterator?.run {
                Log.d(javaClass.name, "load qanda size : ${it.qandas.size}")

                it.qandas.forEach { qanda ->
                    Log.d(javaClass.name, "load qanda : $qanda")

                    val qandaLayout = this.takeIf { hasNext() }?.next()
                    qandaLayout?.apply {
                        findViewById<TextView>(R.id.qanda_group)?.text = qanda.group
                        findViewById<TextView>(R.id.qanda_num)?.text = qanda.num.toString()
                        findViewById<TextView>(R.id.qanda_question)?.text = qanda.question
                        findViewById<LinearLayout>(R.id.qanda_answer)?.apply {
                            val child = takeIf { childCount > 0 }?.get(0)
                            child?.run {
                                when (qanda.type) {
                                    "Int" -> (this as EditText?)?.apply {
                                        setEms(4)
                                        maxEms = 4
                                        text.insert(0, qanda.answer)
                                        inputType =
                                            InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_SIGNED
                                        textAlignment = View.TEXT_ALIGNMENT_CENTER
                                    }
                                    else -> (this as CheckBox?)?.apply {
                                        isChecked = qanda.answer.toBoolean()
                                    } //"Boolean"
                                }
                            }
                        }
                    }
                }
            }
        })

        viewModel.picturesLiveData.observe(viewLifecycleOwner, {
            Log.d(javaClass.name, "pictures - observe : ${it.size}")

            view?.findViewById<LinearLayout>(R.id.picture_list)?.run {
                while (childCount > 1)
                    removeViewAt(1)

                val widthPx: Float = Utils.dp2px(requireContext(), 80f)
                val heightPx: Float = Utils.dp2px(requireContext(), 100f)

                Log.d(javaClass.name, "width px : $widthPx, height px : $heightPx")

                it.forEach { picture ->
                    ImageView(requireContext()).apply {
                        layoutParams = ViewGroup.LayoutParams(widthPx.toInt(), heightPx.toInt())
                        setPadding(
                            0,
                            resources.getDimensionPixelSize(R.dimen.margin_small),
                            resources.getDimensionPixelSize(R.dimen.margin_small),
                            resources.getDimensionPixelSize(R.dimen.margin_small),
                        )
                        scaleType = ImageView.ScaleType.FIT_CENTER
                        val imageFile = Utils.loadImageFile(requireContext(), picture.name).let { bitmap ->
                            Utils.resizeBitmap(bitmap, widthPx, heightPx)
                        }
                        setImageBitmap(imageFile)
                        addView(this)
                    }
                }
            }
        })

        arguments?.run {
            getString("itemId")?.let {
                viewModel.setItemId(it)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
            viewModel.addPicture(DummyContent.Picture(null, viewModel.currentImageName))
        }
    }

    override fun onBackPressed(): Boolean {
        val isSaved = viewModel.isSaved
        if (isSaved)
            return true

        val qandas =
            (viewModel.itemLiveData.value?.qandas ?: DummyContent.createQandaTemplate()).apply {
                val qandaLayoutIterator =
                    view?.findViewById<LinearLayout>(R.id.qanda_list)?.iterator()
                qandaLayoutIterator?.run {
                    Log.d(javaClass.name, "save qanda size : ${this@apply.size}")

                    this@apply.forEach { qanda ->
                        Log.d(javaClass.name, "save qanda : $qanda")

                        val qandaLayout = this.takeIf { hasNext() }?.next()
                        qandaLayout?.run {
                            qanda.group =
                                findViewById<TextView>(R.id.qanda_group)?.text?.toString().orEmpty()
                            qanda.num = findViewById<TextView>(R.id.qanda_num)?.text?.toString()
                                ?.toIntOrNull() ?: 0
                            qanda.question =
                                findViewById<TextView>(R.id.qanda_question)?.text?.toString()
                                    .orEmpty()
                            qanda.answer =
                                findViewById<LinearLayout>(R.id.qanda_answer)?.let { layout ->
                                    val child = layout.takeIf { layout.childCount > 0 }?.get(0)
                                    child?.let { view ->
                                        when (qanda.type) {
                                            "Int" -> (view as EditText?)?.text?.toString().orEmpty()
                                            else -> (view as CheckBox?)?.isChecked?.toString() //"Boolean"
                                        }
                                    }
                                }.orEmpty()
                        }
                    }
                }
            }

        val score = qandas.let {
            var sum = 0
            it.forEach { qanda ->
                var answer = 0
                if (qanda.type == "Int") {
                    answer = qanda.answer.toIntOrNull() ?: 0
                } else if (qanda.answer.toBoolean()) {
                    answer = 1
                }
                sum += answer
            }
            sum
        }

        val homeInfo = DummyContent.HomeInfoWithQandas(
            homeInfo = DummyContent.HomeInfo(
                viewModel.itemLiveData.value?.homeInfo?.id,
                view?.findViewById<TextInputEditText>(R.id.et_name)?.text?.toString().orEmpty(),
                view?.findViewById<TextInputEditText>(R.id.et_address)?.text?.toString().orEmpty(),
                view?.findViewById<TextInputEditText>(R.id.et_deposit)?.text?.toString()
                    ?.toIntOrNull() ?: 0,
                view?.findViewById<TextInputEditText>(R.id.et_rental)?.text?.toString()
                    ?.toIntOrNull() ?: 0,
                view?.findViewById<TextInputEditText>(R.id.et_expense)?.text?.toString()
                    ?.toFloatOrNull() ?: 0f,
                view?.findViewById<TextInputEditText>(R.id.et_start_date)?.text?.toString()
                    .orEmpty(),
                view?.findViewById<TextInputEditText>(R.id.et_end_date)?.text?.toString().orEmpty(),
                score,
            ),
            qandas = qandas,
            pictures = viewModel.picturesLiveData.value ?: ArrayList(),
        )

        Thread {
            // DB Insert
            homeInfo.let {
                if (it.homeInfo.id == null) { // Add
                    val ids = viewModel.db.homeInfoDao().insertAll(it.homeInfo)
                    Log.d(javaClass.name, "inserted homeInfo ${ids[0]}")

                    for (qanda in it.qandas) {
                        qanda.homeInfoId = ids[0]
                        val qandaIds = viewModel.db.qandaDao().insertAll(qanda)
                        Log.d(javaClass.name, "inserted qanda ${qandaIds[0]}")
                    }

                    for (picture in it.pictures) {
                        picture.homeInfoId = ids[0]
                        val pictureIds = viewModel.db.pictureDao().insertAll(picture)
                        Log.d(javaClass.name, "inserted picture ${pictureIds[0]}")
                    }

                } else { // Update
                    val cnt = viewModel.db.homeInfoDao().updateAll(it.homeInfo)
                    Log.d(javaClass.name, "updated homeInfo $cnt")

                    for (qanda in it.qandas) {
                        qanda.homeInfoId = it.homeInfo.id
                        val qandaCnt = viewModel.db.qandaDao().updateAll(qanda)
                        Log.d(javaClass.name, "updated qanda $qandaCnt")
                    }

                    for (picture in it.pictures) {
                        if (picture.homeInfoId == null) {
                            picture.homeInfoId = it.homeInfo.id
                            val pictureIds = viewModel.db.pictureDao().insertAll(picture)
                            Log.d(javaClass.name, "inserted picture ${pictureIds[0]}")
                        } else {
                            val pictureCnt = viewModel.db.pictureDao().updateAll(picture)
                            Log.d(javaClass.name, "updated picture $pictureCnt")
                        }
                    }
                }
            }

            // Notify data changed
            findNavController().currentBackStackEntry?.savedStateHandle?.set(
                "item",
                homeInfo.homeInfo
            )

            if (!viewModel.isSaved) {
                viewModel.isSaved = true
                Handler(Looper.getMainLooper()).post {
                    activity?.onBackPressed()
                }
            }
        }.start()

        return false
    }

    private fun dispatchTakePictureIntent() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            takePictureIntent.resolveActivity(requireContext().packageManager!!)?.also {
                // Create file
                val imageFile: File? = try {
                    Utils.createImageFile(requireContext())
                } catch (ex: IOException) {
                    Log.e(javaClass.name, "Fail to create image file")
                    null
                }

                // Request Take photo
                imageFile?.also {
                    viewModel.currentImageName = it.name

                    val imageUri: Uri = FileProvider.getUriForFile(
                        requireContext(),
                        "com.minuminu.haruu.wheremyhome",
                        it
                    )
                    Log.d(javaClass.name, "imageUri = $imageUri")

                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
                    startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO)
                }
            }
        }
    }
}