package com.example.animaly.activities.ui.favoris

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.animaly.R
import com.example.animaly.activities.ui.home.FavorisViewModel

class FavorisFragment : Fragment() {

    private lateinit var favorisViewModel: FavorisViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        favorisViewModel =
            ViewModelProvider(this).get(FavorisViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_favoris, container, false)



        return root
    }
}