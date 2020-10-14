package com.badmitry.kotlingeekbrains.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.badmitry.kotlingeekbrains.R
import com.badmitry.kotlingeekbrains.data.model.Note
import kotlinx.android.synthetic.main.item_note.view.*

class MainAdapter : RecyclerView.Adapter<MainAdapter.NoteViewHolder>() {

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

    class NoteViewHolder(itemView: View) : ViewHolder(itemView) {
        fun bind(note: Note) = with(itemView) {
            with(note) {
                note_title.text = title
                note_text.text = notes
                setBackgroundColor(color)
            }
        }
    }

}