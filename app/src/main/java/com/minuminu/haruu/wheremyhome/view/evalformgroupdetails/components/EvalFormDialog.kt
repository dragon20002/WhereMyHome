package com.minuminu.haruu.wheremyhome.view.evalformgroupdetails.components

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import com.minuminu.haruu.wheremyhome.R
import com.minuminu.haruu.wheremyhome.databinding.DialogEvalFormBinding
import com.minuminu.haruu.wheremyhome.db.data.EvalFormViewData
import com.minuminu.haruu.wheremyhome.db.data.EvalInfoMethod

class EvalFormDialog : DialogFragment() {
    var caller: View? = null
    var listener: EvalFormDialogListener? = null
    var evalForm: EvalFormViewData? = null

    private val typeDescriptions = EvalInfoMethod.values().map { it.description }

    interface EvalFormDialogListener {
        fun onDialogPositiveClick(
            dialog: DialogFragment,
            evalForm: EvalFormViewData?,
            caller: View?
        )

        fun onDialogNegativeClick(dialog: DialogFragment)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let { it ->
            val builder = AlertDialog.Builder(it)
            val inflater = requireActivity().layoutInflater

            val binding = DataBindingUtil.inflate<DialogEvalFormBinding>(
                inflater,
                R.layout.dialog_eval_form,
                null,
                false
            )
            if (binding != null) {
                binding.evalForm = evalForm
            }
            val view = binding?.root

            view?.findViewById<AutoCompleteTextView>(R.id.at_method_description)?.apply {
                val adapter = ArrayAdapter(
                    context,
                    android.R.layout.simple_dropdown_item_1line,
                    typeDescriptions
                )
                this.setAdapter(adapter)

                evalForm?.let {
                    this.setText(it.methodDescription, false) // filter=false DropDown Menu 모든 항목 표시
                }
                this.setOnItemClickListener { parent, view, position, id ->
                    evalForm?.let {
                        it.methodDescription = typeDescriptions[position]
                    }
                }
            }
            view?.findViewById<Button>(R.id.btn_dialog_cancel)?.setOnClickListener {
                listener?.onDialogNegativeClick(this)
                dialog?.cancel()
            }
            view?.findViewById<Button>(R.id.btn_dialog_ok)?.setOnClickListener {
                listener?.onDialogPositiveClick(this, evalForm, caller)
                dialog?.cancel()
            }

            builder.setView(view)
                .create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}