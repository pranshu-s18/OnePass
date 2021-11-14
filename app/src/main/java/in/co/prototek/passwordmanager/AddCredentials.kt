package `in`.co.prototek.passwordmanager

import `in`.co.prototek.passwordmanager.databinding.FragmentAddCredentialsBinding
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.io.File

class AddCredentials : Fragment() {
    private var _binding: FragmentAddCredentialsBinding? = null
    private val binding get() = _binding!!

    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        db = Firebase.firestore
        auth = Firebase.auth
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddCredentialsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.saveCredentials.setOnClickListener {
//            val userCredentials = db.collection("users").document(auth.currentUser!!.uid).collection("passwords")
            val storeFile = store(requireContext())
            if (!storeFile.exists()) storeFile.createNewFile()
            try {
                val encryptedFile = getEncryptedFile(requireContext())
                if (!storeFile.exists()) {
                    encryptedFile.openFileOutput().use { output ->
                        output.write(
                            getString(
                                R.string.record,
                                binding.serviceEditText.text,
                                binding.usernameEditText.text,
                                binding.passwordEditText.text
                            ).toByteArray()
                        )
                    }
                } else {
                    storeFile.createNewFile()
                }
            } catch (e: Exception) {
                Log.d(MainActivity.TAG, "Unable to save", e)
                Toast.makeText(requireContext(), "Unable to save Credentials", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }
}