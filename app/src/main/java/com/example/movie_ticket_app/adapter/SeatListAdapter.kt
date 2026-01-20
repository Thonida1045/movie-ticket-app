package com.example.movie_ticket_app.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.movie_ticket_app.R
import com.example.movie_ticket_app.databinding.ItemSeatBinding
import com.example.movie_ticket_app.model.Seat

class SeatListAdapter(private val seatList: List<Seat>,
                      private val context: Context,
                      private val selectedSeats: SelectedSeat
):
    RecyclerView.Adapter<SeatListAdapter.Viewholder>(){
    private val selectedSeatName= ArrayList<String>()

    inner class Viewholder(val binding: ItemSeatBinding):
    RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SeatListAdapter.Viewholder {
        return Viewholder(ItemSeatBinding.inflate(LayoutInflater.from(parent.context),
                parent,false))

    }


    override fun onBindViewHolder(holder: SeatListAdapter.Viewholder, position: Int) {
        val seat=seatList[position]
        holder.binding.seatTxt.text=seat.name
        when(seat.status){
            Seat.SeatStatus.AVAILABLE->{
                holder.binding.seatTxt.setBackgroundResource(R.drawable.ic_seat_avaliable)
                holder.binding.seatTxt.setTextColor(context.getColor(R.color.white))
            }
            Seat.SeatStatus.SELECTED->{
                holder.binding.seatTxt.setBackgroundResource(R.drawable.ic_seat_selected)
                holder.binding.seatTxt.setTextColor(context.getColor(R.color.black))
            }
            Seat.SeatStatus.UNAVAILABLE->{
                holder.binding.seatTxt.setBackgroundResource(R.drawable.ic_seat_unavaliable)
                holder.binding.seatTxt.setTextColor(context.getColor(R.color.grey))
            }

            }
            holder.binding.seatTxt.setOnClickListener {
                when (seat.status) {
                    Seat.SeatStatus.AVAILABLE -> {
                        seat.status= Seat.SeatStatus.SELECTED
                        // Generate seat label (e.g., A1, B2) based on position
                        val row = 'A' + (holder.adapterPosition / 7)
                        val col = (holder.adapterPosition % 7) + 1
                        val seatLabel = "$row$col"
                        selectedSeatName.add(seatLabel)
                        notifyItemChanged(holder.adapterPosition)
                    }
                    Seat.SeatStatus.SELECTED -> {
                        seat.status= Seat.SeatStatus.AVAILABLE
                        // Remove seat from selected list
                        val row = 'A' + (holder.adapterPosition / 7)
                        val col = (holder.adapterPosition % 7) + 1
                        val seatLabel = "$row$col"
                        selectedSeatName.remove(seatLabel)
                        notifyItemChanged(holder.adapterPosition)
                    }
                    else -> {}
                }
                val selected=selectedSeatName.joinToString(", ")
                selectedSeats.Return(selected,selectedSeatName.size)
        }
    }

    override fun getItemCount(): Int =seatList.size
    interface SelectedSeat{
        fun Return(selecedName: String,num:Int)
    }
}