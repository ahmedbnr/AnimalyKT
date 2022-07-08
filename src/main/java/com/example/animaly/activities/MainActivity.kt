package com.example.animaly.activities

import android.content.Context
import android.content.Intent
import android.os.*
import androidx.appcompat.app.AppCompatActivity
import com.example.animaly.R
import com.example.animaly.activities.models.Produit
import com.example.animaly.activities.util.ApiClient
import com.example.animaly.activities.util.UserSession
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class MainActivity : AppCompatActivity() {

     val DELAY:Long = 3000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar?.hide()

        Handler(Looper.getMainLooper()).postDelayed({

            var sharedPref = getSharedPreferences(
                getString(R.string.user), Context.MODE_PRIVATE)

            val token = sharedPref.getString(getString(R.string.token),null)

            println(token)

            if(token != null)
            {
                val params = HashMap<String?, String?>()
                params["token"] = token

                ApiClient.apiService.getUserByToken(params).enqueue(
                    object : Callback<JsonObject> {
                        override fun onFailure(call: Call<JsonObject>, t: Throwable) {

                            t.printStackTrace()

                        }
                        override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                            if (response.isSuccessful && response.body() != null) {
                                val content = response.body()

                                if(response.code() == 200)
                                {

                                    val user = content.getAsJsonObject("user")

                                    UserSession.id =
                                        user.get("_id").asString


                                    UserSession.prenom =
                                        user.get("firstName").asString

                                    UserSession.nom =
                                        user.get("lastName").asString

                                    UserSession.email =
                                        user.get("email").asString

                                    val profilePicture = user.get("profilePicture").asString

                                    if(!profilePicture.isEmpty())
                                        UserSession.image = profilePicture

                                    val intent = Intent(this@MainActivity, HomeActivity::class.java)
                                    startActivity(intent)
                                    finish()



                                }
                                else
                                {
                                    val intent = Intent(this@MainActivity, LoginActivity::class.java)
                                    startActivity(intent)
                                    finish()

                                }


                            }
                            else {
                                val content = response.body()

                                println(content)

                                val intent = Intent(this@MainActivity, LoginActivity::class.java)
                                startActivity(intent)
                                finish()


                            }




                        }
                    }
                )
            }
            else{
                val intent = Intent(this@MainActivity, LoginActivity::class.java)
                startActivity(intent)
                finish()
            }



        }, DELAY);
    }
}