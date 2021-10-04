package com.example.grocerystore.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.grocerystore.R
import com.example.grocerystore.models.Product
import kotlinx.android.synthetic.main.frequent_items_layout.view.*

class FrequentItemsAdapter(val context: Context, private val data: ArrayList<Product>): RecyclerView.Adapter<FrequentItemsAdapter.MyViewHolder>() {
    private var onLongClickListener: OnLongClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.frequent_items_layout, parent, false)
        return MyViewHolder(view)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    fun setOnLongClickListener(onLongClickListener: OnLongClickListener){
        this.onLongClickListener = onLongClickListener
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val model = data[position]
        Glide.with(context)
                .load(model.prod_image)
                .placeholder(R.drawable.ic_user_place_holder)
                .into(holder.itemView.iv_frequent_items)
        holder.itemView.tv_name_frequent_items.text = model.prod_name
        holder.itemView.tv_description_frequent_items.text = model.prod_desc
        holder.itemView.tv_price_frequent_items.text = "Rs: ${model.prod_price}"
        holder.itemView.setOnLongClickListener {
            onLongClickListener?.onLongClick(model)
            true
        }
    }

    interface OnLongClickListener{
        fun onLongClick(product: Product)
    }

    class MyViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)
}