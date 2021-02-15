package com.ihsanproject.client.activities

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.ihsanproject.client.R
import com.ihsanproject.client.adapters.BooksRecyclerAdapter
import com.ihsanproject.client.adapters.LoadingAdapter
import com.ihsanproject.client.application.viewmodels.*
import com.ihsanproject.client.repositoryInstances.BooksRepositoryInstance
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class HomeActivity : ActivityBase() {
    private lateinit var homeViewModel: HomeViewModel

    lateinit var signOutButton: Button
    lateinit var textViewName : TextView
    lateinit var textViewEmail : TextView
    lateinit var textViewId : TextView
    lateinit var booksRecyclerView : RecyclerView

    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var adapter: BooksRecyclerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val booksRepository = BooksRepositoryInstance(this)
        homeViewModel = ViewModelProviders
            .of(this, HomeViewModelFactory(this, booksRepository))
            .get(HomeViewModel::class.java)

        textViewName = findViewById(R.id.TextViewName)
        textViewEmail = findViewById(R.id.textViewEmail)
        textViewId = findViewById(R.id.textViewId)
        signOutButton = findViewById(R.id.button_sign_out_google)
        booksRecyclerView = findViewById(R.id.recyclerView_books)

        signOutButton.setOnClickListener {
            signOut()
        }

        val account : GoogleSignInAccount? = GoogleSignIn.getLastSignedInAccount(this)

        // if there is a signed in account
        if (account != null) {
            val name = account.displayName
            val email = account.email
            val id = account.id

            textViewName.setText("Name: " + name)
            textViewEmail.setText("Email: " + email)
            textViewId.setText("ID: " + id)
        }

        linearLayoutManager = LinearLayoutManager(this)
        booksRecyclerView.layoutManager = linearLayoutManager

        adapter = BooksRecyclerAdapter()
        booksRecyclerView.adapter = adapter.withLoadStateHeaderAndFooter(
            header = LoadingAdapter { adapter.retry() },
            footer = LoadingAdapter { adapter.retry() }
        )
    }


    override fun onStart() {
        super.onStart()

        GlobalScope.launch(Dispatchers.Main) {
            homeViewModel.onPage {
                adapter.submitData(it)
            }
        }
    }
}