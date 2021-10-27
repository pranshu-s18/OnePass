package `in`.co.prototek.passwordmanager

import `in`.co.prototek.passwordmanager.databinding.FragmentLoginBinding
import android.app.Activity
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.delay

class Login : Fragment() {
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private lateinit var client: GoogleSignInClient
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        client = GoogleSignIn.getClient(requireContext(), gso)
        auth = Firebase.auth
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        if (auth.currentUser != null) findNavController().navigate(R.id.fingerprint)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        when (resources.configuration.uiMode.and(Configuration.UI_MODE_NIGHT_MASK)) {
            Configuration.UI_MODE_NIGHT_YES -> binding.signIn.setStyle(SignInButton.SIZE_STANDARD, SignInButton.COLOR_LIGHT)
            Configuration.UI_MODE_NIGHT_NO -> binding.signIn.setStyle(SignInButton.SIZE_WIDE, SignInButton.COLOR_DARK)
            Configuration.UI_MODE_NIGHT_UNDEFINED -> binding.signIn.setStyle(SignInButton.SIZE_STANDARD, SignInButton.COLOR_AUTO)
        }

        binding.signIn.setOnClickListener { resultLauncher.launch(client.signInIntent) }
    }

    private var resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { res ->
            if (res.resultCode == Activity.RESULT_OK) {
                binding.progressBar.visibility = View.VISIBLE
                val task = GoogleSignIn.getSignedInAccountFromIntent(res.data)
                try {
                    val account = task.getResult(ApiException::class.java)!!
                    firebaseAuth(account.idToken!!)
                } catch (e: ApiException) {
                    binding.progressBar.visibility = View.INVISIBLE
                    Log.d(MainActivity.TAG, "Google Sign In EXCEPTION", e)
                    Toast.makeText(requireContext(), "Google Sign In Failed", Toast.LENGTH_SHORT).show()
                }
            } else Log.d(MainActivity.TAG, "Activity EXCEPTION, Result Code: ${res.resultCode}")
        }

    private fun firebaseAuth(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential).addOnCompleteListener(requireActivity()) { task ->
            binding.progressBar.visibility = View.INVISIBLE
            if (task.isSuccessful) findNavController().navigate(R.id.fingerprint)
            else {
                Log.d(MainActivity.TAG, "Sign in with Credential Failed", task.exception)
                Toast.makeText(requireContext(), "Google Sign In Failed", Toast.LENGTH_SHORT).show()
            }
        }
    }
}