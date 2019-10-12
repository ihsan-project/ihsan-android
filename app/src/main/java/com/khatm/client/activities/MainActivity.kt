package com.khatm.client.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.khatm.client.BuildConfig
import com.khatm.client.R

class MainActivity : AppCompatActivity() {

    // request code used for logging into google
    val RC_SIGN_IN: Int = BuildConfig.googleRequestClientId
    lateinit var googleSignInButton: SignInButton
    lateinit var mGoogleSignInClient: GoogleSignInClient


    /*
     * called when the activity starts
     *
     * initialize objects
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d("MainActivity.kt", "onCreate() called")

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        googleSignInButton = findViewById(R.id.button_sign_in_google)

        /*
         * Configure sign-in to request the user's ID, email address, and basic profile.
         * ID and basic profile are included in DEFAULT_SIGN_IN.
         */
        val gso: GoogleSignInOptions = GoogleSignInOptions
            .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(BuildConfig.googleWebApplicationClientId)
            .requestEmail()
            .build()

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)

        googleSignInButton.setOnClickListener { signInGoogle() }
    }


    /*
     * When activity start getting visible to user then onStart() will be called.
     *
     * If the activity is in onPause() condition (not visible to user),
     * and if user again launch the activity,
     * then onStart() method will be called.
     *
     * check if a user has already signed in in a previous session
     */
    override fun onStart() {
        super.onStart()

        Log.d("MainActivity.kt", "onStart() called")

        val account: GoogleSignInAccount? = GoogleSignIn.getLastSignedInAccount(this)

        if (account != null) {
            goToNextScreen()
        }
    }


    /*
     * attempt to sign into a google account if not already signed in
     *
     * handle sign-in button taps by creating a sign-in intent with the getSignInIntent method,
     * and starting the intent with startActivityForResult.
     */
    private fun signInGoogle() {
        Log.d("MainActivity.kt", "signInGoogle() called")

        // Check for existing Google Sign In account, if the user is already signed in the GoogleSignInAccount will be non-null.
        val account: GoogleSignInAccount? = GoogleSignIn.getLastSignedInAccount(this)

        // Signed in successfully
        if (account != null){
            goToNextScreen()
        }
        else {
            // attempt to let the user log into a google account
            val signInIntent: Intent = mGoogleSignInClient.signInIntent
            startActivityForResult(signInIntent, RC_SIGN_IN)
        }
    }


    /*
     * Starting the intent prompts the user to select a Google account to sign in with.
     * If requested scopes beyond profile, email, and openid, the user is also prompted to grant access to the requested resources
     *
     * When the user interacts with the activity started by the explicit intent from startActivityForResult() in
     * the googleSignIn() function, it will pass back a result that will be captured by this function
     */
    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        Log.d("MainActivity.kt", "onActivityResult() called")

        super.onActivityResult(requestCode, resultCode, data)

        /*
         * After the user signs in, can get a GoogleSignInAccount object for the user in the activity's onActivityResult method.
         * Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
         */
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, so no need to attach a listener.
            val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)
        }
    }


    /*
     * Called after the user allows a sign in attempt so we can validate if the user was successfully
     * able to log in in order to go to the home screen
     */
    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        Log.d("MainActivity.kt", "handleSignInResult() called")

        try {
            val account: GoogleSignInAccount? = completedTask.getResult(ApiException::class.java)

            Log.d("MainActivity.kt", "mmi: account: $account")

            // Signed in successfully
            if (account != null) {
                goToNextScreen()
            }
        }
        catch (e: ApiException) {
            Log.e("MainActivity.kt", "mmi: error: $e")
            // The ApiException status code indicates the detailed failure reason.
            Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show()
        }
    }


    /*
     * function used to go from the login screen to the home screen
     */
    private fun goToNextScreen() {
        Log.d("MainActivity.kt", "goToNextScreen() called")

        Toast.makeText(this, "Successfully logged in", Toast.LENGTH_SHORT).show()

        val intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)
    }
}
