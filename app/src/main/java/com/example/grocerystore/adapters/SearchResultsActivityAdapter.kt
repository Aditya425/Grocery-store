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
import kotlinx.android.synthetic.main.search_results_view.view.*

class SearchResultsActivityAdapter(val context: Context, private val data: ArrayList<Product>): RecyclerView.Adapter<SearchResultsActivityAdapter.MyViewHolder>() {
    private var onLongClickListener: OnLongClickListener? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.search_results_view, parent, false)
        return MyViewHolder(view)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    fun onLongClickListener(onLongClickListener: OnLongClickListener){
        this.onLongClickListener = onLongClickListener
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val model = data[position]
        if (model.prod_image_url.isNotEmpty()){
            Glide.with(context)
                .load(model.prod_image_url)
                .placeholder(R.drawable.ic_user_place_holder)
                .into(holder.itemView.iv_rv_search_results_activity)
        }else if (model.prod_image != -1){
            Glide.with(context)
                .load(model.prod_image)
                .placeholder(R.drawable.ic_user_place_holder)
                .into(holder.itemView.iv_rv_search_results_activity)
        }

        holder.itemView.tv_rv_search_results_activity_name.text = model.prod_name
        holder.itemView.tv_rv_search_results_activity_description.text = model.prod_desc
        holder.itemView.tv_rv_search_results_activity_price.text = "Rs: ${model.prod_price}"
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