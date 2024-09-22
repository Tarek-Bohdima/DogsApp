package com.example.android.dogsapp.ui.main

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.android.dogsapp.DogsApplication
import com.example.android.dogsapp.data.repository.DogsRepository
import com.example.android.dogsapp.databinding.FragmentMainBinding
import com.example.android.dogsapp.ui.MainActivity
import com.example.android.dogsapp.ui.utils.RefreshManager
import javax.inject.Inject

class MainFragment : Fragment() {

    private lateinit var binding: FragmentMainBinding

    @Inject
    lateinit var dogsRepository: DogsRepository

    @Inject
    lateinit var refreshManager: RefreshManager

    private val viewModel: MainViewModel by viewModels { MainViewModelFactory(dogsRepository, refreshManager) }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (requireActivity() as MainActivity).getActivityComponent().inject(this)
    }

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
