package com.example.animaly.activities.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView

import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView

import androidx.recyclerview.widget.RecyclerView
import com.example.animaly.R
import com.example.animaly.activities.DetailProduitActivity
import com.example.animaly.activities.models.Produit
import com.example.animaly.activities.util.ApiClient
import com.example.animaly.activities.util.UserSession
import com.google.gson.JsonObject
import com.squareup.picasso.Picasso
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class SearchAdapter(private val dataSet: ArrayList<Produit>, private val mContext: Context) :
    RecyclerView.Adapter<SearchAdapter.ViewHolder>() {

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder).
     */
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imgView: ImageView
        val prixTextView: TextView
        val nomTextView: TextView
        val addPanierImageView: ImageView
        val produitCard: CardView

        init {
            // Define click listener for the ViewHolder's View.
            imgView = view.findViewById(R.id.img_produit)
            prixTextView = view.findViewById(R.id.prix_produit)
            nomTextView = view.findViewById(R.id.nom_produit)
            addPanierImageView = view.findViewById(R.id.add_panier_produit)
            produitCard = view.findViewById(R.id.produit_card)

        }
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        // Create a new view, which defines the UI of the list item
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.produit_item, viewGroup, false)

        return ViewHolder(view)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {

        // Get element from your dataset at this position and replace the
        // contents of the view with that element


        viewHolder.nomTextView.text = dataSet[position].nom
        val prix = "${String.format("%.2f", dataSet[position].prix)} ${mContext.getString(R.string.dt)}"
        viewHolder.prixTextView.text = prix

        Picasso.get().load("http://10.0.2.2:3000/img/${dataSet[position].image}").into(viewHolder.imgView);

        viewHolder.addPanierImageView.setOnClickListener(View.OnClickListener{

            val params = HashMap<String?, String?>()
            params["user"] = UserSession.id
            params["produit"] = dataSet[position].id

            ApiClient.apiService.addToCart(params).enqueue(
                object : Callback<JsonObject> {
                    override fun onFailure(call: Call<JsonObject>, t: Throwable) {

                        t.printStackTrace()

                    }
                    override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                        if (response.isSuccessful && response.body() != null) {
                            val content = response.body()

                            Toast.makeText(mContext,content.get("message").asString,Toast.LENGTH_LONG).show()


                        }
                        else {
                            val content = response.body()

                            println(content)


                        }
                    }
                }
            )

        })

        viewHolder.produitCard.setOnClickListener{

            val intent = Intent(mContext, DetailProduitActivity::class.java)

            intent.putExtra("id", dataSet[position].id)
            intent.putExtra("nom", dataSet[position].nom)
            intent.putExtra("image", dataSet[position].image)
            intent.putExtra("desc", dataSet[position].descrption)
            intent.putExtra("prix", dataSet[position].prix)

            mContext.startActivity(intent)

        }





    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = dataSet.size

}
