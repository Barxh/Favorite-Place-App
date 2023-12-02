package com.example.favoriteplacesapp.adapters

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.favoriteplacesapp.R
import com.example.favoriteplacesapp.activities.AddFavoritePlace
import com.example.favoriteplacesapp.databinding.ItemFavoritePlaceBinding
import com.example.favoriteplacesapp.models.FavoritePlaceModel

class FavoritePlaceAdapter(var list: ArrayList<FavoritePlaceModel>): RecyclerView.Adapter<FavoritePlaceAdapter.ViewHolder>() {

    private var onClickListener: OnClickListener? = null
    inner class ViewHolder(binding: ItemFavoritePlaceBinding) : RecyclerView.ViewHolder(binding.root) {
        val title = binding.textViewTitle
        val description = binding.textViewDescription
        val image = binding.circularImage

    }

    fun setOnClickListener(onClickListener: OnClickListener){
        this.onClickListener = onClickListener
    }
    interface OnClickListener {
        fun onClick(position: Int, model: FavoritePlaceModel)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemFavoritePlaceBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun getItemCount(): Int {
        return list.size
    }



    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val model = list[position]

        holder.itemView.setOnClickListener{
            if(onClickListener!= null){
                onClickListener!!.onClick(position,model)
            }
        }
        holder.title.setText(model.title)
        holder.description.setText(model.description)
        holder.image.setImageURI(Uri.parse(model.image))
    }
}