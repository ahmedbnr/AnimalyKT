package com.example.animaly.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.animaly.R
import com.example.animaly.activities.adapters.HistoriqueAdapter
import com.example.animaly.activities.models.PanierItem

class HistoriqueActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_historique)

        supportActionBar?.hide()

        val historiqueArr =  ArrayList<PanierItem>()
        historiqueArr.add(PanierItem("1","Item 1", 1))


        val historiqueRv = findViewById<RecyclerView>(R.id.historique_rv)
        val historiqueAdapter = HistoriqueAdapter(historiqueArr)

        val layoutManager = LinearLayoutManager(this)
        layoutManager.orientation = LinearLayoutManager.VERTICAL

        historiqueRv.layoutManager = layoutManager
        historiqueRv.adapter = historiqueAdapter

        historiqueAdapter.notifyDataSetChanged()




        val backBtn = findViewById<ImageButton>(R.id.back_btn)
        backBtn.setOnClickListener {
            onBackPressed()

        }




    }
}