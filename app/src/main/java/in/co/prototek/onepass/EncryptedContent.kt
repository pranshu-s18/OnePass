package `in`.co.prototek.onepass

import `in`.co.prototek.onepass.databinding.FragmentEncryptedContentBinding
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import java.io.BufferedReader
import java.io.File
import java.io.FileReader

class EncryptedContent : Fragment() {
    private lateinit var storeFile: File
    private var _binding: FragmentEncryptedContentBinding? = null
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
        _binding = FragmentEncryptedContentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.textview.text = if(storeFile.exists()) {
            BufferedReader(FileReader(storeFile)).readText()
        } else "No Credentials found"
    }
}