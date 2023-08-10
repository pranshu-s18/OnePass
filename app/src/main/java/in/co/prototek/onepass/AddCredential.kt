package `in`.co.prototek.onepass

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import `in`.co.prototek.onepass.databinding.FragmentAddCredentialBinding
import `in`.co.prototek.onepass.utils.showKeyboard
import `in`.co.prototek.onepass.viewModel.CredentialViewModel

class AddCredential : Fragment() {
    private var _binding: FragmentAddCredentialBinding? = null
    private val binding get() = _binding!!
    private val credentialViewModel: CredentialViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Adding a listener to child fragment to get password from generator fragment
        childFragmentManager.setFragmentResultListener(
            "password",
            viewLifecycleOwner
        ) { _, bundle ->
            binding.passwordEditText.setText(bundle.getString("password"))
        }

        // View binding setup
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

        // Add new credential, Validation handled in viewModel
        binding.saveCredentials.setOnClickListener {
            credentialViewModel.addCredential(
                binding.serviceEditText.text.toString(),
                binding.usernameEditText.text.toString(),
                binding.passwordEditText.text.toString(),
                requireContext()
            )

            // Go back to home fragment
            findNavController().navigateUp()
        }

        // Generate a random password - Show generator fragment using childFragmentManager
        binding.generatePasswordBtn.setOnClickListener {
            val generator = Generator()
            generator.show(this.childFragmentManager, "Generator")
        }
    }

    // Clear listeners
    override fun onDestroy() {
        childFragmentManager.clearFragmentResultListener("password")
        super.onDestroy()
    }
}