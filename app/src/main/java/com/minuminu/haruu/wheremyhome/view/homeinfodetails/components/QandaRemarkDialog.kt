package com.minuminu.haruu.wheremyhome.view.homeinfodetails.components

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import com.google.android.material.textfield.TextInputEditText
import com.minuminu.haruu.wheremyhome.R
import com.minuminu.haruu.wheremyhome.databinding.DialogQandaRemarkBinding
import com.minuminu.haruu.wheremyhome.view.homeinfodetails.HomeInfoDetailsViewModel

class QandaRemarkDialog : DialogFragment() {
    var caller: View? = null
    var listener: RemarkDialogListener? = null
    var remark: String? = null
    var viewModel: HomeInfoDetailsViewModel? = null

    interface RemarkDialogListener {
        fun onDialogPositiveClick(dialog: DialogFragment, remark: String, caller: View?)
        fun onDialogNegativeClick(dialog: DialogFragment)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let { it ->
            val builder = AlertDialog.Builder(it)
            // Get the layout inflater
            val inflater = requireActivity().layoutInflater

            // Inflate and set the layout for the dialog
            // Pass null as the parent view because its going in the dialog layout
//            val view = inflater.inflate(R.layout.dialog_qanda_remark, null)
            val binding = DataBindingUtil.inflate<DialogQandaRemarkBinding>(
                inflater,
                R.layout.dialog_qanda_remark,
                null,
                false
            )
            if (binding != null) {
                binding.isEditing = viewModel?.isEditing?.get()
                binding.remark = remark
            }
            val view = binding?.root

            val etRemark = view?.findViewById<TextInputEditText>(R.id.et_remark)

            view?.findViewById<Button>(R.id.btn_dialog_cancel)?.setOnClickListener {
                listener?.onDialogNegativeClick(this)
                dialog?.cancel()
            }
            view?.findViewById<Button>(R.id.btn_dialog_ok)?.setOnClickListener {
                listener?.onDialogPositiveClick(this, etRemark?.text.toString(), caller)
                dialog?.cancel()
            }

            builder.setView(view)
                .create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}