package com.khatm.client.adapters

import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.khatm.client.R
import com.khatm.client.domain.models.BookModel
import com.khatm.client.extensions.inflate
import kotlinx.android.synthetic.main.books_recyclerview_item.view.*

class BooksRecyclerAdapter : PagingDataAdapter<BookModel, BooksRecyclerAdapter.BookHolder>(BookDiffUtilCallBack()) {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BooksRecyclerAdapter.BookHolder {
        val inflatedView = parent.inflate(R.layout.books_recyclerview_item, false)
        return BookHolder(inflatedView)
    }

    override fun onBindViewHolder(holder: BookHolder, position: Int) {
        getItem(position)?.let { holder.bindView(it) }
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

class BookDiffUtilCallBack : DiffUtil.ItemCallback<BookModel>() {
    override fun areItemsTheSame(oldItem: BookModel, newItem: BookModel): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: BookModel, newItem: BookModel): Boolean {
        return oldItem.id == newItem.id
                && oldItem.slug == newItem.slug
    }
}