package com.ihsanproject.client.activities


import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import com.ihsanproject.client.R
import com.ihsanproject.client.UnauthorizedEvent
import com.ihsanproject.client.application.viewmodels.AuthViewModel
import com.ihsanproject.client.application.viewmodels.AuthViewModelFactory
import com.ihsanproject.client.proxyInstances.GoogleSSOProxyInstance
import com.ihsanproject.client.repositoryInstances.ProfileRepositoryInstance
import com.ihsanproject.client.repositoryInstances.SettingsRepositoryInstance
import kotlinx.coroutines.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode


/*
* Loading Indicator Controls
*/

fun AppCompatActivity.displayLoading() {
    val inflater = LayoutInflater.from(this)
    val loadingLayout = inflater.inflate(R.layout.view_loading, null, false) as FrameLayout
    loadingLayout.setVisibility(View.VISIBLE);

    var params = FrameLayout.LayoutParams(
        FrameLayout.LayoutParams.MATCH_PARENT,
        FrameLayout.LayoutParams.MATCH_PARENT
    )

    loadingLayout.layoutParams = params

    mainLayout.addView(loadingLayout)
}

fun AppCompatActivity.dismissLoading() {
    val loadingView : FrameLayout = findViewById(R.id.loading_overlay)
    mainLayout.removeView(loadingView)
}

val AppCompatActivity.mainLayout: ViewGroup
    get() {
        return findViewById<ViewGroup>(android.R.id.content)
    }


abstract class ActivityBase : AppCompatActivity() {
    /*
    * The following is code to observe responses to asynchronous Intent actions
    *
    * Courtesy of: https://medium.com/@airtdave/streamlining-intents-with-kotlin-coroutines-33190a56c869
    * Used to create an async flow, a well as managing multiple intents
    */

    private var currentCode : Int = 0
    private var resultByCode = mutableMapOf<Int, CompletableDeferred<ActivityResult?>>()

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        resultByCode[requestCode]?.let {
            it.complete(ActivityResult(resultCode, data))
            resultByCode.remove(requestCode)
        } ?: run {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    /**
     * Launches the intent allowing to process the result using await()
     *
     * @param intent the intent to be launched.
     *
     * @return Deferred<ActivityResult>

     * Example usage:
     *
     * GlobalScope.launch(Dispatchers.Main) {
     *   val result = launchIntentAsync(intent).await()
     *   result?.data?.let {
     *     // Do something with "it"
     *   }
     * }
     */
    fun launchIntentAsync(intent: Intent) : Deferred<ActivityResult?>
    {
        val activityResult = CompletableDeferred<ActivityResult?>()

        if (intent.resolveActivity(packageManager) != null) {
            val resultCode = currentCode++
            resultByCode[resultCode] = activityResult
            startActivityForResult(intent, resultCode)
        } else {
            activityResult.complete(null)
        }
        return activityResult
    }

    fun signOut() {
        val settingsRepository = SettingsRepositoryInstance(this)
        val profileRepository = ProfileRepositoryInstance(this)
        val googleSSOProxy = GoogleSSOProxyInstance(this)
        val authViewModel = ViewModelProviders
            .of(this, AuthViewModelFactory(this, settingsRepository, profileRepository, googleSSOProxy))
            .get(AuthViewModel::class.java)

        GlobalScope.launch(Dispatchers.Main) {
            authViewModel.deauthorize()

            Toast.makeText(this@ActivityBase, "Successfully signed out", Toast.LENGTH_SHORT).show()

            val intent = Intent(this@ActivityBase, AuthActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    open fun onUnauthorizedEvent(event: UnauthorizedEvent) {
        signOut()
    }

    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    override fun onStop() {
        super.onStop()
        EventBus.getDefault().unregister(this)
    }
}

/**
 * Wraps the parameters of onActivityResult
 *
 * @property resultCode the result code returned from the activity.
 * @property data the optional intent returned from the activity.
 */
class ActivityResult(
    val resultCode: Int,
    val data: Intent?
) {
}