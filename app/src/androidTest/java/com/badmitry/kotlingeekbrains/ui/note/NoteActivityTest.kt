package com.badmitry.kotlingeekbrains.ui.note

import android.content.Intent
import androidx.lifecycle.MutableLiveData
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.replaceText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.rule.ActivityTestRule
import com.badmitry.kotlingeekbrains.R
import com.badmitry.kotlingeekbrains.data.entity.Note
import com.badmitry.kotlingeekbrains.vm.NoteViewModel
import io.mockk.*
import org.hamcrest.Matchers.not
import org.junit.*
import org.koin.android.viewmodel.ext.koin.viewModel
import org.koin.dsl.module.module
import org.koin.standalone.StandAloneContext
import org.koin.standalone.StandAloneContext.loadKoinModules


class NoteActivityTest {
    @get:Rule
    val activityTestRule = ActivityTestRule(NoteActivity::class.java, true, false)

    private val viewModel: NoteViewModel = mockk(relaxed = true)
    private val viewStateLiveData = MutableLiveData<NoteViewState>()
    private val showProgressBarLiveData = MutableLiveData<Unit>()
    private val testNote = Note("333", "title", "body")

    @Before
    fun setup() {
        loadKoinModules(
                listOf(
                        module {
                            viewModel { viewModel }
                        }
                )
        )
        every { viewModel.getViewState() } returns viewStateLiveData
        every { viewModel.getShowProgressBarLiveData() } returns showProgressBarLiveData
        activityTestRule.launchActivity(null)
        viewStateLiveData.postValue(NoteViewState(note = testNote))

    }

    @After
    fun tearDown() {
        StandAloneContext.stopKoin()
    }

    @Test
    fun should_show_hide_progress_bar() {
        onView(withId(R.id.progress_bar)).check(matches(isDisplayed()))
        showProgressBarLiveData.postValue(Unit)
        onView(withId(R.id.progress_bar)).check(matches(not(isDisplayed())))
    }

    @Test
    fun should_show_note() {
        onView(withId(R.id.field_title)).check(matches(withText(testNote.title)))
        onView(withId(R.id.field_body)).check(matches(withText(testNote.notes)))
    }

    @Test
    fun should_call_viewModel_loadNote_once() {
        Intent().apply {
            putExtra("note", testNote.id)
        }.let {
            activityTestRule.launchActivity(it)
        }
        verify(exactly = 1) { viewModel.loadNote(testNote.id) }
    }

    @Test
    fun should_save_change_pending_note() {
        showProgressBarLiveData.postValue(Unit)
        onView(withId(R.id.field_body)).perform(replaceText("dsf"))
        verify(exactly = 1) {viewModel.saveNote(any(), "dsf") }
    }

    @Test
    fun show_palette() {
        showProgressBarLiveData.postValue(Unit)
        onView(withId(R.id.palette)).perform(click())
        verify(exactly = 1) {viewModel.togglePalette() }
    }

}