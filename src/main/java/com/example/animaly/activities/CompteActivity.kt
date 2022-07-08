package com.example.animaly.activities

import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.animaly.R
import com.example.animaly.activities.util.ApiClient
import com.example.animaly.activities.util.UserSession
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CompteActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_compte)

        supportActionBar?.hide()

        val backBtn = findViewById<ImageButton>(R.id.back_btn)
        backBtn.setOnClickListener {
            onBackPressed()

        }

        val prenom_et = findViewById<EditText>(R.id.prenom_et)
        val nom_et = findViewById<EditText>(R.id.nom_et)
        val email_et = findViewById<EditText>(R.id.email_et)
        val maj_btn = findViewById<Button>(R.id.maj_btn)

        prenom_et.setText(UserSession.prenom)
        nom_et.setText(UserSession.nom)
        email_et.setText(UserSession.email)

        maj_btn.setOnClickListener{
            if(nom_et.text.toString() == UserSession.nom && prenom_et.text.toString() == UserSession.prenom && email_et.text.toString() == UserSession.email)
            {
                Toast.makeText(this, getString(R.string.aucun_changement), Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }



            val params = HashMap<String?, String?>()
            params["oldEmail"] = UserSession.email
            params["newEmail"] = email_et.text.toString()
            params["firstName"] = prenom_et.text.toString()
            params["lastName"] = nom_et.text.toString()

            ApiClient.apiService.editProfile(params).enqueue(
                object : Callback<JsonObject> {
                    override fun onFailure(call: Call<JsonObject>, t: Throwable) {

                        t.printStackTrace()

                    }
                    override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                        if (response.isSuccessful && response.body() != null) {

                            val content = response.body()

                            if(response.code() == 200)
                            {

                                if(UserSession.email != email_et.text.toString())
                                {
                                    val sharedPref = getSharedPreferences(
                                        getString(R.string.user), Context.MODE_PRIVATE)

                                    with (sharedPref.edit()) {
                                        putString(getString(R.string.token), content.get("token").asString)
                                        commit()
                                    }
                                }

                                UserSession.nom = nom_et.text.toString()
                                UserSession.prenom = prenom_et.text.toString()
                                UserSession.email = email_et.text.toString()

                                finish()

                            }

                            Toast.makeText(this@CompteActivity,content.get("message").asString,Toast.LENGTH_LONG).show()


                        }
                        else {
                            val content = response.body()
                            println(content)

                        }
                    }
                }
            )

        }

        val delete_account_btn = findViewById<Button>(R.id.delete_account_btn)
        delete_account_btn.setOnClickListener{
            val builder = AlertDialog.Builder(this)
            builder.setMessage(getString(R.string.delete_account))
                .setCancelable(false)
                .setNegativeButton(getString(R.string.oui)) { dialog, id ->

                    ApiClient.apiService.deleteAccount(UserSession.id).enqueue(
                        object : Callback<JsonObject> {
                            override fun onFailure(call: Call<JsonObject>, t: Throwable) {

                                t.printStackTrace()

                            }
                            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                                if (response.isSuccessful && response.body() != null) {

                                    if(response.code() == 200)
                                    {
                                        //delete shared prefs
                                        //reset session
                                        //send to login screen
                                        val sharedPref = getSharedPreferences(
                                            getString(R.string.user), Context.MODE_PRIVATE)

                                        with (sharedPref.edit()) {
                                            clear()
                                            commit()
                                        }

                                        UserSession.reset()

                                        val intent = Intent(this@CompteActivity, LoginActivity::class.java)
                                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                                        startActivity(intent)
                                        ActivityCompat.finishAffinity(this@CompteActivity)


                                    }

                                }
                                else {
                                    val content = response.body()

                                    println(content)


                                }
                            }
                        }
                    )

                }
                .setPositiveButton(getString(R.string.non)) { dialog, id ->
                    dialog.dismiss()
                }
            val alert = builder.create()
            alert.show()

            val nbutton = alert.getButton(DialogInterface.BUTTON_NEGATIVE);
            nbutton.setTextColor(Color.RED)
            val pbutton = alert.getButton(DialogInterface.BUTTON_POSITIVE);
            pbutton.setTextColor(Color.BLACK)

        }

    }
}