package `in`.co.prototek.onepass

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import `in`.co.prototek.onepass.databinding.FragmentEditCredentialBinding
import `in`.co.prototek.onepass.viewModel.CredentialViewModel

class EditCredential : Fragment() {
    private var _binding: FragmentEditCredentialBinding? = null
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
            binding.container.passwordEditText.setText(bundle.getString("password"))
        }

        _binding = FragmentEditCredentialBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // Get selected credential from viewModel
        credentialViewModel.selectedCredential.observe(viewLifecycleOwner) {
            binding.container.serviceEditText.setText(it?.service)
            binding.container.usernameEditText.setText(it?.username)
            binding.container.passwordEditText.setText(it?.password)
        }

        // Update credential, Validation handled in viewModel
        binding.container.saveCredentials.setOnClickListener {
            credentialViewModel.editCredential(
                binding.container.serviceEditText.text.toString(),
                binding.container.usernameEditText.text.toString(),
                binding.container.passwordEditText.text.toString(),
                requireContext()
            )

            // Go back to home fragment
            findNavController().navigateUp()
        }

        // Generate a random password - Show generator fragment using childFragmentManager
        binding.container.generatePasswordBtn.setOnClickListener {
            val generator = Generator()
            generator.show(this.childFragmentManager, "Generator")
        }
    }
}