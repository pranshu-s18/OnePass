package `in`.co.prototek.onepass

import android.content.ClipData
import android.content.ClipboardManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import `in`.co.prototek.onepass.databinding.FragmentGeneratorBinding
import `in`.co.prototek.onepass.utils.Constants
import java.security.SecureRandom

class Generator : Fragment() {
    private var _binding: FragmentGeneratorBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGeneratorBinding.inflate(inflater, container, false)
        return binding.root
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

        // Copy password to clipboard
        binding.copy.setOnClickListener {
            val clip = ClipData.newPlainText("password", binding.password.text)
            requireContext().getSystemService(ClipboardManager::class.java).setPrimaryClip(clip)
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
        if (allowedChar.isEmpty()) binding.password.text = ""

        val max = binding.passwordLength.value.toInt()
        if (allowedChar.isNotEmpty()) {
            val random =
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) SecureRandom.getInstanceStrong()
                else SecureRandom()

            val res = StringBuilder()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                random.ints(max.toLong(), 0, allowedChar.size)
                    .forEach { res.append(allowedChar[it]) }
            } else {
                for (i in 1..max) {
                    res.append(allowedChar[random.nextInt(allowedChar.size)])
                }
            }

            binding.password.text = res.toString()
        }
    }
}