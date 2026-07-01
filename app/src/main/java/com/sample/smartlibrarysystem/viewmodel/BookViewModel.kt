package com.sample.smartlibrarysystem.viewmodel

import com.sample.smartlibrarysystem.model.BookModel
import com.sample.smartlibrarysystem.repo.BookRepoImpl

class BookViewModel {

    private val repo = BookRepoImpl()

    fun addBook(book: BookModel, callback: (Boolean, String) -> Unit) {
        repo.addBook(book, callback)
    }

    fun getAllBooks(callback: (Boolean, String, List<BookModel>) -> Unit) {
        repo.getAllBooks(callback)
    }

    fun updateBook(book: BookModel, callback: (Boolean, String) -> Unit) {
        repo.updateBook(book, callback)
    }

    fun deleteBook(bookId: String, callback: (Boolean, String) -> Unit) {
        repo.deleteBook(bookId, callback)
    }
}