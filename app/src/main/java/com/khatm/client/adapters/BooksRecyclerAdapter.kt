package com.khatm.client.adapters

import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.khatm.client.R
import com.khatm.client.domain.models.BookModel
import com.khatm.client.extensions.inflate
import kotlinx.android.synthetic.main.books_recyclerview_item.view.*

class BooksRecyclerAdapter(private val books: List<BookModel>) : RecyclerView.Adapter<BooksRecyclerAdapter.BookHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BooksRecyclerAdapter.BookHolder {
        val inflatedView = parent.inflate(R.layout.books_recyclerview_item, false)
        return BookHolder(inflatedView)
    }

    override fun getItemCount(): Int = books.size

    override fun onBindViewHolder(holder: BooksRecyclerAdapter.BookHolder, position: Int) {
        holder.bindView(books[position])
    }

    class BookHolder(v: View) : RecyclerView.ViewHolder(v), View.OnClickListener {
        private var view: View = v
        private var book: BookModel? = null

        init {
            v.setOnClickListener(this)
        }

        override fun onClick(v: View) {
            Log.d("RecyclerView", "CLICK!")
        }

        companion object {
            private val BOOK_KEY = "BOOK_ITEM"
        }

        fun bindView(book: BookModel) {
            this.book = book

            view.itemTitle.text = book.title
            view.itemDescription.text = book.slug
        }
    }

}