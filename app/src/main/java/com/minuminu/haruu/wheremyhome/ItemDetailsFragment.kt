package com.minuminu.haruu.wheremyhome

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider

class ItemDetailsFragment : Fragment() {

    companion object {
        fun newInstance() = ItemDetailsFragment()
    }

    private lateinit var viewModel: ItemDetailsViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.item_details_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(ItemDetailsViewModel::class.java)

        viewModel.itemLiveData.observe(viewLifecycleOwner, {
//            // TODO : update ui
        })

        activity?.findViewById<Button>(R.id.btn_location)?.setOnClickListener {
            viewModel.popupGoogleMap()
        }
    }

}