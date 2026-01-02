package com.example.hw1_drivinggame

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class HighScoreAdapter(
    private var items: List<HighScoreEntry>,
    private val onClick: (HighScoreEntry) -> Unit
) : RecyclerView.Adapter<HighScoreAdapter.VH>() {

    class VH(v: View) : RecyclerView.ViewHolder(v) {
        val tvRank: TextView = v.findViewById(R.id.tvRank)
        val tvName: TextView = v.findViewById(R.id.tvName)
        val tvScore: TextView = v.findViewById(R.id.tvScore)
        val tvDistance: TextView = v.findViewById(R.id.tvDistance)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_high_score, parent, false)
        return VH(v)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val e = items[position]
        holder.tvRank.text = "#${position + 1}"
        holder.tvName.text = e.name
        holder.tvScore.text = "Score: ${e.score}"
        holder.tvDistance.text = "Distance: ${e.distance}"
        holder.itemView.setOnClickListener { onClick(e) }
    }

    override fun getItemCount(): Int = items.size

    fun update(newItems: List<HighScoreEntry>) {
        items = newItems
        notifyDataSetChanged()
    }
}
