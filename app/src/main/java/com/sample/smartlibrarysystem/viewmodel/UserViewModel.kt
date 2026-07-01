package com.sample.smartlibrarysystem.viewmodel

import androidx.lifecycle.ViewModel
import com.sample.smartlibrarysystem.model.UserModel
import com.sample.smartlibrarysystem.repo.UserRepo
import com.sample.smartlibrarysystem.repo.UserRepoImp

class UserViewModel : ViewModel() {

    private val repo: UserRepo = UserRepoImp()

    fun login(
        email: String,
        password: String,
        callback: (Boolean, String, String) -> Unit
    ) {
        if (email.isEmpty() || password.isEmpty()) {
            callback(false, "Please fill all fields", "")
            return
        }

        repo.login(email, password) { code, message, userId ->
            if (code == 200) {
                callback(true, message, userId)
            } else {
                callback(false, message, "")
            }
        }
    }

    fun register(
        name: String,
        email: String,
        phone: String,
        password: String,
        confirmPassword: String,
        callback: (Boolean, String) -> Unit
    ) {
        if (name.isEmpty() || email.isEmpty() || phone.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            callback(false, "Please fill all fields")
            return
        }

        if (password != confirmPassword) {
            callback(false, "Password and Confirm Password do not match")
            return
        }

        repo.register(email, password) { success, message, userId ->
            if (success) {
                val userModel = UserModel(
                    id = userId,
                    name = name,
                    email = email,
                    contact = phone,
                    address = ""
                )

                repo.addUser(userId, userModel) { dbSuccess, dbMessage ->
                    callback(dbSuccess, dbMessage)
                }
            } else {
                callback(false, message)
            }
        }
    }

    fun forgotPassword(
        email: String,
        callback: (Boolean, String) -> Unit
    ) {
        repo.forgetPassword(email, callback)
    }
    fun deleteUser(
        id: String,
        callback: (Boolean, String) -> Unit
    ) {
        repo.deleteUser(id, callback)
    }
}

