package com.BugraKStudios.AIWeather.fragments.onBoarding


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import androidx.navigation.fragment.findNavController
import com.BugraKStudios.AIWeather.R
import com.BugraKStudios.AIWeather.databinding.FragmentOnBoardingBinding


class OnBoardingFragment : Fragment() {

    private var _binding : FragmentOnBoardingBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_on_boarding, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentOnBoardingBinding.bind(view)
        _binding = binding


        binding.getBtn.setOnClickListener {
            findNavController().navigate(R.id.action_onBoardingFragment_to_onBoardingTwoFragment)
        }



    }

    }

