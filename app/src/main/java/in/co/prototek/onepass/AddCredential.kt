package `in`.co.prototek.onepass

import `in`.co.prototek.onepass.databinding.FragmentAddCredentialBinding
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController

class AddCredential : Fragment() {
    private lateinit var username: String
    private lateinit var password: String

    private var _binding: FragmentAddCredentialBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let { bundle ->
            username = bundle.getString("username", "")
            password = bundle.getString("password", "")
        }

        requireActivity().onBackPressedDispatcher.addCallback(this) {
            findNavController().navigate(R.id.addCredential_Home)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddCredentialBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        if (username.isNotBlank()) binding.usernameEditText.setText(username)
        if (password.isNotBlank()) binding.passwordEditText.setText(password)

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
                resetFile(requireContext())
                encryptedFile.openFileOutput().use { output -> output.write(body.toByteArray()) }

                findNavController().navigate(R.id.addCredential_Home)
            } catch (e: Exception) {
                Log.d(MainActivity.TAG, "Unable to Save", e)
                Toast.makeText(requireContext(), "Unable to save Credentials", Toast.LENGTH_SHORT).show()
            }
        }

        binding.usernameGeneratorBtn.setOnClickListener {
            val bundle = bundleOf("username" to username, "password" to password, "isPassword" to false)
            findNavController().navigate(R.id.addCredential_Generator, bundle)
        }

        binding.passwordGeneratorBtn.setOnClickListener {
            val bundle = bundleOf("username" to username, "password" to password, "isPassword" to true)
            findNavController().navigate(R.id.addCredential_Generator, bundle)
        }
    }
}