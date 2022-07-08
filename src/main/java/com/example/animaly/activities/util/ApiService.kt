package com.example.animaly.activities.util

import com.example.animaly.activities.models.User
import com.google.gson.JsonObject
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*
import java.util.*

interface ApiService {


    @POST("user/login")
     fun login(@Body post: User): Call<JsonObject>

    @POST("user/register")
    fun register(@Body post: User): Call<JsonObject>

    @GET("produit")
    fun getProduits(): Call<JsonObject>

    @FormUrlEncoded
    @POST("panier/get-my")
    fun getPanier(@FieldMap params: HashMap<String?, String?>): Call<JsonObject>

    @FormUrlEncoded
    @POST("panier/add-to-cart")
    fun addToCart(@FieldMap params: HashMap<String?, String?>): Call<JsonObject>

    @FormUrlEncoded
    @POST("user/get-by-token")
    fun getUserByToken(@FieldMap params: HashMap<String?, String?>): Call<JsonObject>

    @GET("produit/user")
    fun getUserProduits(@Query("user") userEmail: String): Call<JsonObject>

    @DELETE("produit/{produit_id}")
    fun deleteProduct(@Path("produit_id") produit_id:String): Call<JsonObject>

    @Multipart
    @POST("produit")
    fun ajouterProduit(@Part image : MultipartBody.Part, @Part("user") user:String, @Part("name") name:String, @Part("price") price:Double, @Part("information") description:String ): Call<JsonObject>

    @FormUrlEncoded
    @PUT("user/edit-profile")
    fun editProfile(@FieldMap params: HashMap<String?, String?>): Call<JsonObject>

    @Multipart
    @PUT("user/edit-profile-picture")
    fun editProfilePicture(@Part image : MultipartBody.Part, @Part("email") email:String): Call<JsonObject>


    @DELETE("user/one/{user_id}")
    fun deleteAccount(@Path("user_id") user_id:String): Call<JsonObject>


}