package com.example.hw1_drivinggame

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class HighScoresListFragment : Fragment() {

    interface OnScoreClickListener {
        fun onScoreClicked(entry: HighScoreEntry)
    }

    private var listener: OnScoreClickListener? = null
    private lateinit var store: HighScoreStore

    private lateinit var recycler: RecyclerView
    private lateinit var emptyText: TextView
    private lateinit var adapter: HighScoreAdapter

    override fun onAttach(context: Context) {
        super.onAttach(context)
        listener = context as? OnScoreClickListener
        store = HighScoreStore(context)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val v = inflater.inflate(R.layout.fragment_high_scores_list, container, false)

        recycler = v.findViewById(R.id.scoresRecycler)
        emptyText = v.findViewById(R.id.emptyText)

        recycler.layoutManager = LinearLayoutManager(requireContext())
        adapter = HighScoreAdapter(emptyList()) { entry ->
            listener?.onScoreClicked(entry)
        }
        recycler.adapter = adapter

        return v
    }

    override fun onResume() {
        super.onResume()

        val data = store.getTop()
        adapter.update(data)

        if (data.isEmpty()) {
            emptyText.visibility = View.VISIBLE
            recycler.visibility = View.GONE
        } else {
            emptyText.visibility = View.GONE
            recycler.visibility = View.VISIBLE
        }
    }
}
