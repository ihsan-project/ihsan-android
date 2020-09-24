package com.khatm.client.activities


import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import com.khatm.client.R
import kotlinx.coroutines.*

/*
* Loading Indicator Controls
*/

fun AppCompatActivity.displayLoading() {
    val inflater = LayoutInflater.from(this)
    val loadingLayout = inflater.inflate(R.layout.view_loading, null, false) as FrameLayout
    loadingLayout.setVisibility(View.VISIBLE);

    var params = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT)

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