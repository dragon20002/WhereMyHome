package com.minuminu.haruu.wheremyhome

import android.content.Context
import android.graphics.BitmapFactory
import android.os.Bundle
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
import com.google.android.material.textfield.TextInputEditText
import com.minuminu.haruu.wheremyhome.dummy.DummyContent

class ItemDetailsFragment : Fragment() {

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
        viewModel = ViewModelProvider(this).get(ItemDetailsViewModel::class.java)

        viewModel.itemLiveData.observe(viewLifecycleOwner, {
            Log.d(javaClass.name, it.toString())

            view?.findViewById<TextInputEditText>(R.id.et_name)?.text?.apply {
                clear()
                insert(0, it.name)
            }
            view?.findViewById<TextInputEditText>(R.id.et_address)?.text?.apply {
                clear()
                insert(0, it.address)
            }
            view?.findViewById<TextInputEditText>(R.id.et_deposit)?.text?.apply {
                clear()
                insert(0, it.deposit.toString())
            }
            view?.findViewById<TextInputEditText>(R.id.et_rental)?.text?.apply {
                clear()
                insert(0, it.rental.toString())
            }
            view?.findViewById<TextInputEditText>(R.id.et_expense)?.text?.apply {
                clear()
                insert(0, it.expense.toString())
            }
            view?.findViewById<TextInputEditText>(R.id.et_start_date)?.text?.apply {
                clear()
                insert(0, it.startDate)
            }
            view?.findViewById<TextInputEditText>(R.id.et_end_date)?.text?.apply {
                clear()
                insert(0, it.endDate)
            }

            val qandaLayoutIterator = view?.findViewById<LinearLayout>(R.id.qanda_list)?.iterator()
            qandaLayoutIterator?.run {
                it.qandas.forEach {
                    val qandaLayout = this.takeIf { hasNext() }?.next()
                    qandaLayout?.apply {
                        findViewById<TextView>(R.id.qanda_group)?.text = it.group
                        findViewById<TextView>(R.id.qanda_num)?.text = it.num.toString()
                        findViewById<TextView>(R.id.qanda_question)?.text = it.question
                        findViewById<LinearLayout>(R.id.qanda_answer)?.apply {
                            val child = takeIf { childCount > 0 }?.get(0)
                            child?.run {
                                when (it.type) {
                                    "Int" -> (this as EditText?)?.apply {
                                        setEms(4)
                                        maxEms = 4
                                        text.insert(0, it.answer)
                                        inputType =
                                            InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_SIGNED
                                        textAlignment = View.TEXT_ALIGNMENT_CENTER
                                    }
                                    else -> (this as CheckBox?)?.apply {
                                        isChecked = it.answer.toBoolean()
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

}