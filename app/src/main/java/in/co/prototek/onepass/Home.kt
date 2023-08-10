package `in`.co.prototek.onepass

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import `in`.co.prototek.onepass.data.Credential
import `in`.co.prototek.onepass.databinding.FragmentHomeBinding
import `in`.co.prototek.onepass.recyclerView.CredentialsRecyclerViewAdapter
import `in`.co.prototek.onepass.viewModel.CredentialViewModel

class Home : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val credentialViewModel: CredentialViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        // Set up RecyclerView
        binding.passwordView.layoutManager = LinearLayoutManager(requireContext())
        binding.passwordView.setHasFixedSize(true)

        // Implementation of CredentialsRecyclerViewAdapter.RecyclerViewOperations interface
        val operations = object : CredentialsRecyclerViewAdapter.RecyclerViewOperations {
            // If an item is clicked, set it as selected and navigate to EditCredentialFragment
            override fun onClick(credential: Credential) {
                credentialViewModel.setSelectedCredential(credential)
                findNavController().navigate(R.id.nav_action_home_to_edit_credential)
            }

            // Delete credential (handled in viewModel)
            override fun deleteCredential(credential: Credential) {
                credentialViewModel.deleteCredential(credential, requireContext())
            }
        }

        // Update RecyclerView when data changes
        credentialViewModel.credentials.observe(viewLifecycleOwner) {
            binding.empty.visibility = if (it.isEmpty()) View.VISIBLE else View.GONE
            binding.passwordView.adapter = CredentialsRecyclerViewAdapter(it, operations)
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.add.setOnClickListener {
            findNavController().navigate(R.id.nav_action_home_to_add_credential)
        }

        // Hide add button when scrolled to the bottom, show otherwise
        binding.passwordView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (!recyclerView.canScrollVertically(1)) binding.add.hide()
                else binding.add.show()
            }
        })
    }

    // Clear listeners
    override fun onDestroyView() {
        binding.passwordView.clearOnScrollListeners()
        super.onDestroyView()
    }
}