package com.minuminu.haruu.wheremyhome.view.homeinfodetails

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
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.minuminu.haruu.wheremyhome.R
import com.minuminu.haruu.wheremyhome.databinding.FragmentHomeInfoDetailsBinding
import com.minuminu.haruu.wheremyhome.db.AppDatabase
import com.minuminu.haruu.wheremyhome.db.data.DataUtil
import com.minuminu.haruu.wheremyhome.db.data.HomeInfo
import com.minuminu.haruu.wheremyhome.db.data.Picture
import com.minuminu.haruu.wheremyhome.utils.AppUtils
import com.minuminu.haruu.wheremyhome.view.maps.MapsActivity
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

        view?.findViewById<Button>(R.id.btn_go_to_list)?.setOnClickListener {
            findNavController().popBackStack()
        }
        view?.findViewById<Button>(R.id.btn_edit)?.setOnClickListener {
            viewModel?.isEditing?.set(true)
        }
        view?.findViewById<Button>(R.id.btn_cancel)?.setOnClickListener {
            val homeInfoId = arguments?.getLong("homeInfoId")
            Log.d(javaClass.name, "itemId $homeInfoId")

            if (homeInfoId == null) {
                Snackbar.make(view, "존재하지 않는 집 정보입니다.", Snackbar.LENGTH_SHORT).show()
            } else if (homeInfoId == -1L) {
                findNavController().popBackStack()
            } else {
                viewModel?.loadHomeInfo(homeInfoId)
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
            homeInfoLiveData.observe(viewLifecycleOwner, { homeInfo ->
                // Log.d(javaClass.name, "homeInfoWithQandas - observe $it")

                name.set(homeInfo.name)
                address.set(homeInfo.address)
                deposit.set(homeInfo.deposit.toString())
                rental.set(homeInfo.rental.toString())
                expense.set(homeInfo.expense.toString())
                startDate.set(homeInfo.startDate)
                endDate.set(homeInfo.endDate)

                homeInfo.id?.let { homeInfoId ->
                    loadPictureList(homeInfoId)
                    loadQandaList(homeInfoId)
                }
            })

            pictureListLiveData.observe(viewLifecycleOwner, { pictureList ->
                this.pictureList.clear()
                this.pictureList.addAll(pictureList)
            })

            qandaListLiveData.observe(viewLifecycleOwner, { qandaList ->
                this.qandaList.clear()
                this.qandaList.addAll(qandaList)
            })
        }

        val homeInfoId = arguments?.getLong("homeInfoId")
        Log.d(javaClass.name, "homeInfoId $homeInfoId")

        when {
            homeInfoId == null -> {
                view?.let { _view ->
                    Snackbar.make(_view, "존재하지 않는 집 정보입니다.", Snackbar.LENGTH_SHORT).show()
                }
            }
            homeInfoId == -1L -> {
                Log.d(javaClass.name, "add mode")

                // Create temporary data
                viewModel?.isEditing?.set(true)
                viewModel?.homeInfoLiveData?.postValue(HomeInfo(null, "", ""))
                viewModel?.pictureListLiveData?.postValue(ArrayList())

                // TODO: Setting에서 불러온 질문 목록을 추가할 것
                viewModel?.qandaListLiveData?.postValue(DataUtil.createQandaTemplate())
            }
            else -> {
                Log.d(javaClass.name, "modify mode")

                (homeInfoId as Long?)?.let { _itemId ->
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