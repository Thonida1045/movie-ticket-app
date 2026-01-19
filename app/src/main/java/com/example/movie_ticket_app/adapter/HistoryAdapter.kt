package com.example.movie_ticket_app.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.movie_ticket_app.R
import com.example.movie_ticket_app.model.Booking

class HistoryAdapter(
    private val items: MutableList<Booking> = mutableListOf()
) : RecyclerView.Adapter<HistoryAdapter.VH>() {

    inner class VH(v: View) : RecyclerView.ViewHolder(v) {
        val img: ImageView = v.findViewById(R.id.imgPoster)
        val title: TextView = v.findViewById(R.id.txtTitle)
        val line2: TextView = v.findViewById(R.id.txtLine2)
        val txtSeat: TextView? = v.findViewById(R.id.txtSeat)
        val line3: TextView = v.findViewById(R.id.txtLine3)
    }

    override fun onCreateViewHolder(p: ViewGroup, vt: Int): VH {
        val v = LayoutInflater.from(p.context).inflate(R.layout.item_history_ticket, p, false)
        return VH(v)
    }

    override fun onBindViewHolder(h: VH, pos: Int) {
        val b = items[pos]
        h.title.text = b.movieTitle
        h.line2.text = "${b.showDate} • ${b.showTime}"
        h.txtSeat?.text = "Seat: ${b.seat}"
        h.line3.text = "$${b.price}"
        
        // Load poster with Glide
        if (!b.posterUrl.isNullOrEmpty()) {
            Glide.with(h.img.context)
                .load(b.posterUrl)
                .placeholder(R.drawable.ic_launcher_foreground)
                .error(R.drawable.ic_launcher_foreground)
                .into(h.img)
        }
    }

    override fun getItemCount(): Int = items.size

    fun submit(newItems: List<Booking>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }
}