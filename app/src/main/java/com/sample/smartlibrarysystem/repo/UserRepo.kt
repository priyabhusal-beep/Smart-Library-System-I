package com.sample.smartlibrarysystem.repo

import com.sample.smartlibrarysystem.model.UserModel

interface UserRepo {

    fun login(
        email: String,
        password: String,
        callback: (Int, String, String) -> Unit
    )

    fun register(
        email: String,
        password: String,
        callback: (Boolean, String, String) -> Unit
    )

    fun forgetPassword(
        email: String,
        callback: (Boolean, String) -> Unit
    )

    fun addUser(
        id: String,
        model: UserModel,
        callback: (Boolean, String) -> Unit
    )


    fun getUserById(
        id: String,
        callback: (Boolean, String, UserModel?) -> Unit
    )

    fun getAllUser(
        callback: (Boolean, String, List<UserModel>) -> Unit
    )

    fun editProfile(
        id: String,
        model: UserModel,
        callback: (Boolean, String) -> Unit
    )

    fun deleteUser(
        id: String,
        callback: (Boolean, String) -> Unit
    )

    fun logOut(
        callback: (Boolean, String) -> Unit
    )
}