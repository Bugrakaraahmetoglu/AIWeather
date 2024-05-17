package com.BugraKStudios.AIWeather.fragments.onBoarding

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.BugraKStudios.AIWeather.HomeActivity
import com.BugraKStudios.AIWeather.R
import com.BugraKStudios.AIWeather.databinding.FragmentOnBoardingTwoBinding


class OnBoardingTwoFragment : Fragment() {
    private var _binding: FragmentOnBoardingTwoBinding? = null
    private val binding get() = _binding!!
    private var currentPage = 1


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentOnBoardingTwoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.nxtBtn.setOnClickListener {
            if (currentPage < 3) {
                currentPage++
                navigateToNextPage()
            } else {
                navigateToHomeActivity()
            }
        }

        // İlk sayfa yüklendiğinde
        navigateToNextPage()
    }

    private fun navigateToNextPage() {
        when (currentPage) {
            1 -> {
                binding.topText.text = "Don’t know what to \nwear today?"
                binding.bottomText.text = "Get suggestions \nbased on your local \nweather."
                binding.imgView.setImageResource(R.drawable.think)
            }
            2 -> {
                binding.topText.text = "Need daily suggestions? \nWe got you!"
                binding.bottomText.text = "Automatically get \nnotifications, when you \nwake up."
                binding.imgView.setImageResource(R.drawable.onboarding_phone)
            }
            3 -> {
                binding.topText.text = "That’s it, you are ready \nto go."
                binding.bottomText.text = "Make sure to keep your \nlocation enabled."
                binding.imgView.setImageResource(R.drawable.onboarding_suit)
            }
        }
    }

    private fun navigateToHomeActivity() {
        // HomeActivity'ye yönlendirme işlemi
        // Örn: startActivity(Intent(context, HomeActivity::class.java))
        // Bu örnekte sadece bir Toast mesajı gösteriyoruz
        val intent = Intent(context, HomeActivity::class.java)
        startActivity(intent)
        requireActivity().finish()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}