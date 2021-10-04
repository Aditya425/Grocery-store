package com.example.grocerystore.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.grocerystore.R
import com.example.grocerystore.models.Product
import kotlinx.android.synthetic.main.layout_category_search_results.view.*

class CategorySearchResultsActivityAdapter(val context: Context, val data: ArrayList<Product>): RecyclerView.Adapter<CategorySearchResultsActivityAdapter.MyViewHolder>() {
    private var onClickListener: OnClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.layout_category_search_results, parent, false)
        return MyViewHolder(view)
    }

    fun setOnClickListener(onClickListener: OnClickListener){
        this.onClickListener = onClickListener
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val model = data[position]
        Glide.with(context)
            .load(model.prod_image_url)
            .placeholder(R.drawable.ic_user_place_holder)
            .into(holder.itemView.iv_category_results_image)

        holder.itemView.tv_category_results_title.text = model.prod_name
        holder.itemView.tv_category_results_description.text = model.prod_desc
        holder.itemView.tv_category_results_price.text = model.prod_price.toString()

        holder.itemView.setOnLongClickListener {
            onClickListener?.onClick(model)
            true
        }
    }

    interface OnClickListener{
        fun onClick(product: Product)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    class MyViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)
}