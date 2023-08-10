package `in`.co.prototek.onepass

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.ClipData
import android.content.ClipboardManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import `in`.co.prototek.onepass.databinding.FragmentGeneratorBinding
import `in`.co.prototek.onepass.utils.Constants
import java.security.SecureRandom

class Generator : DialogFragment() {
    private var _binding: FragmentGeneratorBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        if (dialog == null) _binding = FragmentGeneratorBinding.inflate(inflater, container, false)
        return binding.root
    }

    // For DialogFragment, onViewCreated is called only if onCreateView does not return null
    @SuppressLint("UseGetLayoutInflater", "SetTextI18n")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = FragmentGeneratorBinding.inflate(LayoutInflater.from(requireContext()))

        // If dialog, change copy button text to OK
        binding.copy.text = "OK"

        // Return dialog
        return AlertDialog.Builder(requireContext())
            .setView(binding.root)
            .create()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // Generate password using default configuration
        generate()

        // Generate password on configuration change
        binding.passwordLength.addOnChangeListener { _, _, _ -> generate() }
        binding.lowercase.setOnClickListener { generate() }
        binding.uppercase.setOnClickListener { generate() }
        binding.numbers.setOnClickListener { generate() }
        binding.symbols.setOnClickListener { generate() }

        // Regenerate password on click
        binding.regenerate.setOnClickListener { generate() }

        // If dialog, send password to parent fragment and dismiss
        // Otherwise, copy password to clipboard
        binding.copy.setOnClickListener {
            if (dialog != null) {
                setFragmentResult(
                    "password",
                    Bundle().apply { putString("password", binding.password.text.toString()) },
                )

                dismiss()
            } else {
                val clip = ClipData.newPlainText("password", binding.password.text)
                requireContext().getSystemService(ClipboardManager::class.java).setPrimaryClip(clip)
            }
        }
    }

    // Clear listeners
    override fun onDestroyView() {
        binding.passwordLength.clearOnChangeListeners()
        super.onDestroyView()
    }

    // Generate password based on configuration and update UI
    // Uses SecureRandom (Java) for generating random numbers
    private fun generate() {
        val allowedChar = mutableListOf<Char>()
        if (binding.lowercase.isChecked) allowedChar.addAll(Constants.lowercaseCharacters)
        if (binding.uppercase.isChecked) allowedChar.addAll(Constants.uppercaseCharacters)
        if (binding.numbers.isChecked) allowedChar.addAll(Constants.numbers)
        if (binding.symbols.isChecked) allowedChar.addAll(Constants.symbols)

        // If no character is selected, clear password and return
        if (allowedChar.isEmpty()) {
            binding.password.text = ""
            return
        }

        // Required length of password
        val max = binding.passwordLength.value.toInt()

        // Random number generator using SecureRandom
        // If API Level >= 26, use SecureRandom.getInstanceStrong() -- ensures strong algorithm
        val random =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) SecureRandom.getInstanceStrong()
            else SecureRandom()

        // Generate password using characters at random indices in allowedChar
        val res = StringBuilder()
        for (i in 1..max) {
            res.append(allowedChar[random.nextInt(allowedChar.size)])
        }

        binding.password.text = res.toString()
    }
}