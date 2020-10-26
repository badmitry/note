package com.badmitry.kotlingeekbrains.data.entity

import com.badmitry.kotlingeekbrains.data.model.Color

data class Note(val id: String = "",
                val title: String = "",
                val notes: String = "",
                val color: Color = Color.WHITE,
                val lastChanged: String = "") {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as Note
        if(id != other.id) return false
        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + title.hashCode()
        result = 31 * result + notes.hashCode()
        result = 31 * result + color.hashCode()
        result = 31 * result + lastChanged.hashCode()
        return result
    }
}