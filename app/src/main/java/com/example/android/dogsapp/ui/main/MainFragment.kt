package com.example.android.dogsapp.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.android.dogsapp.databinding.FragmentMainBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainFragment : Fragment() {

    private lateinit var binding: FragmentMainBinding
    private val viewModel: MainViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMainBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel

        binding.swipeRefreshLayout.setOnRefreshListener {
            viewModel.refreshDogs()
            binding.swipeRefreshLayout.isRefreshing = false
        }

        val adapter = DogsAdapter(DogClickListener { dog ->
            viewModel.onDogClicked(dog)
        })

        binding.recyclerView.adapter = adapter

        viewModel.dogs.observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }

        viewModel.navigateToDetail.observe(viewLifecycleOwner){
            it?.let {
                this.findNavController().navigate(MainFragmentDirections.actionMainFragmentToDetailsFragment(it))
                viewModel.onDogDetailNavigated()
            }
        }

        return binding.root
    }
}
