package com.muslim.browser.pro.browser

import android.webkit.JavascriptInterface

/**
 * JavaScript interface bridge to handle blob: and large in-page downloads.
 * Exposed to WebView under the name "AndroidBlobBridge".
 */
class BlobDownloadBridge(
    @Volatile var onReadyCallback: ((base64: String, mime: String) -> Unit)? = null,
    @Volatile var onErrorCallback: ((message: String) -> Unit)? = null
) {

    @JavascriptInterface
    fun onBlobReady(base64: String, mime: String) {
        onReadyCallback?.invoke(base64, mime)
    }

    @JavascriptInterface
    fun onBlobError(message: String) {
        onErrorCallback?.invoke(message)
    }
}
