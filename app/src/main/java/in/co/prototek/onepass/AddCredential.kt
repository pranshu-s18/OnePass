package `in`.co.prototek.onepass

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
        binding.serviceEditText.requestFocus()
        showKeyboard(binding.serviceEditText, requireContext())

        binding.saveCredentials.setOnClickListener {
            try {
                val body = readEncryptedFile(requireContext()).plus(
                    getString(
                        R.string.record,
                        binding.serviceEditText.text,
                        binding.usernameEditText.text,
                        binding.passwordEditText.text
                    )
                )

                val encryptedFile = getEncryptedFile(requireContext())
                clearAllData(requireContext())
                encryptedFile.openFileOutput().use { output -> output.write(body.toByteArray()) }

                findNavController().navigateUp()
            } catch (e: Exception) {
                Log.d(MainActivity.TAG, "Unable to Save", e)
                Toast.makeText(requireContext(), "Unable to save Credentials", Toast.LENGTH_SHORT).show()
            }
        }
    }
}