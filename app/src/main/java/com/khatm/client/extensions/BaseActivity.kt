package com.khatm.client.extensions


import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.khatm.client.R
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Deferred


abstract class BaseActivity : AppCompatActivity() {

    /*
    * Loading Indicator Controls
    */

    fun displayLoading() {
        val layout: ConstraintLayout = findViewById(R.id.activity_auth);
        layout.addView(loadingIndicatorView)
    }

    private val loadingIndicatorView : FrameLayout
        get() {
            val inflater = LayoutInflater.from(this)
            val layout = inflater.inflate(R.layout.view_progress, null, false) as FrameLayout
            layout.setVisibility(View.VISIBLE);

            var params = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT)

            layout.layoutParams = params

            return layout
        }

    /*
    * The following is code to observe responses to asynchronous Intent actions
    *
    * Courtesy of: https://medium.com/@airtdave/streamlining-intents-with-kotlin-coroutines-33190a56c869
    * Used to create an async flow, a well as managing multiple intents
    */

    var currentCode : Int = 0
    var resultByCode = mutableMapOf<Int, CompletableDeferred<ActivityResult?>>()

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
}

/**
 * Wraps the parameters of onActivityResult
 *
 * @property resultCode the result code returned from the activity.
 * @property data the optional intent returned from the activity.
 */
class ActivityResult(
    val resultCode: Int,
    val data: Intent?) {
}