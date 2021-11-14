package `in`.co.prototek.passwordmanager

import `in`.co.prototek.passwordmanager.databinding.FragmentPasswordManagerBinding
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class PasswordManager : Fragment() {
    private var _binding: FragmentPasswordManagerBinding? = null
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
        _binding = FragmentPasswordManagerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.add.setOnClickListener { findNavController().navigate(R.id.addCredentials) }
        val encryptedFile = getEncryptedFile(requireContext())
        if (store(requireContext()).exists()) {
            try {
                encryptedFile.openFileInput().use { input ->
                    Log.d(
                        MainActivity.TAG,
                        String(input.readBytes(), Charsets.UTF_8)
                    )
                }
            } catch (e: Exception) {
                Log.e(MainActivity.TAG, "Decrypt Error", e)
                Toast.makeText(requireContext(), "Unable to decrypt", Toast.LENGTH_SHORT).show()
            }
        } else {
            var errorOccurred = false
            val user = db.collection("users").document(auth.currentUser!!.uid)
            user.get().addOnSuccessListener { doc ->
                if (doc.exists()) {
                    user.collection("passwords").get().addOnSuccessListener { pswd ->
                        Log.d(
                            MainActivity.TAG,
                            pswd.documents.toString()
                        )
                    }.addOnFailureListener { e ->
                        Log.w(MainActivity.TAG, "Error in fetching from Firebase", e)
                        errorOccurred = true
                    }
                } else {
                    user.set(hashMapOf("exists" to true))
                        .addOnSuccessListener { Log.d(MainActivity.TAG, "User added to Firebase") }
                        .addOnFailureListener { e ->
                            Log.w(MainActivity.TAG, "Error adding user to Firebase", e)
                            errorOccurred = true
                        }
                }
            }.addOnFailureListener { e ->
                Log.w(MainActivity.TAG, "DB Error", e)
                errorOccurred = true
            }

            if (errorOccurred) Toast.makeText(
                requireContext(),
                "Some server error occurred...",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}