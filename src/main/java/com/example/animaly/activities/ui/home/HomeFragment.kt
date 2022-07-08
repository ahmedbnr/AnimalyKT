package com.example.animaly.activities.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.animaly.R

class HomeFragment : Fragment() {

    private lateinit var homeViewModel: PanierViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        homeViewModel =
            ViewModelProvider(this).get(PanierViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_home, container, false)



        return root
    }
}