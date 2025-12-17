package com.example.movie_ticket_app.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.movie_ticket_app.databinding.ViewholderCastBinding
import com.example.movie_ticket_app.model.Cast

class CastListAdapter (private val cast:ArrayList<Cast>):
RecyclerView.Adapter<CastListAdapter.ViewHolder>(){
    private var context: Context?=null
    inner class ViewHolder (private val binding: ViewholderCastBinding):
    RecyclerView.ViewHolder(binding.root){
    fun bind(cast: Cast){
        context?.let{
            Glide.with(it)
                .load(cast.PicUrl)
                .into(binding.actorImage)
        }
        binding.nameTxt.text=cast.Actor
    }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CastListAdapter.ViewHolder {
        context=parent.context
        val binding= ViewholderCastBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CastListAdapter.ViewHolder, position: Int) {
        holder.bind(cast[position])
    }

    override fun getItemCount(): Int = cast.size
}