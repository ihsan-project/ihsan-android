package com.khatm.client.extensions


/*
* Courtesy of: https://medium.com/@airtdave/streamlining-intents-with-kotlin-coroutines-33190a56c869
* Used to create an async flow, a well as managing multiple intents
*/

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Deferred


/**
 * Provides a mechanism to launch intents using co-routines.
 * Example usage:
 *
 * GlobalScope.launch(Dispatchers.Main) {
 *   val result = launchIntent(intent).await()
 *   result?.data?.let {
 *     // Do something with "it"
 *   }
 * }
 */
abstract class AsyncActivityExtension : AppCompatActivity() {

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