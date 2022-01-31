package com.minuminu.haruu.wheremyhome.view.homeinfodetails

import android.annotation.SuppressLint
import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.core.util.Pair
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout
import com.minuminu.haruu.wheremyhome.R
import com.minuminu.haruu.wheremyhome.databinding.FragmentHomeInfoDetailsBinding
import com.minuminu.haruu.wheremyhome.db.AppDatabase
import com.minuminu.haruu.wheremyhome.db.data.HomeInfo
import com.minuminu.haruu.wheremyhome.db.data.HomeInfoDealType
import com.minuminu.haruu.wheremyhome.db.data.PictureViewData
import com.minuminu.haruu.wheremyhome.utils.AppUtils
import com.minuminu.haruu.wheremyhome.view.maps.MapsActivity
import kotlinx.coroutines.launch
import java.io.IOException
import java.util.*

class HomeInfoDetailsFragment : Fragment() {

    companion object {
        fun newInstance() = HomeInfoDetailsFragment()
    }

    private val dealTypeDescriptions = HomeInfoDealType.values().map { it.description }

    // Open Intent(Take Picture)
    private val requestTakePhoto =
        registerForActivityResult(ActivityResultContracts.TakePicture()) { isSuccess ->
            if (isSuccess) {
                viewModel?.currentImageName?.let { imageName ->
                    val picture = PictureViewData(null, imageName)
                    viewModel?.pictureList?.add(picture)
                }
            }
        }

    // Open `MapsActivity`
    private val requestGoogleMap =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                result.data?.extras?.getString("address")
                    ?.also { address ->
                        viewModel?.address?.set(address)
                    }
            }
        }

    private var viewModel: HomeInfoDetailsViewModel? = null
    private var binding: FragmentHomeInfoDetailsBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProvider(this)[HomeInfoDetailsViewModel::class.java].apply {
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

        view?.findViewById<Button>(R.id.btn_go_to_list)?.setOnClickListener {
            findNavController().popBackStack()
        }
        view?.findViewById<Button>(R.id.btn_edit)?.setOnClickListener {
            viewModel?.isEditing?.set(true)
        }
        view?.findViewById<Button>(R.id.btn_cancel)?.setOnClickListener {
            val homeInfoId = arguments?.getLong("homeInfoId")
            Log.d(javaClass.name, "homeInfoId $homeInfoId")

            when (homeInfoId) {
                null -> {
                    Snackbar.make(view, "존재하지 않는 집 정보입니다.", Snackbar.LENGTH_SHORT).show()
                }
                -1L -> {
                    findNavController().popBackStack()
                }
                else -> {
                    viewModel?.loadHomeInfo(homeInfoId)
                    viewModel?.isEditing?.set(false)
                }
            }
        }
        view?.findViewById<Button>(R.id.btn_done)?.setOnClickListener {
            viewModel?.viewModelScope?.launch {
                val homeInfo = viewModel?.saveItem()
                Log.d(javaClass.name, "homeInfo $homeInfo")

                findNavController().popBackStack()
            }
        }
        view?.findViewById<Button>(R.id.btn_camera)?.setOnClickListener {
            dispatchTakePictureIntent()
        }
        view?.findViewById<RecyclerView>(R.id.rv_pictures)?.apply {
            this.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            this.adapter = viewModel?.let { PictureRecyclerViewAdapter(context, it) }
        }
        view?.findViewById<TextInputLayout>(R.id.ly_address)
            ?.setEndIconOnClickListener {
                // 지도
                requestGoogleMap.launch(Intent(requireContext(), MapsActivity::class.java).apply {
                    putExtra("address", viewModel?.address?.get())
                })
            }
        view?.findViewById<AutoCompleteTextView>(R.id.at_deal_type_description)?.apply {
            val adapter = ArrayAdapter(
                context,
                android.R.layout.simple_dropdown_item_1line,
                dealTypeDescriptions
            )
            this.setAdapter(adapter)

            viewModel?.let {
                this.setText(
                    it.dealTypeDescription.get() ?: HomeInfoDealType.Monthly.description,
                    false
                )
            }
            this.setOnItemClickListener { parent, view, position, id ->
                viewModel?.dealTypeDescription?.set(dealTypeDescriptions[position])
            }
        }
        val dateRangePicker = MaterialDatePicker.Builder.dateRangePicker()
            .setTitleText("계약기간 선택")
            .setSelection(
                Pair(
                    MaterialDatePicker.thisMonthInUtcMilliseconds(),
                    MaterialDatePicker.todayInUtcMilliseconds()
                )
            )
            .build().apply {
                addOnPositiveButtonClickListener { range ->
                    fun getDateStr(timeInMillis: Long): String {
                        return Calendar.getInstance().apply {
                            this.timeInMillis = timeInMillis
                        }.let { cal ->
                            "${cal[Calendar.YEAR]}년 ${cal[Calendar.MONTH] + 1}월 ${cal[Calendar.DAY_OF_MONTH]}일"
                        }
                    }

                    view?.findViewById<Button>(R.id.btn_start_date)?.text =
                        getDateStr(range.first)
                    view?.findViewById<Button>(R.id.btn_end_date)?.text =
                        getDateStr(range.second)
                }
            }
        view?.findViewById<Button>(R.id.btn_start_date)?.setOnClickListener { _ ->
            dateRangePicker.show(parentFragmentManager, null)
        }
        view?.findViewById<Button>(R.id.btn_end_date)?.setOnClickListener { _ ->
            dateRangePicker.show(parentFragmentManager, null)
        }
        view?.findViewById<RecyclerView>(R.id.eval_info_list)?.apply {
            this.layoutManager = LinearLayoutManager(context)
            this.adapter = viewModel?.let { EvalInfoRecyclerViewAdapter(it) }
        }

        viewModel?.run {
            homeInfoLiveData.observe(viewLifecycleOwner) { homeInfo ->
                // Log.d(javaClass.name, "homeInfoViewData - observe $it")

                name.set(homeInfo.name)
                address.set(homeInfo.address)
                deposit.set(homeInfo.deposit.toString())
                rental.set(homeInfo.rental.toString())
                expense.set(homeInfo.expense.toString())
                startDate.set(homeInfo.startDate)
                endDate.set(homeInfo.endDate)
                thumbnail.set(homeInfo.thumbnail)
                area.set(homeInfo.area.toString())
                floor.set(homeInfo.floor.toString())
                realtorTelNo.set(homeInfo.realtorTelNo)
                ownerTelNo.set(homeInfo.ownerTelNo)
                dealTypeDescription.set(homeInfo.dealType?.let { name ->
                    HomeInfoDealType.values()
                        .find { dealType -> dealType.name.contentEquals(name) }?.description
                        ?: HomeInfoDealType.Monthly.description
                })

                homeInfo.id?.let { homeInfoId ->
                    loadPictureList(homeInfoId)
                    loadEvalInfoList(homeInfoId)
                }
            }

            pictureListLiveData.observe(viewLifecycleOwner) { _pictureList ->
                pictureList.clear()
                pictureList.addAll(_pictureList)
            }

            evalInfoListLiveData.observe(viewLifecycleOwner) { _evalInfoList ->
                evalInfoList.clear()
                evalInfoList.addAll(_evalInfoList)
            }
        }

        val homeInfoId = arguments?.getLong("homeInfoId", 0L)
        Log.d(javaClass.name, "homeInfoId $homeInfoId")

        when (homeInfoId) {
            0L -> {
                Log.d(javaClass.name, "add mode")

                // Create temporary data
                viewModel?.isEditing?.set(true)
                viewModel?.homeInfoLiveData?.postValue(HomeInfo(null, "", ""))
                viewModel?.pictureListLiveData?.postValue(ArrayList())

                activity?.getSharedPreferences("evalFormGroup", Context.MODE_PRIVATE)
                    ?.getLong("checkedId", -1L)?.let { checkedId ->
                        viewModel?.loadEvalFormList(checkedId)
                    }
            }
            else -> {
                Log.d(javaClass.name, "modify mode")

                homeInfoId?.let { _itemId ->
                    viewModel?.loadHomeInfo(_itemId)
                }
            }
        }

        return view
    }

    @SuppressLint("QueryPermissionsNeeded")
    private fun dispatchTakePictureIntent() {
        // Create file
        val imageUri = try {
            AppUtils.createImageFile(requireContext())
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