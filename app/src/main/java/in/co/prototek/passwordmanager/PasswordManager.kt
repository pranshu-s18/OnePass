package `in`.co.prototek.passwordmanager

import `in`.co.prototek.passwordmanager.databinding.FragmentPasswordManagerBinding
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

    override fun onStart() {
        super.onStart()
        var errorOccurred = false
        val user = db.collection("users").document(auth.currentUser!!.uid)
        user.get().addOnSuccessListener { doc ->
            if (doc.exists()) {
                user.collection("passwords").get()
                    .addOnSuccessListener { passwords -> Log.d(MainActivity.TAG, passwords.documents.toString()) }
                    .addOnFailureListener { e ->
                        Log.w(MainActivity.TAG, "Error in fetching passwords", e)
                        errorOccurred = true
                    }
            } else
                user.set(hashMapOf("exists" to true))
                    .addOnSuccessListener { Log.d(MainActivity.TAG, "User added to DB") }
                    .addOnFailureListener { e ->
                        Log.w(MainActivity.TAG, "Error in adding user to DB", e)
                        errorOccurred = true
                    }
        }.addOnFailureListener { e ->
            Log.d(MainActivity.TAG, "DB Error", e)
            errorOccurred = true
        }

        if(errorOccurred) Toast.makeText(requireContext(), "Some Server Error Occurred...", Toast.LENGTH_SHORT).show()
    }
}