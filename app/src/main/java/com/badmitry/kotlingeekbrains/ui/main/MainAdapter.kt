package com.badmitry.kotlingeekbrains.ui.main

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.badmitry.kotlingeekbrains.R
import com.badmitry.kotlingeekbrains.data.entity.Note
import com.badmitry.kotlingeekbrains.data.getColorInt
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
            with(itemView as CardView) {
                with(note) {
                    note_title.text = title
                    note_text.text = notes
                    setCardBackgroundColor(color.getColorInt(context))
                    setOnClickListener {
                        onClickListener?.invoke(note)
                    }
                }
            }
        }
    }

}