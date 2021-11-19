package `in`.co.prototek.onepass

import `in`.co.prototek.onepass.databinding.FragmentGeneratorBinding
import android.os.Bundle
import android.text.InputFilter
import android.text.InputFilter.LengthFilter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController

class Generator : Fragment() {
    private lateinit var username: String
    private lateinit var password: String
    private var isPassword = false

    private var allowedChar = mutableListOf<Char>()
    private var _binding: FragmentGeneratorBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let { bundle ->
            username = bundle.getString("username", "")
            password = bundle.getString("password", "")
            isPassword = bundle.getBoolean("isPassword")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGeneratorBinding.inflate(inflater, container, false)
        if (isPassword) binding.symbols.visibility = View.VISIBLE
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.charEditText.filters = arrayOf<InputFilter>(LengthFilter(2))
        binding.gen.setOnClickListener {
            allowedChar.clear()
            if (binding.lowercase.isChecked) allowedChar.addAll('a'..'z')
            if (binding.uppercase.isChecked) allowedChar.addAll('A'..'Z')
            if (binding.numbers.isChecked) allowedChar.addAll('0'..'9')
            if (binding.symbols.isChecked) allowedChar.addAll(listOf('@', '$', '!', '#', '%', '&'))

            val max = binding.charEditText.text.toString().toInt()
            if (allowedChar.isNotEmpty()) {
                if (isPassword) password = (1..max).map { allowedChar.random() }.joinToString("")
                else username = (1..max).map { allowedChar.random() }.joinToString("")

                val bundle = bundleOf("username" to username, "password" to password)
                findNavController().navigate(R.id.generator_AddCredential, bundle)
            }
        }
    }
}