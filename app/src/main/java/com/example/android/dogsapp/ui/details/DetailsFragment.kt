package com.example.android.dogsapp.ui.details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.android.dogsapp.databinding.FragmentDetailsBinding
import com.example.android.dogsapp.ui.common.BaseFragment

class DetailsFragment : BaseFragment(){

    private lateinit var binding: FragmentDetailsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.fragment_details, container, false)
        binding = FragmentDetailsBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner

        val dogDetails = DetailsFragmentArgs.fromBundle(requireArguments()).dog

        binding.dog = dogDetails

        return binding.root
    }
}