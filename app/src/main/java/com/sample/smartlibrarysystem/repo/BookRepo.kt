
package com.sample.smartlibrarysystem.repo

import com.sample.smartlibrarysystem.model.BookModel

interface BookRepo {
    fun addBook(book: BookModel, callback: (Boolean, String) -> Unit)
    fun getAllBooks(callback: (Boolean, String, List<BookModel>) -> Unit)
    fun updateBook(book: BookModel, callback: (Boolean, String) -> Unit)
    fun deleteBook(bookId: String, callback: (Boolean, String) -> Unit)
}