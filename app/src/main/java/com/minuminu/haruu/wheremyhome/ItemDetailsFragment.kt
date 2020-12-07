package com.minuminu.haruu.wheremyhome

import android.content.Context
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.InputType
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.*
import androidx.core.view.get
import androidx.core.view.iterator
import androidx.core.view.setPadding
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.material.textfield.TextInputEditText
import com.minuminu.haruu.wheremyhome.db.AppDatabase
import com.minuminu.haruu.wheremyhome.dummy.DummyContent

class ItemDetailsFragment : Fragment(), MainActivity.OnBackPressed {

    companion object {
        fun newInstance() = ItemDetailsFragment()
    }

    private lateinit var viewModel: ItemDetailsViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.item_details_fragment, container, false)

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

        viewModel.picDirLiveData.observe(viewLifecycleOwner, {
            Log.d(javaClass.name, it.toString())

            view?.findViewById<LinearLayout>(R.id.picture_list)?.run {
                while (childCount > 1)
                    removeViewAt(1)

                val densityDpi: Int = DisplayMetrics().apply {
                    (context?.getSystemService(Context.WINDOW_SERVICE) as WindowManager?)?.let {
                        display.getRealMetrics(this)
                    }
                }.densityDpi
                val widthPx: Int = 80 * densityDpi
                val heightPx: Int = 100 * densityDpi

                context?.getFileStreamPath(it)?.listFiles()?.forEach { file ->
                    file.takeIf { it.isFile }?.absolutePath?.let {
                        // Get Bitmap
                        BitmapFactory.decodeFile(it)
                    }?.let {
                        // Create ImageView
                        ImageView(context).apply {
                            layoutParams = ViewGroup.LayoutParams(widthPx, heightPx)
                            setPadding(resources.getDimensionPixelSize(R.dimen.margin_mid))
                            setImageBitmap(it)
                            scaleType = ImageView.ScaleType.FIT_CENTER
                        }
                    }?.let { imageView ->
                        // Attach ImageView
                        addView(imageView)
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

    override fun onBackPressed(): Boolean {
        val isSaved = viewModel.isSaved
        if (isSaved)
            return true

        val qandas = (viewModel.itemLiveData.value?.qandas ?: DummyContent.createQandaTemplate()).apply {
            val qandaLayoutIterator = view?.findViewById<LinearLayout>(R.id.qanda_list)?.iterator()
            qandaLayoutIterator?.run {
                Log.d(javaClass.name, "save qanda size : ${this@apply.size}")

                this@apply.forEach { qanda ->
                    Log.d(javaClass.name, "save qanda : $qanda")

                    val qandaLayout = this.takeIf { hasNext() }?.next()
                    qandaLayout?.run {
                        qanda.group = findViewById<TextView>(R.id.qanda_group)?.text?.toString().orEmpty()
                        qanda.num = findViewById<TextView>(R.id.qanda_num)?.text?.toString()?.toIntOrNull() ?: 0
                        qanda.question = findViewById<TextView>(R.id.qanda_question)?.text?.toString().orEmpty()
                        qanda.answer = findViewById<LinearLayout>(R.id.qanda_answer)?.let { layout ->
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
                view?.findViewById<TextInputEditText>(R.id.et_deposit)?.text?.toString()?.toIntOrNull() ?: 0,
                view?.findViewById<TextInputEditText>(R.id.et_rental)?.text?.toString()?.toIntOrNull() ?: 0,
                view?.findViewById<TextInputEditText>(R.id.et_expense)?.text?.toString()?.toFloatOrNull() ?: 0f,
                view?.findViewById<TextInputEditText>(R.id.et_start_date)?.text?.toString().orEmpty(),
                view?.findViewById<TextInputEditText>(R.id.et_end_date)?.text?.toString().orEmpty(),
                score,
            ),
            qandas = qandas,
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
                } else { // Update
                    val cnt = viewModel.db.homeInfoDao().updateAll(it.homeInfo)
                    Log.d(javaClass.name, "updated homeInfo $cnt")

                    for (qanda in it.qandas) {
                        qanda.homeInfoId = it.homeInfo.id!!
                        val qandaCnt = viewModel.db.qandaDao().updateAll(qanda)
                        Log.d(javaClass.name, "updated qanda $qandaCnt")
                    }
                }
            }

            // Notify data changed
            findNavController().currentBackStackEntry?.savedStateHandle?.set("item", homeInfo.homeInfo)

            if (!viewModel.isSaved) {
                viewModel.isSaved = true
                Handler(Looper.getMainLooper()).post {
                    activity?.onBackPressed()
                }
            }
        }.start()

        return false
    }
}