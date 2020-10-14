package com.badmitry.kotlingeekbrains.ui.main

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.cardview.widget.CardView
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.badmitry.kotlingeekbrains.R
import com.badmitry.kotlingeekbrains.data.model.Color
import com.badmitry.kotlingeekbrains.data.model.Note
import kotlinx.android.synthetic.main.item_note.view.*

class MainAdapter(val onClickListener: ((Note) -> Unit)? = null) : RecyclerView.Adapter<MainAdapter.NoteViewHolder>() {

    var notes: List<Note> = listOf()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            NoteViewHolder(
                    LayoutInflater.from(parent.context).inflate(
                            R.layout.item_note,
                            parent,
                            false
                    )
            )

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) = holder.bind(notes[position])
    override fun getItemCount() = notes.size

    inner class NoteViewHolder(itemView: View) : ViewHolder(itemView) {
        fun bind(note: Note) {
            val backgroundColor = when (note.color) {
                Color.WHITE -> R.color.color_white
                Color.VIOLET -> R.color.color_violet
                Color.YELLOW -> R.color.color_yellow
                Color.RED -> R.color.color_red
                Color.PINK -> R.color.color_pink
                Color.GREEN -> R.color.color_green
                Color.BLUE -> R.color.color_blue
            }

            with(itemView as CardView) {
                with(note) {
                    note_title.text = title
                    note_text.text = notes
                    setCardBackgroundColor(ResourcesCompat.getColor(resources, backgroundColor, null))
                    setOnClickListener {
                        onClickListener?.invoke(note)
                    }
                }
            }
        }
    }

}