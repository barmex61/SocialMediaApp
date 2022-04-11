package com.fatih.instagramapp.view.loginui

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import com.fatih.instagramapp.MainCoroutineRule
import com.fatih.instagramapp.getOrAwaitValueTest
import com.fatih.instagramapp.repositories.FakeModelRepositories
import com.fatih.instagramapp.util.Status
import com.fatih.instagramapp.view.loginui.viewmodel.SignInViewModel
import com.google.common.truth.Truth.assertThat
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class SignInViewModelTest {

    @get:Rule
    var instantTaskExecutorRule=InstantTaskExecutorRule()
    @get:Rule
    var mainCoroutineRule=MainCoroutineRule()

    private lateinit var viewModel: SignInViewModel

    @Before
    fun setup(){
        FirebaseApp.initializeApp(ApplicationProvider.getApplicationContext())
        viewModel= SignInViewModel(FakeModelRepositories())
    }

    @Test
    fun `logIn without password returns error`(){
        viewModel.logIn("ss@hotmail.com","", FirebaseAuth.getInstance())
        val result=viewModel.controlMessage.getOrAwaitValueTest()
        assertThat(result.status).isEqualTo(Status.ERROR)
    }

    @Test
    fun `logIn without email returns error`(){
        viewModel.logIn("","123456", FirebaseAuth.getInstance())
        val result=viewModel.controlMessage.getOrAwaitValueTest()
        assertThat(result.status).isEqualTo(Status.ERROR)
    }

    @Test
    fun `logIn without email and password returns error`(){
        viewModel.logIn("","", FirebaseAuth.getInstance())
        val result=viewModel.controlMessage.getOrAwaitValueTest()
        assertThat(result.status).isEqualTo(Status.ERROR)
    }

    @Test
    fun `logIn with email and password returns success`(){
        viewModel.logIn("haunter61@hotmail.com","123456", FirebaseAuth.getInstance())
        val result=viewModel.controlMessage.getOrAwaitValueTest()
        assertThat(result.status).isEqualTo(Status.SUCCESS)
    }

    @Test
    fun `log in with google without email returns error`(){
        viewModel.logIn("","123456",FirebaseAuth.getInstance())
        val result=viewModel.controlMessage.getOrAwaitValueTest()
        assertThat(result.status).isEqualTo(Status.ERROR)
    }

}