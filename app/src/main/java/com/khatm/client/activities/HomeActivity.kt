package com.khatm.client.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.khatm.client.R
import com.khatm.client.adapters.BooksRecyclerAdapter
import com.khatm.client.adapters.LoadingAdapter
import com.khatm.client.application.viewmodels.*
import com.khatm.client.proxyInstances.GoogleSSOProxyInstance
import com.khatm.client.repositoryInstances.BooksRepositoryInstance
import com.khatm.client.repositoryInstances.ProfileRepositoryInstance
import com.khatm.client.repositoryInstances.SettingsRepositoryInstance
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


    private fun signOut() {
        // TODO: Need to move this to a better place
        val settingsRepository = SettingsRepositoryInstance(this)
        val profileRepository = ProfileRepositoryInstance(this)
        val googleSSOProxy = GoogleSSOProxyInstance(this)
        val authViewModel = ViewModelProviders
            .of(this, AuthViewModelFactory(this, settingsRepository, profileRepository, googleSSOProxy))
            .get(AuthViewModel::class.java)

        GlobalScope.launch(Dispatchers.Main) {
            authViewModel.deauthorize()

            Toast.makeText(this@HomeActivity, "Successfully signed out", Toast.LENGTH_SHORT).show()

            val intent = Intent(this@HomeActivity, AuthActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}