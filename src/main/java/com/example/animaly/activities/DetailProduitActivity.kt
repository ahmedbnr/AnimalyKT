package com.example.animaly.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.example.animaly.R
import com.example.animaly.activities.util.ApiClient
import com.example.animaly.activities.util.UserSession
import com.google.gson.JsonObject
import com.squareup.picasso.Picasso
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DetailProduitActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_produit)
        val nomTv = findViewById<TextView>(R.id.detail_produit_nom_tv)
        val imageIv = findViewById<ImageView>(R.id.detail_produit_image_iv)
        val descTv = findViewById<TextView>(R.id.detail_produit_desc_tv)
        val prixTv = findViewById<TextView>(R.id.detail_produit_prix_tv)
        val ajoutBtn = findViewById<Button>(R.id.detail_produit_ajt_btn)
        val back_btn = findViewById<ImageView>(R.id.back_btn)

        back_btn.setOnClickListener{
            onBackPressed()
        }

        val id = intent.getStringExtra("id")
        val nom = intent.getStringExtra("nom")
        val image = intent.getStringExtra("image")
        val desc = intent.getStringExtra("desc")
        val prix = intent.getDoubleExtra("prix", 0.0)


        Picasso.get().load("http://10.0.2.2:3000/img/$image").into(imageIv);

        nomTv.text = nom
        descTv.text = desc
        val prixTxt = "$prix DT"
        prixTv.text = prixTxt

        ajoutBtn.setOnClickListener{

            val params = HashMap<String?, String?>()
            params["user"] = UserSession.id
            params["produit"] = id

            ApiClient.apiService.addToCart(params).enqueue(
                object : Callback<JsonObject> {
                    override fun onFailure(call: Call<JsonObject>, t: Throwable) {

                        t.printStackTrace()

                    }
                    override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                        if (response.isSuccessful && response.body() != null) {
                            val content = response.body()

                            Toast.makeText(this@DetailProduitActivity,content.get("message").asString, Toast.LENGTH_LONG).show()


                        }
                        else {
                            val content = response.body()

                            println(content)


                        }
                    }
                }
            )
        }

    }
}