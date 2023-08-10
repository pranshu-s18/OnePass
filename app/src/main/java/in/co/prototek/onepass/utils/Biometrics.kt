package `in`.co.prototek.onepass.utils

import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import `in`.co.prototek.onepass.MainActivity
import `in`.co.prototek.onepass.R

class Biometrics(val context: AppCompatActivity) {
    fun canAuthenticate(onSuccess: () -> Unit) {
        if (check()) {
            val executor = ContextCompat.getMainExecutor(context)
            val promptInfo = BiometricPrompt.PromptInfo.Builder()
                .setTitle(context.getString(R.string.B_Title))
                .setDescription(context.getString(R.string.B_Description))
                .setAllowedAuthenticators(BiometricManager.Authenticators.BIOMETRIC_STRONG)
                .setNegativeButtonText("Cancel")
                .build()

            val biometricPrompt = BiometricPrompt(context, executor,
                object : BiometricPrompt.AuthenticationCallback() {
                    override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                        Log.d(MainActivity.TAG, errString.toString())
                        showError(errorCode)
                        context.finishAndRemoveTask()
                    }

                    override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                        super.onAuthenticationSucceeded(result)
                        onSuccess()
                    }
                })

            biometricPrompt.authenticate(promptInfo)
        } else {
            context.finishAndRemoveTask()
        }
    }

    private fun check(): Boolean {
        val biometricManager = BiometricManager.from(context)
        val res = biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG)
        return if (res == BiometricManager.BIOMETRIC_SUCCESS) true else {
            showError(res)
            false
        }
    }

    private fun showError(errorCode: Int) {
        val message =
            when (errorCode) {
                BiometricPrompt.ERROR_NEGATIVE_BUTTON, BiometricPrompt.ERROR_USER_CANCELED ->
                    context.getString(R.string.B_Cancel)

                BiometricPrompt.ERROR_LOCKOUT, BiometricPrompt.ERROR_LOCKOUT_PERMANENT ->
                    context.getString(R.string.B_Lock)

                BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE, BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE, BiometricManager.BIOMETRIC_ERROR_SECURITY_UPDATE_REQUIRED ->
                    context.getString(R.string.B_Unavailable)

                BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> context.getString(R.string.B_Not_Enrolled)
                else -> context.getString(R.string.B_Error)
            }

        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
    }
}
