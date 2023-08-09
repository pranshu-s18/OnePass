package `in`.co.prototek.onepass

import `in`.co.prototek.onepass.databinding.FragmentHomeBinding
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import `in`.co.prototek.onepass.data.Credential
import `in`.co.prototek.onepass.utils.readEncryptedFile
import `in`.co.prototek.onepass.utils.store
import java.io.File

class Home : Fragment() {
    private lateinit var storeFile: File
    private var records = mutableListOf<Credential>()
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        storeFile = store(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        binding.passwordView.layoutManager = LinearLayoutManager(requireContext())
        binding.passwordView.setHasFixedSize(true)
        binding.passwordView.adapter = RecyclerViewAdapter(records as ArrayList<Credential>)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.add.setOnClickListener {
            findNavController().navigate(R.id.nav_action_home_to_add_credential)
        }
    }

    override fun onStart() {
        super.onStart()
        if (storeFile.exists()) {
            val data = readEncryptedFile(requireContext())
            records.clear()
            data.lines().forEach { record ->
                val cred = record.split(',').map { str -> str.filterNot { it.isWhitespace() } }
                if (record.isNotEmpty()) records.add(Credential(cred[0], cred[1], cred[2]))
            }
        }

        binding.empty.visibility =
            if (binding.passwordView.adapter!!.itemCount == 0) View.VISIBLE else View.GONE
    }
}