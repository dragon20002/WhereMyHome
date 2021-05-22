package com.minuminu.haruu.wheremyhome.view.fragment

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import com.minuminu.haruu.wheremyhome.data.DataUtil
import com.minuminu.haruu.wheremyhome.data.Picture
import com.minuminu.haruu.wheremyhome.data.QandaViewData
import com.minuminu.haruu.wheremyhome.databinding.FragmentHomeInfoDetailsBinding
import com.minuminu.haruu.wheremyhome.db.AppDatabase
import com.minuminu.haruu.wheremyhome.utils.Utils
import com.minuminu.haruu.wheremyhome.view.activity.MainActivity
import com.minuminu.haruu.wheremyhome.viewmodel.HomeInfoDetailsViewModel
import kotlinx.coroutines.launch
import java.io.IOException
import java.util.*
import kotlin.collections.ArrayList

class HomeInfoDetailsFragment : Fragment(), MainActivity.OnBackPressed {

    companion object {
        fun newInstance() = HomeInfoDetailsFragment()
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

        view?.findViewById<TextInputEditText>(R.id.et_start_date)?.setOnClickListener { v ->
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
        view?.findViewById<ImageButton>(R.id.btn_camera)
            ?.setOnClickListener { dispatchTakePictureIntent() }
        view?.findViewById<ImageButton>(R.id.btn_location)?.setOnClickListener {
            // 지도
            findNavController().navigate(
                R.id.action_HomeInfoDetailsFragment_to_MapsFragment,
                Bundle().apply {
                    putString("address", viewModel?.address?.get())
                })
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

                for (i in it.qandas.indices) {
                    val qanda: QandaViewData = it.qandas[i].let { qanda ->
                        QandaViewData(
                            qanda.id, qanda.group, qanda.num.toString(),
                            qanda.question, qanda.type, qanda.answer, qanda.remark
                        )
                    }
                    if (i < qandaList.size) {
                        qandaList[i] = qanda
                    } else {
                        qandaList.add(qanda)
                    }
                }
            })
        }

        findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<String>(
            "address"
        )?.observe(viewLifecycleOwner, { address ->
            Log.d(javaClass.name, "address - navController $address")

            viewModel?.address?.set(address)
        })

        if (arguments == null) {
            Log.d(javaClass.name, "add mode")

            viewModel?.itemLiveData?.postValue(DataUtil.createDummyItem())
            viewModel?.pictureList?.addAll(ArrayList())
            viewModel?.qandaList?.addAll(DataUtil.createQandaTemplate())
        } else {
            Log.d(javaClass.name, "modify mode")

            arguments?.getString("itemId")?.let {
                Log.d(javaClass.name, "itemId $it")
                viewModel?.setItemId(it)
            }
        }

        return view
    }

    override fun onBackPressed(): Boolean {
        viewModel?.viewModelScope?.launch {
            val homeInfo = viewModel?.saveItem()

            // Notify data changed
            findNavController().previousBackStackEntry?.savedStateHandle?.set("homeInfo", homeInfo)

            findNavController().popBackStack()
        }

        return false
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