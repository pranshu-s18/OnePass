package `in`.co.prototek.onepass

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import `in`.co.prototek.onepass.databinding.FragmentAddCredentialBinding
import `in`.co.prototek.onepass.utils.clearAllData
import `in`.co.prototek.onepass.utils.getEncryptedFile
import `in`.co.prototek.onepass.utils.readEncryptedFile
import `in`.co.prototek.onepass.utils.showKeyboard

class AddCredential : Fragment() {
    private var _binding: FragmentAddCredentialBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddCredentialBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // Auto focus on service name/URL field
        binding.serviceEditText.requestFocus()
        showKeyboard(binding.serviceEditText, requireContext())

        // Clear focus when user presses done
        binding.passwordEditText.setOnEditorActionListener { textView, action, _ ->
            if (action == EditorInfo.IME_ACTION_DONE) textView.clearFocus()
            return@setOnEditorActionListener false
        }

        // Encrypt and save credentials
        binding.saveCredentials.setOnClickListener {
            // Validate input
            if (binding.serviceEditText.text.isNullOrBlank() ||
                binding.usernameEditText.text.isNullOrBlank() ||
                binding.passwordEditText.text.isNullOrBlank()
            ) {
                Toast.makeText(
                    requireContext(),
                    "Please fill all the fields",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            // Update an encrypted file is not allowed
            // WORKAROUND: Delete the existing file and create a new one
            try {
                // Read existing credentials and append new credentials
                val allCredentials = readEncryptedFile(requireContext()).plus(
                    getString(
                        R.string.record,
                        binding.serviceEditText.text,
                        binding.usernameEditText.text,
                        binding.passwordEditText.text
                    )
                )

                // Delete existing file
                clearAllData(requireContext())

                // Create new file and write all credentials
                val encryptedFile = getEncryptedFile(requireContext())
                encryptedFile.openFileOutput()
                    .use { output -> output.write(allCredentials.toByteArray()) }

                // Go back to home
                findNavController().navigateUp()
            } catch (e: Exception) {
                Log.d(MainActivity.TAG, "Unable to Save Credentials", e)
                Toast.makeText(requireContext(), "Unable to save credentials", Toast.LENGTH_SHORT)
                    .show()
            }
        }

        // Adding a listener to child fragment to get password from generator fragment
        childFragmentManager.setFragmentResultListener(
            "password",
            viewLifecycleOwner
        ) { _, bundle ->
            binding.passwordEditText.setText(bundle.getString("password"))
        }

        // Generate a random password - Show generator fragment using childFragmentManager
        binding.generatePasswordBtn.setOnClickListener {
            val generator = Generator()
            generator.show(this.childFragmentManager, "Generator")
        }
    }
}