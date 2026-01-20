package com.example.movie_ticket_app.adapter

import android.R.attr.data
import android.view.LayoutInflater
import android.view.ViewGroup
import com.example.movie_ticket_app.R;
import androidx.recyclerview.widget.RecyclerView
import com.example.movie_ticket_app.databinding.ItemDateBinding

class DateAdapter(private val timeSlots:List<String>,
                  private val onItemClick: (String) -> Unit):
    RecyclerView.Adapter<DateAdapter.ViewHolder> () {
        private var selectedPosition =-1
        private var lastSelectedPosition=-1

    inner class ViewHolder (private  val binding: ItemDateBinding):
        RecyclerView.ViewHolder(binding.root)
    {
        fun bind(date: String) {
            val  dateParts=date.split("/")
            if(dateParts.size==3) {
                binding.dayTxt.text=dateParts[0]
                binding.dayMonthTxt.text=dateParts[1]+ " "+dateParts[2]

                if(selectedPosition==adapterPosition) {
                    binding.mainLayout.setBackgroundResource(R.drawable.orange_bg)
                    binding.dayTxt.setTextColor(binding.root.context.getColor(R.color.black))
                    binding.dayMonthTxt.setTextColor(binding.root.context.getColor(R.color.black))
                }else{
                    binding.mainLayout.setBackgroundResource(R.drawable.light_black_bg)
                    binding.dayTxt.setTextColor(binding.root.context.getColor(R.color.white))
                    binding.dayMonthTxt.setTextColor(binding.root.context.getColor(R.color.white))
                }
                binding.root.setOnClickListener {
                    val clickedPosition = adapterPosition
                    if(clickedPosition != RecyclerView.NO_POSITION){
                        lastSelectedPosition=selectedPosition
                        selectedPosition=clickedPosition
                        notifyItemChanged(lastSelectedPosition)
                        notifyItemChanged(selectedPosition)
                        onItemClick(date)
                    }
                }
            }
        }
        }



    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): DateAdapter.ViewHolder {
        return ViewHolder(ItemDateBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        ))
    }

    override fun onBindViewHolder(holder: DateAdapter.ViewHolder, position: Int) {
        holder.bind(timeSlots[position])
    }

    override fun getItemCount(): Int = timeSlots.size

}