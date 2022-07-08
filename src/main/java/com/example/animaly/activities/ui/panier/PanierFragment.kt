package com.example.animaly.activities.ui.panier

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.animaly.R
import com.example.animaly.activities.adapters.HistoriqueAdapter
import com.example.animaly.activities.models.PanierItem
import com.example.animaly.activities.ui.home.PanierViewModel
import com.example.animaly.activities.util.ApiClient
import com.example.animaly.activities.util.UserSession
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PanierFragment : Fragment() {

    private lateinit var panierViewModel: PanierViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        panierViewModel =
            ViewModelProvider(this).get(PanierViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_panier, container, false)

        val historiqueArr =  arrayListOf<PanierItem>()
        val historiqueRv = root.findViewById<RecyclerView>(R.id.panier_rv)
        val historiqueAdapter = HistoriqueAdapter(historiqueArr)

        val params = HashMap<String?, String?>()
        params["user"] = UserSession.id

        ApiClient.apiService.getPanier(params).enqueue(
            object : Callback<JsonObject> {
                override fun onFailure(call: Call<JsonObject>, t: Throwable) {

                    t.printStackTrace()

                }
                override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                    if (response.isSuccessful && response.body() != null) {
                        val content = response.body()

                        if(response.code() == 200)
                        {

                            if(!content.get("panier").isJsonNull)
                            {
                                val produits = content.getAsJsonObject("panier").getAsJsonArray("produits")

                                produits.forEach {
                                    val item = it.asJsonObject

                                    val produit = PanierItem(item.get("_id").asString,item.get("name").asString, 1)

                                    historiqueArr.add(produit)
                                }

                                historiqueAdapter.notifyDataSetChanged()


                            }


                        }


                    }
                    else {
                        val content = response.body()

                        println(content)


                    }
                }
            }
        )


        val layoutManager = LinearLayoutManager(root.context)
        layoutManager.orientation = LinearLayoutManager.VERTICAL

        historiqueRv.layoutManager = layoutManager
        historiqueRv.adapter = historiqueAdapter



        return root
    }
}