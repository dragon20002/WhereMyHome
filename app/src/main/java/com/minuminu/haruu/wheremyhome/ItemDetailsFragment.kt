package com.minuminu.haruu.wheremyhome

import android.app.Activity.RESULT_OK
import android.app.DatePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.core.content.FileProvider
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.textfield.TextInputEditText
import com.minuminu.haruu.wheremyhome.databinding.ItemDetailsFragmentBinding
import com.minuminu.haruu.wheremyhome.db.AppDatabase
import com.minuminu.haruu.wheremyhome.dummy.DummyContent
import com.minuminu.haruu.wheremyhome.utils.Utils
import kotlinx.coroutines.launch
import java.io.File
import java.io.IOException
import java.util.*
import kotlin.collections.ArrayList

class ItemDetailsFragment : Fragment(), MainActivity.OnBackPressed {

    companion object {
        private const val REQUEST_TAKE_PHOTO = 1

        fun newInstance() = ItemDetailsFragment()
    }

    private var viewModel: ItemDetailsViewModel? = null
    private var binding: ItemDetailsFragmentBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.item_details_fragment, container, false)
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
        view?.findViewById<ImageButton>(R.id.btn_camera)?.setOnClickListener { dispatchTakePictureIntent() }
        view?.findViewById<ImageButton>(R.id.btn_location)?.setOnClickListener {
            // 지도
            findNavController().navigate(
                R.id.action_ItemDetailsFragment_to_MapsFragment,
                Bundle().apply {
                    putString("address", viewModel?.itemLiveData?.value?.homeInfo?.address)
                })
        }

        findNavController().addOnDestinationChangedListener { _, dest, args ->
            when (dest.id) {
                R.id.MapsFragment -> {
                    args?.getString("address")?.let { address ->
                        Log.d(javaClass.name, "address - observe $address")

                        viewModel?.itemLiveData?.value?.let { value ->
                            value.homeInfo.address = address
                            viewModel?.itemLiveData?.postValue(value)
                        }
                    }
                }
            }
        }

        viewModel = viewModel ?: ViewModelProvider(this).get(ItemDetailsViewModel::class.java).apply {
            init(AppDatabase.getDatabase(requireContext()))
            itemLiveData.removeObservers(viewLifecycleOwner)

            itemLiveData.observe(viewLifecycleOwner, {
                Log.d(javaClass.name, it.toString())

                name.set(it.homeInfo.name)
                address.set(it.homeInfo.address)
                deposit.set(it.homeInfo.deposit.toString())
                rental.set(it.homeInfo.rental.toString())
                expense.set(it.homeInfo.expense.toString())
                startDate.set(it.homeInfo.startDate)
                endDate.set(it.homeInfo.endDate)
                pictureList.addAll(it.pictures)

                val qandas = ArrayList<DummyContent.QandaViewData>()
                it.qandas.forEach { qanda ->
                    qandas.add(
                        DummyContent.QandaViewData(
                            qanda.id,
                            qanda.group,
                            qanda.num.toString(),
                            qanda.question,
                            qanda.type,
                            qanda.answer,
                            qanda.remark
                        )
                    )
                }
                qandaList.addAll(qandas)
            })
        }

        binding?.viewModel = viewModel

        if (arguments == null) {
            Log.d(javaClass.name, "add mode")

            viewModel?.itemLiveData?.postValue(DummyContent.createDummyItem())
            viewModel?.pictureList?.addAll(ArrayList())
            viewModel?.qandaList?.addAll(DummyContent.createQandaTemplate())
        } else {
            Log.d(javaClass.name, "modify mode")

            arguments?.getString("itemId")?.let {
                Log.d(javaClass.name, "itemId $it")
                viewModel?.setItemId(it)
            }
        }

        return view
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
            viewModel?.currentImageName?.let { imageName ->
                val picture = DummyContent.Picture(null, imageName)
                viewModel?.pictureList?.add(picture)
            }
        }
    }

    override fun onBackPressed(): Boolean {
        val isSaved = viewModel?.isSaved ?: true
        if (isSaved)
            return true

        viewModel?.viewModelScope?.launch {
            val homeInfo = viewModel?.saveItem()

            // Notify data changed
            arguments?.putParcelable("item", homeInfo)

            if (!isSaved) {
                viewModel?.isSaved = true
                activity?.onBackPressed()
            }
        }

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
                    viewModel?.currentImageName = it.name

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