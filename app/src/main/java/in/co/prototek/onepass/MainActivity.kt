package `in`.co.prototek.onepass

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupActionBarWithNavController
import java.util.concurrent.Executor

class MainActivity : AppCompatActivity() {
    private lateinit var navController: NavController
    private lateinit var executor: Executor
    private lateinit var biometricPrompt: BiometricPrompt
    private lateinit var promptInfo: BiometricPrompt.PromptInfo

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        setContentView(R.layout.activity_main)

        executor = ContextCompat.getMainExecutor(this)
        biometricPrompt =
            BiometricPrompt(this, executor, object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    Log.d(TAG, errString.toString())
                    Toast.makeText(
                        this@MainActivity,
                        errorMessage(errorCode, true),
                        Toast.LENGTH_SHORT
                    ).show()
                }

                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
                    navController = navHostFragment.navController
                    navController.setGraph(R.navigation.nav_graph)
                    navController.addOnDestinationChangedListener { _, destination, arguments ->
                        if (destination.id == R.id.generator) {
                            val name = if(arguments!!.getBoolean("isPassword")) "Password" else "Username"
                            destination.label = "$name Generator"
                        }
                    }

                    setupActionBarWithNavController(navController)
                }
            })

        promptInfo = BiometricPrompt.PromptInfo.Builder().setTitle(getString(R.string.B_Title))
            .setAllowedAuthenticators(Authenticators.BIOMETRIC_STRONG)
            .setNegativeButtonText("Cancel").build()
    }

    override fun onStart() {
        super.onStart()
        if (checkBiometrics()) biometricPrompt.authenticate(promptInfo)
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

    private fun checkBiometrics(): Boolean {
        val biometricManager = BiometricManager.from(this)
        val check = biometricManager.canAuthenticate(Authenticators.BIOMETRIC_STRONG)
        return if (check == BiometricManager.BIOMETRIC_SUCCESS) true
        else {
            Toast.makeText(this, errorMessage(check, false), Toast.LENGTH_SHORT).show()
            false
        }
    }

    private fun errorMessage(errorCode: Int, authError: Boolean): String {
        return if (authError) {
            when (errorCode) {
                BiometricPrompt.ERROR_NEGATIVE_BUTTON, BiometricPrompt.ERROR_USER_CANCELED -> getString(
                    R.string.B_Cancel
                )
                BiometricPrompt.ERROR_LOCKOUT, BiometricPrompt.ERROR_LOCKOUT_PERMANENT -> getString(
                    R.string.B_Lock
                )
                else -> getString(R.string.B_Error)
            }
        } else {
            when (errorCode) {
                BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE, BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE, BiometricManager.BIOMETRIC_ERROR_SECURITY_UPDATE_REQUIRED -> getString(
                    R.string.B_Unavailable
                )
                BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> getString(R.string.B_Not_Enrolled)
                else -> getString(R.string.B_Error)
            }
        }
    }

    companion object {
        const val TAG = "ONEPASS_LOGS"
    }
}