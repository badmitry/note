package com.badmitry.kotlingeekbrains.data.provider

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.badmitry.kotlingeekbrains.data.CheckerInternetConnection
import com.badmitry.kotlingeekbrains.data.entity.Note
import com.badmitry.kotlingeekbrains.data.error.NotAuthentication
import com.badmitry.kotlingeekbrains.data.model.Color
import com.badmitry.kotlingeekbrains.data.model.NoteResult
import com.badmitry.kotlingeekbrains.data.model.NoteResult.Error
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.*
import io.mockk.*
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.consume
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class FirebaseDataProviderTest {
    @get:Rule
    val taskExecutorRule = InstantTaskExecutorRule()

    private val mockDB = mockk<FirebaseFirestore>()
    private val mockAuth = mockk<FirebaseAuth>()
    private val mockUser = mockk<FirebaseUser>()
    private var mockResultCollection = mockk<CollectionReference>()
    private val mockCheckInt = mockk<CheckerInternetConnection>()

    private val mockDocument1 = mockk<DocumentSnapshot>()
    private val mockDocument2 = mockk<DocumentSnapshot>()
    private val mockDocument3 = mockk<DocumentSnapshot>()

    private val testNotes = listOf(Note("1", "t1", "text1", Color.WHITE), Note("2", "t2", "text2", Color.BLUE), Note("3", "t3", "text3", Color.YELLOW))

    private val provider = FirebaseDataProvider(mockAuth, mockDB, mockCheckInt)

    @Before
    fun setup() {
        clearAllMocks()
        every { mockAuth.currentUser } returns mockUser
        every { mockUser.uid } returns ""
        coEvery { mockDB.collection(any()).document(any()).collection(any()) } returns mockResultCollection
        every { mockDocument1.toObject(Note::class.java) } returns testNotes[0]
        every { mockDocument2.toObject(Note::class.java) } returns testNotes[1]
        every { mockDocument3.toObject(Note::class.java) } returns testNotes[2]
    }

    @Test
    fun `before throw NotAuthentication if no auth`() = runBlocking {
        coEvery { mockAuth.currentUser } returns null
        val result = provider.subscribeNotes().receive()
        val error = (result as NoteResult.Error).error
        assertTrue(error is NotAuthentication)
//
//        assertTrue(provider.saveNote(testNotes[0]) is Unit)
//        assertTrue(provider.saveNote(testNotes[0]) is Unit)


//        result = (it as? Error)?.error
//        assertTrue(provider.saveNote(testNotes[0]) is Throwable)
//        provider.getNoteById(testNotes[0].id).observeForever {
//            result = (it as? Error)?.error
//        }
//        assertTrue(result is NotAuthentication)
//        provider.deleteNote(testNotes[0].id).observeForever {
//            result = (it as? Error)?.error
//        }
//        assertTrue(result is NotAuthentication)
    }

//    @Test
//    fun `getNotes calls snapshot once`() {
//        val mockSnapshot = mockk<QuerySnapshot>()
//        val slot = slot<EventListener<QuerySnapshot>>()
//        every { mockSnapshot.documents } returns listOf(mockDocument1, mockDocument2, mockDocument3)
//        every { mockResultCollection.addSnapshotListener(capture(slot)) } returns mockk()
//        provider.subscribeNotes().observeForever {
//            Any()
//        }
//        slot.captured.onEvent(mockSnapshot, null)
//        verify(exactly = 1) { mockSnapshot.documents }
//    }
//
//    @Test
//    fun `getNotes should return notes`() {
//        var result: Any? = null
//        val mockSnapshot = mockk<QuerySnapshot>()
//        val slot = slot<EventListener<QuerySnapshot>>()
//        every { mockSnapshot.documents } returns listOf(mockDocument1, mockDocument2, mockDocument3)
//        every { mockResultCollection.addSnapshotListener(capture(slot)) } returns mockk()
//
//        provider.subscribeNotes().observeForever {
//            result = (it as NoteResult.Success<*>).data
//        }
//        slot.captured.onEvent(mockSnapshot, null)
//        assertEquals(testNotes, result)
//    }
//
//    @Test
//    fun `getNotes should return error`() {
//        var result: Throwable? = null
//        val testError = mockk<FirebaseFirestoreException>()
//        val slot = slot<EventListener<QuerySnapshot>>()
//        every { mockResultCollection.addSnapshotListener(capture(slot)) } returns mockk()
//
//        provider.subscribeNotes().observeForever {
//            result = (it as Error).error
//        }
//        slot.captured.onEvent(null, testError)
//        assertEquals(testError, result)
//    }
//
//    @Test
//    fun `saveNote calls set once`() {
//        val mockDocumentReference = mockk<DocumentReference>()
//        every { mockResultCollection.document(testNotes[0].id) } returns mockDocumentReference
//        provider.saveNote(testNotes[0])
//        verify(exactly = 1) { mockDocumentReference.set(testNotes[0]) }
//    }
//
//    @Test
//    fun `saveNote should return note`() {
//        var result: Note? = null
//        val mockDocumentReference = mockk<DocumentReference>()
//        val slot = slot<OnSuccessListener<Void>>()
//        every { mockResultCollection.document(testNotes[0].id) } returns mockDocumentReference
//        every { mockDocumentReference.set(testNotes[0]).addOnSuccessListener(capture(slot)).addOnFailureListener(any()) } returns mockk()
//
//        provider.saveNote(testNotes[0]).observeForever {
//            result = (it as NoteResult.Success<*>).data as Note?
//        }
//        slot.captured.onSuccess(null)
//        assertEquals(testNotes[0], result)
//    }
//
//    @Test
//    fun `saveNote should return error`(){
//        var result: Throwable? = null
//        val testError = mockk<FirebaseFirestoreException>()
//        val mockDocumentReference = mockk<DocumentReference>()
//        val slot = slot<OnFailureListener>()
//        every { mockResultCollection.document(testNotes[0].id)} returns mockDocumentReference
//        every { mockDocumentReference.set(testNotes[0]).addOnSuccessListener (any()).addOnFailureListener(capture(slot))} returns mockk()
//        provider.saveNote(testNotes[0]).observeForever {
//            result = (it as Error).error
//        }
//        slot.captured.onFailure(testError)
//        assertEquals(testError, result)
//    }
//
//    @Test
//    fun `deleteNote calls delete once`() {
//        val mockDocumentReference = mockk<DocumentReference>()
//        every { mockResultCollection.document(testNotes[0].id) } returns mockDocumentReference
//        provider.deleteNote(testNotes[0].id)
//        verify(exactly = 1) { mockDocumentReference.delete() }
//    }
//
//    @Test
//    fun `deleteNote return null`(){
//        var result: Any? = null
//        val mockDocumentReference = mockk<DocumentReference>()
//        val slot = slot<OnSuccessListener<Void>>()
//        every { mockResultCollection.document(testNotes[0].id)} returns mockDocumentReference
//        every { mockDocumentReference.delete().addOnSuccessListener (capture(slot)).addOnFailureListener(any())} returns mockk()
//        provider.deleteNote(testNotes[0].id).observeForever {
//            result = (it as NoteResult.Success<*>).data
//        }
//        slot.captured.onSuccess(null)
//        assertEquals(null, result)
//    }
//
//    @Test
//    fun `deleteNote return error`(){
//        var result: Throwable? = null
//        val testError = mockk<FirebaseFirestoreException>()
//        val mockDocumentReference = mockk<DocumentReference>()
//        val slot = slot<OnFailureListener>()
//        every { mockResultCollection.document(testNotes[0].id)} returns mockDocumentReference
//        every { mockDocumentReference.delete().addOnSuccessListener (any()).addOnFailureListener(capture(slot))} returns mockk()
//        provider.deleteNote(testNotes[0].id).observeForever {
//            result = (it as Error).error
//        }
//        slot.captured.onFailure(testError)
//        assertEquals(testError, result)
//    }
}