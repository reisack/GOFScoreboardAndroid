package rek.gofscoreboard

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.score_grid_cell.view.*

class ScoreboardAdapter(private val scoresData: Array<String>)
    : androidx.recyclerview.widget.RecyclerView.Adapter<ScoreboardAdapter.ScoreboardViewHolder>() {

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder.
    // Each data item is just a string in this case that is shown in a TextView.
    class ScoreboardViewHolder(val scoreView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(scoreView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScoreboardViewHolder {
        val layoutViewScore = LayoutInflater.from(parent.context)
            .inflate(R.layout.score_grid_cell, parent, false)

        return ScoreboardViewHolder(layoutViewScore)
    }

    override fun onBindViewHolder(holder: ScoreboardViewHolder, position: Int) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.scoreView.textScoreValue.text = scoresData[position]
    }

    override fun getItemCount(): Int = scoresData.size
}