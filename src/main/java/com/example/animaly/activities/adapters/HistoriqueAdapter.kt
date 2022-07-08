package com.example.animaly.activities.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.animaly.R
import com.example.animaly.activities.models.PanierItem


class HistoriqueAdapter(private val dataSet: ArrayList<PanierItem>) :
    RecyclerView.Adapter<HistoriqueAdapter.ViewHolder>() {

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder).
     */
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val descriptionTextView: TextView
        val countTextView: TextView
        val itemLayout : LinearLayout
        val arrow : ImageView

        init {
            // Define click listener for the ViewHolder's View.
            descriptionTextView = view.findViewById(R.id.item_description)
            countTextView = view.findViewById(R.id.item_count)
            itemLayout = view.findViewById(R.id.item_layout)
            arrow = view.findViewById(R.id.arrow)
        }
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        // Create a new view, which defines the UI of the list item
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.historique_item, viewGroup, false)

        return ViewHolder(view)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {

        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        viewHolder.descriptionTextView.text = dataSet[position].nom
        viewHolder.countTextView.text = dataSet[position].qte.toString()

        if( ((position + 1) % 2) == 0)
        {
            viewHolder.itemLayout.setBackgroundColor(Color.parseColor("#FFEFEFEF"))
            viewHolder.arrow.setBackgroundColor(Color.WHITE)

        }
        else
        {
            viewHolder.itemLayout.setBackgroundColor(Color.WHITE)
            viewHolder.arrow.setBackgroundColor(Color.parseColor("#FFEFEFEF"))
        }



    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = dataSet.size

}
