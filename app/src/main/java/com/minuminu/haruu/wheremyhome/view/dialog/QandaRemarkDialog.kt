package com.minuminu.haruu.wheremyhome.view.dialog

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.View
import androidx.fragment.app.DialogFragment
import com.google.android.material.textfield.TextInputEditText
import com.minuminu.haruu.wheremyhome.R

class QandaRemarkDialog: DialogFragment() {
    var caller: View? = null
    var listener: RemarkDialogListener? = null
    var remark: String? = null

    private lateinit var etRemark: TextInputEditText

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
            val view = inflater.inflate(R.layout.dialog_qanda_remark, null)
            etRemark = view.findViewById(R.id.et_remark)
            remark?.let {
                etRemark.setText(it)
            }

            builder.setView(view)
                // Add action buttons
                .setPositiveButton(R.string.ok) { _, _ ->
                    listener?.onDialogPositiveClick(this, etRemark.text.toString(), caller)
                }
                .setNegativeButton(R.string.cancel) { dialog, _ ->
                    listener?.onDialogNegativeClick(this)
                    dialog.cancel()
                }
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}