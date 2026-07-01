package com.sample.smartlibrarysystem.repo

import com.google.firebase.database.FirebaseDatabase
import com.sample.smartlibrarysystem.model.BookModel

class BookRepoImpl : BookRepo {

    private val ref = FirebaseDatabase.getInstance().getReference("books")

    override fun addBook(book: BookModel, callback: (Boolean, String) -> Unit) {
        val id = ref.push().key ?: ""
        val newBook = book.copy(id = id)

        ref.child(id).setValue(newBook)
            .addOnSuccessListener { callback(true, "Book added successfully") }
            .addOnFailureListener { callback(false, it.message ?: "Failed to add book") }
    }

    override fun getAllBooks(callback: (Boolean, String, List<BookModel>) -> Unit) {
        ref.get()
            .addOnSuccessListener { snapshot ->
                val books = mutableListOf<BookModel>()
                for (child in snapshot.children) {
                    val book = child.getValue(BookModel::class.java)
                    if (book != null) books.add(book)
                }
                callback(true, "Books fetched", books)
            }
            .addOnFailureListener {
                callback(false, it.message ?: "Failed to fetch books", emptyList())
            }
    }
    override fun updateBook(book: BookModel, callback: (Boolean, String) -> Unit) {
        ref.child(book.id).setValue(book)
            .addOnSuccessListener { callback(true, "Book updated") }
            .addOnFailureListener { callback(false, it.message ?: "Update failed") }
    }

    override fun deleteBook(bookId: String, callback: (Boolean, String) -> Unit) {
        ref.child(bookId).removeValue()
            .addOnSuccessListener { callback(true, "Book deleted") }
            .addOnFailureListener { callback(false, it.message ?: "Delete failed") }
    }
}