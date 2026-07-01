package com.sample.smartlibrarysystem.repo

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.sample.smartlibrarysystem.model.UserModel

class UserRepoImp : UserRepo {

    private val auth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance()
    private val ref = database.getReference("users")

    override fun login(
        email: String,
        password: String,
        callback: (Int, String, String) -> Unit
    ) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val userId = auth.currentUser?.uid ?: ""
                    callback(200, "Login Success", userId)
                } else {
                    callback(500, task.exception?.message ?: "Login failed", "")
                }
            }
    }

    override fun register(
        email: String,
        password: String,
        callback: (Boolean, String, String) -> Unit
    ) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val userId = auth.currentUser?.uid ?: ""
                    callback(true, "Registration Successful", userId)
                } else {
                    Log.e("Firebase", task.exception?.message ?: "Unknown error")
                    callback(false, task.exception?.message ?: "Registration failed", "")
                }
            }
    }

    override fun forgetPassword(
        email: String,
        callback: (Boolean, String) -> Unit
    ) {
        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    callback(true, "Reset link sent to $email")
                } else {
                    callback(false, task.exception?.message ?: "Failed to send reset email")
                }
            }
    }

    override fun addUser(
        id: String,
        model: UserModel,
        callback: (Boolean, String) -> Unit
    ) {
        ref.child(id).setValue(model)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    callback(true, "User saved successfully")
                } else {
                    callback(false, task.exception?.message ?: "Failed to save user")
                }
            }
    }

    override fun getUserById(
        id: String,
        callback: (Boolean, String, UserModel?) -> Unit
    ) {
        ref.child(id).get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val snapshot = task.result
                    if (snapshot.exists()) {
                        val user = snapshot.getValue(UserModel::class.java)
                        callback(true, "User fetched", user)
                    } else {
                        callback(false, "User does not exist", null)
                    }
                } else {
                    callback(false, task.exception?.message ?: "Failed to fetch user", null)
                }
            }
    }

    override fun getAllUser(
        callback: (Boolean, String, List<UserModel>) -> Unit
    ) {
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val users = mutableListOf<UserModel>()

                for (data in snapshot.children) {
                    val user = data.getValue(UserModel::class.java)
                    if (user != null) {
                        users.add(user)
                    }
                }

                callback(true, "Users fetched", users)
            }

            override fun onCancelled(error: DatabaseError) {
                callback(false, error.message, emptyList())
            }
        })
    }

    override fun editProfile(
        id: String,
        model: UserModel,
        callback: (Boolean, String) -> Unit
    ) {
        ref.child(id).updateChildren(model.toMap())
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    callback(true, "Profile updated")
                } else {
                    callback(false, task.exception?.message ?: "Profile update failed")
                }
            }
    }

    override fun deleteUser(
        id: String,
        callback: (Boolean, String) -> Unit
    ) {
        ref.child(id).removeValue()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    callback(true, "User deleted")
                } else {
                    callback(false, task.exception?.message ?: "Delete failed")
                }
            }
    }

    override fun logOut(
        callback: (Boolean, String) -> Unit
    ) {
        try {
            auth.signOut()
            callback(true, "Logout successful")
        } catch (e: Exception) {
            callback(false, e.message ?: "Logout failed")
        }
    }
}