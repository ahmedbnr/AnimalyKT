package com.example.animaly.activities.ui.search

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.animaly.R
import com.example.animaly.activities.adapters.CategorieAdapter
import com.example.animaly.activities.adapters.SearchAdapter
import com.example.animaly.activities.models.Produit
import com.example.animaly.activities.ui.mes_produits.MesProduitsFragment
import com.example.animaly.activities.util.ApiClient
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class SearchFragment : Fragment() {

    private lateinit var searchViewModel: SearchViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        searchViewModel =
            ViewModelProvider(this).get(SearchViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_search, container, false)

        val categorieArray = arrayListOf<Array<*>>()
        val filterArray = arrayListOf<String>()

        var sharedPref = activity?.getSharedPreferences(
            getString(R.string.search_preference_file_key), Context.MODE_PRIVATE)

        if (sharedPref != null) {
            val historiqueRecherche = sharedPref.all

            for(entry in historiqueRecherche.entries)
            {
                categorieArray.add(arrayOf(entry.value as String, false))
            }

        }


        val produitArrayOrig = arrayListOf<Produit>()
        var produitArray = arrayListOf<Produit>()
        val produitRv = root.findViewById<RecyclerView>(R.id.produit_rv)

        val produitAdapter = SearchAdapter(produitArray, root.context)
        val produitLayoutManager = GridLayoutManager(root.context, 2)

        produitRv.layoutManager = produitLayoutManager
        produitRv.adapter = produitAdapter



        ApiClient.apiService.getProduits().enqueue(
            object : Callback<JsonObject> {
                override fun onFailure(call: Call<JsonObject>, t: Throwable) {

                    t.printStackTrace()

                }
                override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                    if (response.isSuccessful && response.body() != null) {
                        val content = response.body()

                        if(response.code() == 200)
                        {

                            val produits = content.getAsJsonArray("produits")

                            produits.forEach {
                                val item = it.asJsonObject


                                var information = getString(R.string.pas_desc)
                                if ( item.get("information") != null )
                                    information = item.get("information").asString
                                val produit = Produit(item.get("_id").asString,item.get("name").asString, item.get("imagePath").asString, item.get("price").asString.toDouble(),information, 1)

                                produitArrayOrig.add(produit)
                                produitArray.add(produit)
                            }

                            produitAdapter.notifyDataSetChanged()


                        }


                    }
                    else {
                        val content = response.body()

                        println(content)


                    }
                }
            }
        )
        val categorieRv = root.findViewById<RecyclerView>(R.id.categorie_rv)
        val catAdapter = CategorieAdapter(categorieArray, CategorieAdapter.OnClickListener { value, state ->
            if(state == View.VISIBLE)
            {
                filterArray.add(value)
            }else{
                filterArray.remove(value)
            }

            produitArray.clear()

            if(!filterArray.isEmpty())
            {

                for(produit in produitArrayOrig)
                {
                    for(filtre in filterArray)
                    {
                        if (produit.nom.toLowerCase(Locale.ROOT).contains(filtre.toLowerCase(Locale.ROOT)))
                            produitArray.add(produit)


                    }
                }

            }else{

                produitArray.addAll(produitArrayOrig)
            }

            produitAdapter.notifyDataSetChanged()



        }, root.context)

        val layoutManager = LinearLayoutManager(root.context)
        layoutManager.orientation = LinearLayoutManager.HORIZONTAL

        categorieRv.layoutManager = layoutManager
        categorieRv.adapter = catAdapter

        catAdapter.notifyDataSetChanged()
        categorieRv.scrollToPosition(categorieArray.size-1)

        val search_et = root.findViewById<EditText>(R.id.search_et)
        search_et.setOnEditorActionListener { view, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {

                search_et.clearFocus()
                hideKeyboard()

                if (sharedPref != null) {
                    with(sharedPref!!.edit())
                    {
                        putString(search_et.text.toString(),search_et.text.toString())
                        apply()
                        categorieArray.add(arrayOf(search_et.text.toString(), true))
                        filterArray.add(search_et.text.toString())
                        catAdapter.notifyDataSetChanged()

                        categorieRv.scrollToPosition(categorieArray.size-1)

                        produitArray.clear()

                        for(produit in produitArrayOrig)
                        {
                            for(filtre in filterArray)
                            {
                                if (produit.nom.toLowerCase(Locale.ROOT).contains(filtre.toLowerCase(Locale.ROOT)))
                                    produitArray.add(produit)

                            }
                        }
                        produitAdapter.notifyDataSetChanged()


                    }
                }
                search_et.text.clear()


            }
            false
        }

        val reset = root.findViewById<TextView>(R.id.reset_tv)
        reset.setOnClickListener(View.OnClickListener {
            produitArray.clear()
            produitArray.addAll(produitArrayOrig)
            produitAdapter.notifyDataSetChanged()

            filterArray.clear()

            sharedPref = activity?.getSharedPreferences(
                getString(R.string.search_preference_file_key), Context.MODE_PRIVATE)

            if (sharedPref != null) {
                val historiqueRecherche = sharedPref!!.all
                categorieArray.clear()

                for(entry in historiqueRecherche.entries)
                {
                    categorieArray.add(arrayOf(entry.value as String, false))
                }

                catAdapter.notifyDataSetChanged()

            }

        })

        val mes_produits_btn = root.findViewById<Button>(R.id.mes_produits_btn)
        mes_produits_btn.setOnClickListener(View.OnClickListener {

            val fr = this@SearchFragment.parentFragmentManager.beginTransaction()
            fr.replace(R.id.nav_host_fragment, MesProduitsFragment())
            fr.commit()
        })

        return root
    }
    fun Fragment.hideKeyboard() {
        view?.let { activity?.hideKeyboard(it) }
    }

    fun Activity.hideKeyboard() {
        hideKeyboard(currentFocus ?: View(this))
    }

    fun Context.hideKeyboard(view: View) {
        val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }
}