package com.badmitry.kotlingeekbrains.data.provider

import com.badmitry.kotlingeekbrains.data.CheckerInternetConnection
import com.badmitry.kotlingeekbrains.data.entity.Note
import com.badmitry.kotlingeekbrains.data.entity.User
import com.badmitry.kotlingeekbrains.data.error.NotAuthentication
import com.badmitry.kotlingeekbrains.data.model.NoteResult
import com.badmitry.kotlingeekbrains.data.model.NoteResult.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class FirebaseDataProvider(private val firebaseAuth: FirebaseAuth, private val db: FirebaseFirestore,
                           private val checkerInternetConnection: CheckerInternetConnection) : DataProvider {

    companion object {
        private const val NOTES_COLLECTION = "notes"
        private const val USERS_COLLECTION = "user"
    }


    private val currentUser
        get() = firebaseAuth.currentUser

    override suspend fun getCurrentUser(): User? =
    suspendCoroutine {continuation ->
            val user = currentUser?.let {User(it.displayName ?: "", it.email ?: "") }
            continuation.resume(user)
    }

    override suspend fun checkInternetConnection(): Boolean =
        suspendCoroutine {continuation ->
            continuation.resume(checkerInternetConnection.isConnection())
        }


    private val userCollectionOfNotes
        get() = currentUser?.let { db.collection(USERS_COLLECTION).document(it.uid).collection(NOTES_COLLECTION) }
                ?: throw NotAuthentication()

    @ExperimentalCoroutinesApi
    override suspend fun subscribeNotes(): ReceiveChannel<NoteResult> = Channel<NoteResult>(Channel.CONFLATED).apply {
        var registration: ListenerRegistration? = null
        try {
            registration = userCollectionOfNotes.addSnapshotListener { snapshot, error ->
                val value = error?.let {
                    Error(it)
                } ?: snapshot?.let {
                    val notes = it.documents.map {it.toObject(Note::class.java)}
                    Success(notes)
                }
                value?.let {offer(it)}
            }
        }catch (e: Throwable) {
            offer(Error(e))
        }
        invokeOnClose { registration?.remove() }
    }

    override suspend fun saveNote(note: Note): Unit =
            suspendCoroutine { continuation ->
                try {
                    userCollectionOfNotes.document(note.id).set(note)
                            .addOnSuccessListener {continuation.resume(Unit)}
                            .addOnFailureListener {continuation.resumeWithException(it)}
                } catch (e: Throwable) {
                    continuation.resumeWithException(e)
                }
            }

    override suspend fun getNoteById(id: String): Note =
            suspendCoroutine { continuation ->
                try {
                    userCollectionOfNotes.document(id).get()
                            .addOnSuccessListener {
                                continuation.resume(it.toObject(Note::class.java) as Note)
                            }.addOnFailureListener {
                                continuation.resumeWithException(it)
                            }
                } catch (e: Throwable) {
                    continuation.resumeWithException(e)
                }
            }

    override suspend fun deleteNote(id: String): Unit =
            suspendCoroutine { continuation ->
                try{
                    userCollectionOfNotes.document(id).delete()
                            .addOnSuccessListener { continuation.resume(Unit) }
                            .addOnFailureListener {continuation.resumeWithException(it)
                            }
                } catch (e: Throwable) {
                    continuation.resumeWithException(e)
                }
            }
}