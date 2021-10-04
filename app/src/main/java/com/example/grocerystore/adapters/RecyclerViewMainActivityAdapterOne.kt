package com.example.grocerystore.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.grocerystore.R
import kotlinx.android.synthetic.main.card_view_images_one.view.*

class RecyclerViewMainActivityAdapterOne(val context: Context, private val data: ArrayList<Int>): RecyclerView.Adapter<RecyclerViewMainActivityAdapterOne.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.card_view_images_one, parent, false)
        return MyViewHolder(view)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val model = data[position]

        Glide.with(context)
                .load(model)
                .placeholder(R.drawable.ic_user_place_holder)
                .into(holder.itemView.iv_card_view_one_images)
    }

    class MyViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)
}