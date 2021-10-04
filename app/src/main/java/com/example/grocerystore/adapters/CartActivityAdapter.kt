package com.example.grocerystore.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.grocerystore.R
import com.example.grocerystore.models.Product
import kotlinx.android.synthetic.main.cart_activity_view.view.*

class CartActivityAdapter(val context: Context, private val data: ArrayList<Product>): RecyclerView.Adapter<CartActivityAdapter.MyViewHolder>() {
    private var i = 1.0
    private var j=1
    private var addClickListener: AddClickListener? = null
    private var subtractClickListener: SubtractClickListener? = null
    private var textViewListener: TextViewListener? = null
    private var onLongClickListener: OnLongClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.cart_activity_view, parent, false)
        return MyViewHolder(view)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    fun onAddClickListener(addClickListener: AddClickListener){
        this.addClickListener = addClickListener
    }

    fun onSubtractClickListener(subtractClickListener: SubtractClickListener){
        this.subtractClickListener = subtractClickListener
    }

    fun getText(textViewListener: TextViewListener){
        this.textViewListener = textViewListener
    }

    fun setOnLongClickListener(onLongClickListener: OnLongClickListener){
        this.onLongClickListener = onLongClickListener
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val model = data[position]
        if (model.prod_image != -1) {
            Glide.with(context)
                .load(model.prod_image)
                .into(holder.itemView.iv_cart_activity)
        }else if (model.prod_image_url.isNotEmpty()){
            Glide.with(context)
                .load(model.prod_image_url)
                .placeholder(R.drawable.ic_user_place_holder)
                .into(holder.itemView.iv_cart_activity)
        }

        Log.i("prod_image", "${model.prod_image}")
        //holder.itemView.iv_cart_activity.setImageResource(model.prod_image)

        if (holder.itemView.tv_quantity_cart_activity.text.isEmpty()){
            textViewListener?.isText(false)
        }else{
            textViewListener?.isText(true)
        }

        holder.itemView.tv_name_cart_activity.text = model.prod_name
        holder.itemView.ib_add_cart_activity.setOnClickListener {
            if (model.kgOrPack) {
                j++
                holder.itemView.tv_quantity_cart_activity.text = "$j"
                addClickListener?.onAddClick(j.toDouble(), model.prod_name)
            }else{
                i+=0.5
                holder.itemView.tv_quantity_cart_activity.text = "$i"
                addClickListener?.onAddClick(i, model.prod_name)
            }

        }

        holder.itemView.setOnLongClickListener {
            onLongClickListener?.onLongClick(model)
            true
        }

        holder.itemView.ib_minus_cart_activity.setOnClickListener {
            if (model.kgOrPack) {
                j--
                if (j>0) {
                    holder.itemView.tv_quantity_cart_activity.text = "$j"
                    subtractClickListener?.onSubtractClick(j.toDouble(), model.prod_name)
                }
            }else{
                i-=0.5
                if (i>0) {
                    holder.itemView.tv_quantity_cart_activity.text = "$i"
                    subtractClickListener?.onSubtractClick(i, model.prod_name)
                }
            }
        }
    }

    interface OnLongClickListener{
        fun onLongClick(product: Product)
    }

    interface TextViewListener{
        fun isText(isFilled: Boolean)
    }

    interface AddClickListener{
        fun onAddClick(i: Double, name: String)
    }

    interface SubtractClickListener{
        fun onSubtractClick(j: Double, name: String)
    }

    class MyViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)
}