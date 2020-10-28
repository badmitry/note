package com.badmitry.kotlingeekbrains.data.provider

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.badmitry.kotlingeekbrains.data.CheckerInternetConnection
import com.badmitry.kotlingeekbrains.data.entity.Note
import com.badmitry.kotlingeekbrains.data.entity.User
import com.badmitry.kotlingeekbrains.data.error.NotAuthentication
import com.badmitry.kotlingeekbrains.data.model.NoteResult
import com.badmitry.kotlingeekbrains.data.model.NoteResult.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class FirebaseDataProvider(private val firebaseAuth: FirebaseAuth, private val db: FirebaseFirestore,
                           private val checkerInternetConnection: CheckerInternetConnection) : DataProvider {

    companion object {
        private const val NOTES_COLLECTION = "notes"
        private const val USERS_COLLECTION = "user"
    }


    private val currentUser
        get() = firebaseAuth.currentUser

    override fun getCurrentUser() = MutableLiveData<User>().apply {
        value = currentUser?.let { User(it.displayName ?: "", it.email ?: "") }
    }

    override fun checkInternetConnection(): Boolean {
        return checkerInternetConnection.isConnection()
    }

    private val notesReference
        get() = currentUser?.let { db.collection(USERS_COLLECTION).document(it.uid).collection(NOTES_COLLECTION) }
                ?: throw NotAuthentication()

    override fun getNotes(): LiveData<NoteResult> =
            MutableLiveData<NoteResult>().apply {
                notesReference.addSnapshotListener { snapshot, error ->
                    value = error?.let { Error(it) } ?: snapshot?.let {
                        val notes = it.documents.map { it.toObject(Note::class.java) }
                        Success(notes)
                    }
                }
            }


    override fun saveNote(note: Note): LiveData<NoteResult> =
            MutableLiveData<NoteResult>().apply {
                notesReference.document(note.id).set(note)
                        .addOnSuccessListener { value = Success(note) }
                        .addOnFailureListener { value = Error(it) }
            }


    override fun getNoteById(id: String): LiveData<NoteResult> =
            MutableLiveData<NoteResult>().apply {
                notesReference.document(id).get()
                        .addOnSuccessListener { snapshot ->
                            val note = snapshot.toObject(Note::class.java) as Note
                            value = Success(note)
                        }.addOnFailureListener {
                            value = Error(it)
                        }
            }


    override fun deleteNote(id: String): LiveData<NoteResult> =
            MutableLiveData<NoteResult>().apply {
                notesReference.document(id).delete()
                        .addOnSuccessListener { value = Success(null) }
                        .addOnFailureListener { value = Error(it) }
            }

}