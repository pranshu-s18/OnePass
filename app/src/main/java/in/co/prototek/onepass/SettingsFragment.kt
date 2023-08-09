package `in`.co.prototek.onepass

import android.os.Bundle
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreferenceCompat
import android.view.WindowManager.LayoutParams.FLAG_SECURE
import `in`.co.prototek.onepass.utils.clearAllData

class SettingsFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)
    }

    // Handle preference clicks
    override fun onPreferenceTreeClick(preference: Preference): Boolean {
        when (preference.key) {
            // Toggle screenshots and screen recording
            getString(R.string.pref_screenshots_key) -> {
                val allow = (preference as SwitchPreferenceCompat).isChecked
                val window = requireActivity().window
                if (allow) window.clearFlags(FLAG_SECURE)
                else window.setFlags(FLAG_SECURE, FLAG_SECURE)
            }

            // Erase all credentials
            getString(R.string.pref_clearData_key) -> clearAllData(requireContext())
        }

        return super.onPreferenceTreeClick(preference)
    }
}