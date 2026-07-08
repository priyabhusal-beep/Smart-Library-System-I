package com.sample.smartlibrarysystem

import com.sample.smartlibrarysystem.repo.UserRepo
import com.sample.smartlibrarysystem.viewmodel.UserViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.mockito.kotlin.*

class UserViewModelTest {

    @Test
    fun login_success_test() {
        val repo = mock<UserRepo>()
        val viewModel = UserViewModel(repo)

        doAnswer { invocation ->
            val callback = invocation.getArgument<(Int, String, String) -> Unit>(2)
            callback(200, "Login Success", "user123")
            null
        }.`when`(repo).login(eq("test@gmail.com"), eq("123456"), any())

        var successResult = false
        var messageResult = ""
        var userIdResult = ""

        viewModel.login("test@gmail.com", "123456") { success, message, userId ->
            successResult = success
            messageResult = message
            userIdResult = userId
        }

        assertTrue(successResult)
        assertEquals("Login Success", messageResult)
        assertEquals("user123", userIdResult)

        verify(repo).login(eq("test@gmail.com"), eq("123456"), any())
    }

    @Test
    fun login_empty_fields_test() {
        val repo = mock<UserRepo>()
        val viewModel = UserViewModel(repo)

        var successResult = true
        var messageResult = ""

        viewModel.login("", "") { success, message, _ ->
            successResult = success
            messageResult = message
        }

        assertFalse(successResult)
        assertEquals("Please fill all fields", messageResult)

        verify(repo, never()).login(any(), any(), any())
    }

    @Test
    fun login_failed_test() {
        val repo = mock<UserRepo>()
        val viewModel = UserViewModel(repo)

        doAnswer { invocation ->
            val callback = invocation.getArgument<(Int, String, String) -> Unit>(2)
            callback(500, "Invalid email or password", "")
            null
        }.`when`(repo).login(eq("wrong@gmail.com"), eq("wrong123"), any())

        var successResult = true
        var messageResult = ""

        viewModel.login("wrong@gmail.com", "wrong123") { success, message, _ ->
            successResult = success
            messageResult = message
        }

        assertFalse(successResult)
        assertEquals("Invalid email or password", messageResult)

        verify(repo).login(eq("wrong@gmail.com"), eq("wrong123"), any())
    }
    @Test
    fun register_password_mismatch_test() {
        val repo = mock<UserRepo>()
        val viewModel = UserViewModel(repo)

        var successResult = true
        var messageResult = ""

        viewModel.register(
            name = "Priya",
            email = "test@gmail.com",
            phone = "9800000000",
            password = "123456",
            confirmPassword = "1234567"
        ) { success, message ->
            successResult = success
            messageResult = message
        }

        assertFalse(successResult)
        assertEquals("Password and Confirm Password do not match", messageResult)

        verify(repo, never()).register(any(), any(), any())
    }
}