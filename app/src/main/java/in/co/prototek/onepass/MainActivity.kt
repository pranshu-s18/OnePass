package `in`.co.prototek.onepass

import android.os.Bundle
import android.view.MenuItem
import android.view.MotionEvent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.onNavDestinationSelected
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.preference.PreferenceManager
import com.google.android.material.textfield.TextInputEditText
import `in`.co.prototek.onepass.databinding.ActivityMainBinding
import `in`.co.prototek.onepass.utils.Biometrics
import `in`.co.prototek.onepass.utils.hideKeyboard
import `in`.co.prototek.onepass.viewModel.CredentialViewModel
import android.view.WindowManager.LayoutParams.FLAG_SECURE

class MainActivity : AppCompatActivity() {
    private lateinit var appBarConfiguration: AppBarConfiguration
    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!
    private val credentialViewModel: CredentialViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Splash Screen config -- Happens before setContentView
        installSplashScreen()

        // Disable screenshots and screen recording (if disabled in settings or first launch)
        val allowScreenshots = PreferenceManager.getDefaultSharedPreferences(this).getBoolean(
            getString(R.string.pref_screenshots_key),
            false
        )

        if (!allowScreenshots) window.setFlags(FLAG_SECURE, FLAG_SECURE)

        // ViewBinding
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Nav controller config
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        binding.navView.setupWithNavController(navController)

        // Drawer config
        setSupportActionBar(binding.toolbar)
        appBarConfiguration = AppBarConfiguration(
            setOf(R.id.nav_home, R.id.nav_settings, R.id.nav_generator),
            binding.drawerLayout,
        )

        setupActionBarWithNavController(navController, appBarConfiguration)
    }

    override fun onStart() {
        super.onStart()

        // Biometric authentication can only happen after app is launched
        // If successful, read credentials from EncryptedFile and initialize viewModel
        Biometrics(this).canAuthenticate() {
            credentialViewModel.initialize(this)
        }
    }

    // Handle menu item selection
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return item.onNavDestinationSelected(navController) || super.onOptionsItemSelected(item)
    }

    // Handle back button navigation
    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    // Clear focus when touched outside (for EditText)
    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        if (ev?.action == MotionEvent.ACTION_DOWN && currentFocus != null) {
            if (currentFocus is TextInputEditText) {
                val tv = currentFocus as TextInputEditText
                tv.clearFocus()
                hideKeyboard(tv, this)
            }
        }

        return super.dispatchTouchEvent(ev)
    }

    // Companion object to hold constants
    companion object {
        const val TAG = "ONEPASS_LOGS"
    }
}