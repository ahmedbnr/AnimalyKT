package com.example.animaly.activities

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import com.example.animaly.R
import com.example.animaly.activities.models.User
import com.example.animaly.activities.util.ApiClient
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        supportActionBar?.hide()

        val nom = findViewById<EditText>(R.id.nom_et)
        val prenom = findViewById<EditText>(R.id.prenom_et)
        val cin = findViewById<EditText>(R.id.cin_et)
        val email = findViewById<EditText>(R.id.email_et)
        val adresse = findViewById<EditText>(R.id.adresse_et)
        val tel = findViewById<EditText>(R.id.tel_et)
        val mdp = findViewById<EditText>(R.id.mdp_et)
        val close_btn = findViewById<ImageView>(R.id.close_btn)
        close_btn.setOnClickListener{
            onBackPressed()
        }

        val inscriptionBtn = findViewById<Button>(R.id.inscription_btn)

        inscriptionBtn.setOnClickListener{

            if(nom.text.isNullOrBlank())
            {
                nom.error = getString(R.string.champ_vide)

                return@setOnClickListener
            }

            if(prenom.text.isNullOrBlank())
            {
                prenom.error = getString(R.string.champ_vide)

                return@setOnClickListener
            }
            if(cin.text.isNullOrBlank())
            {
                cin.error = getString(R.string.champ_vide)

                return@setOnClickListener
            }
            if(email.text.isNullOrBlank())
            {
                email.error = getString(R.string.champ_vide)

                return@setOnClickListener
            }
            if(adresse.text.isNullOrBlank())
            {
                adresse.error = getString(R.string.champ_vide)

                return@setOnClickListener
            }
            if(tel.text.isNullOrBlank())
            {
                tel.error = getString(R.string.champ_vide)

                return@setOnClickListener
            }
            if(mdp.text.isNullOrBlank())
            {
                mdp.error = getString(R.string.champ_vide)

                return@setOnClickListener
            }
            ApiClient.apiService.register(User("",prenom.text.toString(), nom.text.toString(), cin.text.toString(), email.text.toString(), adresse.text.toString(), mdp.text.toString(), tel.text.toString(), "","User")).enqueue(
                object : Callback<JsonObject> {
                    override fun onFailure(call: Call<JsonObject>, t: Throwable) {

                        t.printStackTrace()

                    }
                    override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                        if (response.isSuccessful && response.body() != null) {
                            val content = response.body()

                            if(response.code() == 201)
                            {

                                println(content)

                                val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
                                startActivity(intent)
                                finish()

                            }

                            if(response.code() == 403)
                            {
                                Toast.makeText(
                                    this@RegisterActivity,
                                    "Error Occurred: ${response.message()}",
                                    Toast.LENGTH_LONG
                                ).show()
                            }



                        }
                        else {

                            Toast.makeText(
                                this@RegisterActivity,
                                "Error Occurred: ${response.message()}",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                }
            )



            val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
            startActivity(intent)
        }


        val connexionId = findViewById<LinearLayout>(R.id.connexion_layout)
        connexionId.setOnClickListener {
            val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
            startActivity(intent)
        }
    }


}