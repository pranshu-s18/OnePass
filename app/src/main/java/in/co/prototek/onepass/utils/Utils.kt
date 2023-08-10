package `in`.co.prototek.onepass.utils

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import `in`.co.prototek.onepass.data.Credential

// Show keyboard
fun showKeyboard(tv: TextView, context: Context) {
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.showSoftInput(tv, InputMethodManager.RESULT_UNCHANGED_SHOWN)
}

// Hide keyboard
fun hideKeyboard(tv: TextView, context: Context) {
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(tv.windowToken, InputMethodManager.RESULT_UNCHANGED_SHOWN)
}

// Copy text to clipboard
fun copyToClipboard(context: Context, text: String) {
    val clipboard = context.getSystemService(ClipboardManager::class.java)
    val clip = ClipData.newPlainText("Copied Text", text)
    clipboard.setPrimaryClip(clip)
}

// Convert list of credentials to CSV
fun credentialsToCSV(credentials: List<Credential>): String {
    return credentials.joinToString("\n") { it.toString() }
}

// Wrap URL with https:// if both http:// and https:// are missing
fun handleHttps(url: String): String {
    return if (url.startsWith("http://") || url.startsWith("https://")) url else "https://$url"
}