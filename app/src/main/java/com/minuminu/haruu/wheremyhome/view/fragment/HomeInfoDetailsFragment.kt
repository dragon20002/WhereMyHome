package com.minuminu.haruu.wheremyhome.view.fragment

import android.annotation.SuppressLint
import android.app.Activity.RESULT_OK
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.textfield.TextInputEditText
import com.minuminu.haruu.wheremyhome.R
import com.minuminu.haruu.wheremyhome.databinding.FragmentHomeInfoDetailsBinding
import com.minuminu.haruu.wheremyhome.db.AppDatabase
import com.minuminu.haruu.wheremyhome.db.data.DataUtil
import com.minuminu.haruu.wheremyhome.db.data.Picture
import com.minuminu.haruu.wheremyhome.db.data.QandaViewData
import com.minuminu.haruu.wheremyhome.utils.Utils
import com.minuminu.haruu.wheremyhome.view.activity.MapsActivity
import com.minuminu.haruu.wheremyhome.viewmodel.HomeInfoDetailsViewModel
import kotlinx.coroutines.launch
import java.io.IOException
import java.util.*
import kotlin.collections.ArrayList

class HomeInfoDetailsFragment : Fragment() {

    companion object {
        fun newInstance() = HomeInfoDetailsFragment()
    }

    private val requestGoogleMap =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                result.data?.extras?.getString("address")
                    ?.also { address ->
                        viewModel?.address?.set(address)
                    }
            }
        }
    private val requestTakePhoto =
        registerForActivityResult(ActivityResultContracts.TakePicture()) { isSuccess ->
            if (isSuccess) {
                viewModel?.currentImageName?.let { imageName ->
                    val picture = Picture(null, imageName)
                    viewModel?.pictureList?.add(picture)
                }
            }
        }

    private var viewModel: HomeInfoDetailsViewModel? = null
    private var binding: FragmentHomeInfoDetailsBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProvider(this).get(HomeInfoDetailsViewModel::class.java).apply {
            init(AppDatabase.getDatabase(requireContext()))
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_home_info_details, container, false
        )
        binding?.viewModel = viewModel
        val view = binding?.root

        view?.findViewById<Button>(R.id.btn_edit)?.setOnClickListener {
            viewModel?.isEditing?.set(true)
        }
        view?.findViewById<Button>(R.id.btn_cancel)?.setOnClickListener {
            val itemId = arguments?.getString("itemId")
            Log.d(javaClass.name, "itemId $itemId")

            if (itemId === null) {
                findNavController().popBackStack()
            } else {
                viewModel?.setItemId(itemId)
                viewModel?.isEditing?.set(false)
            }
        }
        view?.findViewById<Button>(R.id.btn_done)?.setOnClickListener {
            viewModel?.viewModelScope?.launch {
                val homeInfo = viewModel?.saveItem()
                Log.d(javaClass.name, "homeInfo $homeInfo")

                findNavController().popBackStack()
            }
        }
        view?.findViewById<ImageButton>(R.id.btn_camera)
            ?.setOnClickListener {
                dispatchTakePictureIntent()
            }
        view?.findViewById<ImageButton>(R.id.btn_location)?.setOnClickListener {
            // 지도
            requestGoogleMap.launch(Intent(requireContext(), MapsActivity::class.java).apply {
                putExtra("address", viewModel?.address?.get())
            })
        }
        view?.findViewById<TextInputEditText>(R.id.et_start_date)?.setOnClickListener { v ->
            viewModel?.isEditing?.get()?.takeIf { !it }?.also { return@setOnClickListener }

            val today = Calendar.getInstance()
            DatePickerDialog(
                requireContext(), (DatePickerDialog.OnDateSetListener { _, y, m, d ->
                    val date = Calendar.getInstance().apply { set(y, m, d) }.let {
                        "${it[Calendar.YEAR]}년 ${it[Calendar.MONTH] + 1}월 ${it[Calendar.DAY_OF_MONTH]}일"
                    }
                    (v as TextInputEditText).setText(date)
                }),
                today[Calendar.YEAR],
                today[Calendar.MONTH],
                today[Calendar.DAY_OF_MONTH]
            ).show()
        }
        view?.findViewById<TextInputEditText>(R.id.et_end_date)?.setOnClickListener { v ->
            viewModel?.isEditing?.get()?.takeIf { !it }?.also { return@setOnClickListener }

            val today = Calendar.getInstance()
            DatePickerDialog(
                requireContext(), (DatePickerDialog.OnDateSetListener { _, y, m, d ->
                    val date = Calendar.getInstance().apply { set(y, m, d) }.let {
                        "${it[Calendar.YEAR]}년 ${it[Calendar.MONTH] + 1}월 ${it[Calendar.DAY_OF_MONTH]}일"
                    }
                    (v as TextInputEditText).setText(date)
                }),
                today[Calendar.YEAR],
                today[Calendar.MONTH],
                today[Calendar.DAY_OF_MONTH]
            ).show()
        }

        viewModel?.run {
            itemLiveData.observe(viewLifecycleOwner, {
                // Log.d(javaClass.name, "homeInfoWithQandas - observe $it")

                name.set(it.homeInfo.name)
                address.set(it.homeInfo.address)
                deposit.set(it.homeInfo.deposit.toString())
                rental.set(it.homeInfo.rental.toString())
                expense.set(it.homeInfo.expense.toString())
                startDate.set(it.homeInfo.startDate)
                endDate.set(it.homeInfo.endDate)
                for (i in it.pictures.indices) {
                    if (i < pictureList.size) {
                        pictureList[i] = it.pictures[i]
                    } else {
                        pictureList.add(it.pictures[i])
                    }
                }

                qandaList.addAll(it.qandas.map { qanda ->
                    QandaViewData(
                        qanda.id,
                        qanda.group,
                        qanda.num.toString(),
                        qanda.question,
                        qanda.type,
                        qanda.answer,
                        qanda.remark
                    )
                })
            })
        }

        val itemId = arguments?.getString("itemId")
        Log.d(javaClass.name, "itemId $itemId")

        if (itemId === null) {
            Log.d(javaClass.name, "add mode")

            viewModel?.isEditing?.set(true)
            viewModel?.itemLiveData?.postValue(DataUtil.createDummyItem())
            viewModel?.pictureList?.addAll(ArrayList())
            viewModel?.qandaList?.addAll(DataUtil.createQandaTemplate())
        } else {
            Log.d(javaClass.name, "modify mode")

            viewModel?.setItemId(itemId)
        }

        return view
    }

    @SuppressLint("QueryPermissionsNeeded")
    private fun dispatchTakePictureIntent() {
        // Create file
        val imageUri = try {
            Utils.createImageFile(requireContext())
        } catch (ex: IOException) {
            Log.e(javaClass.name, "Fail to create image file")
            null
        }?.also {
            viewModel?.currentImageName = it.name
        }?.let {
            FileProvider.getUriForFile(
                requireContext(),
                "com.minuminu.haruu.wheremyhome",
                it
            )
        }
        Log.d(javaClass.name, "imageUri = $imageUri")

        // Request Take photo
        requestTakePhoto.launch(imageUri)
    }
}