package `in`.co.prototek.onepass.utils

import android.content.Context
import android.view.inputmethod.InputMethodManager
import android.widget.TextView

fun showKeyboard(tv: TextView, context: Context) {
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.showSoftInput(tv, InputMethodManager.RESULT_UNCHANGED_SHOWN)
}