package mosh.com.assignment.ui

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.GoogleAuthProvider
import mosh.com.assignment.ExtensionsUtils.Companion.toast
import mosh.com.assignment.R
import mosh.com.assignment.adapters.CategoriesAdapter
import mosh.com.assignment.databinding.FragmentHomeBinding
import mosh.com.assignment.enum.Categories

class FragmentHome : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: SharedViewModel
    private lateinit var mGoogleSignInClient: GoogleSignInClient
    private lateinit var resultLauncher: ActivityResultLauncher<Intent>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(requireActivity())[SharedViewModel::class.java]

//  Getting the api key from the metaData
        viewModel.apiKey = requireContext().applicationContext.packageManager
            .getApplicationInfo(
                requireContext()
                    .applicationContext.packageName, PackageManager.GET_META_DATA
            )
            .metaData["ApiKey"] as String?

        configureSignInSettings()

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        updateSignInButton()

        binding.categoriesRecyclerView.adapter = CategoriesAdapter {
            if (isConnectedInternet()) {
                if (it == Categories.FAVORITES && viewModel.auth.currentUser == null)
                    toast(
                        getString(R.string.please_sign_in_to_watch_favorites),
                        Toast.LENGTH_LONG
                    )
                else {
                    viewModel.chosenCategory = it
                    findNavController().navigate(R.id.navigation_to_articles)
                }
            }else showNoInternetDialog()
        }

        binding.categoriesRecyclerView.layoutManager =
            GridLayoutManager(requireContext(), 2)

        binding.signInButton.setOnClickListener {
            if (isConnectedInternet())
                resultLauncher.launch(Intent(mGoogleSignInClient.signInIntent))
            else showNoInternetDialog()
        }
    }

    /**
     * Changes the visibility of google sign in button/the 'is login' text
     */
    private fun updateSignInButton() {
        if (viewModel.auth.currentUser != null) {
            binding.signInButton.visibility = View.GONE
            binding.loggedinTextView.visibility = View.VISIBLE
        }
    }

    /**
     * Signing in to firebase
      */
    private fun fireBaseAuthWithGoogle(idToken: String?) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        viewModel.auth.signInWithCredential(credential)
            .addOnCompleteListener {
                var message = getString(R.string.login_failed)
                if (it.isSuccessful) {
                    message = getString(R.string.login_successfully)
                    updateSignInButton()
                }
                toast(message)
            }
    }

    /**
     * Check if phone internet is turned on, if not, will return false
     */
    private fun isConnectedInternet(): Boolean {
        val cm =
            requireContext().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val capabilities = cm.getNetworkCapabilities(cm.activeNetwork)
        return capabilities != null &&
            capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

    /**
     * Shows no internet dialog and send to internet setting if user clicks the positive button
     */
    private fun showNoInternetDialog(){
        AlertDialog.Builder(requireContext())
            .setMessage(R.string.no_internet_dialog)
            .setPositiveButton(R.string.go_to_settings) { _, _ ->
                startActivity(Intent(Settings.ACTION_WIRELESS_SETTINGS))
            }
            .setNegativeButton(R.string.cancel) { _, _ -> }
            .show()
    }

//  Configure sign in settings
    private fun configureSignInSettings() {
        mGoogleSignInClient =
            GoogleSignIn.getClient(
                requireContext(),
                GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(getString(R.string.server_client_id))
                    .requestEmail()
                    .build()
            )

        resultLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult(),
        ) {
            if (it.resultCode == Activity.RESULT_OK) {
                val intent = it.data
                val task = GoogleSignIn.getSignedInAccountFromIntent(intent)
                try {
                    val account = task.result
                    fireBaseAuthWithGoogle(account.idToken)
                } catch (e: ApiException) {
                    toast(
                        getString(R.string.server_error_text),
                        Toast.LENGTH_LONG
                    )
                }
            }
        }
    }
}