package com.example.grocerystore.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.grocerystore.R
import kotlinx.android.synthetic.main.category_item.view.*

class CategoriesAdapter(val context: Context, val data: ArrayList<String>): RecyclerView.Adapter<CategoriesAdapter.MyViewHolder>() {
    private var onClickListener: OnClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.category_item, parent, false)
        return MyViewHolder(view)
    }

    fun setOnClickListener(onClickListener: OnClickListener){
        this.onClickListener = onClickListener
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val model = data[position]
        holder.itemView.tv_category_name.text = model
        holder.itemView.setOnClickListener {
            onClickListener?.onClick(model)
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }

    interface OnClickListener{
        fun onClick(category: String)
    }

    class MyViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)
}