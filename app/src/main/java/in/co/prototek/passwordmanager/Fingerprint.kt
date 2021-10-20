package `in`.co.prototek.passwordmanager

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import java.util.concurrent.Executor

class Fingerprint : Fragment() {
    private lateinit var executor: Executor
    private lateinit var biometricPrompt: BiometricPrompt
    private lateinit var promptInfo: BiometricPrompt.PromptInfo

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        executor = ContextCompat.getMainExecutor(requireContext())
        biometricPrompt =
            BiometricPrompt(this, executor, object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    Log.d(MainActivity.TAG, errString.toString())
                    Toast.makeText(requireContext(), errorMessage(errorCode, true), Toast.LENGTH_SHORT).show()
                }

                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    findNavController().navigate(R.id.passwordManager)
                }
            })

        promptInfo = BiometricPrompt.PromptInfo.Builder().setTitle(getString(R.string.B_Title))
            .setAllowedAuthenticators(BiometricManager.Authenticators.BIOMETRIC_STRONG)
            .setNegativeButtonText("Cancel").build()
    }

    override fun onStart() {
        super.onStart()
        if(checkBiometrics()) biometricPrompt.authenticate(promptInfo)
    }

    private fun checkBiometrics(): Boolean {
        val biometricManager = BiometricManager.from(requireContext())
        val check = biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG)
        return if (check == BiometricManager.BIOMETRIC_SUCCESS) true
        else {
            Toast.makeText(requireContext(), errorMessage(check, false), Toast.LENGTH_SHORT).show()
            false
        }
    }

    private fun errorMessage(errorCode: Int, authError: Boolean): String {
        return if (authError) {
            when (errorCode) {
                BiometricPrompt.ERROR_NEGATIVE_BUTTON, BiometricPrompt.ERROR_USER_CANCELED -> getString(R.string.B_Cancel)
                BiometricPrompt.ERROR_LOCKOUT, BiometricPrompt.ERROR_LOCKOUT_PERMANENT -> getString(R.string.B_Lock)
                else -> getString(R.string.B_Error)
            }
        } else {
            when (errorCode) {
                BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE, BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE, BiometricManager.BIOMETRIC_ERROR_SECURITY_UPDATE_REQUIRED -> getString(R.string.B_Unavailable)
                BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> getString(R.string.B_Not_Enrolled)
                else -> getString(R.string.B_Error)
            }
        }
    }
}
