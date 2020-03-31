package com.anastasiu.staytfhome.ui.fragment

import android.app.Activity.RESULT_OK
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.view.*
import android.view.View.GONE
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.facebook.login.LoginManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.api.client.util.IOUtils
import com.anastasiu.staytfhome.R
import com.anastasiu.staytfhome.data.forms.UserForm
import com.anastasiu.staytfhome.data.model.User
import com.anastasiu.staytfhome.databinding.FragmentProfileManagerBinding
import com.anastasiu.staytfhome.ui.event.Event
import com.anastasiu.staytfhome.ui.viewmodel.*
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.fragment_profile_manager.*
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import javax.inject.Inject

class ProfileManagerFragment : Fragment() {
    private lateinit var binding: FragmentProfileManagerBinding

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    lateinit var loginViewModel: LoginViewModel

    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_profile_manager, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loginViewModel = ViewModelProviders.of(activity!!, viewModelFactory)
            .get(LoginViewModel::class.java)

        loginViewModel.user.observe(this, Observer<User> { user ->
            if (user != null) {
                loginViewModel.userForm.name = user.name
                binding.model = loginViewModel
                binding.lifecycleOwner = this

                val roles = user.roles.split(",")
                if (!roles.contains("usermanager")) {
                    binding.btUserDashboard.visibility = GONE
                }
                if (!roles.contains("admin")) {
                    binding.btAdminDashboard.visibility = GONE
                }

                Glide.with(this)
                    .load(user.avatarURL)
                    .override(100, 100)
                    .placeholder(R.drawable.ic_baseline_person_24)
                    .into(ivAvatar)
            }
        })

        loginViewModel.userSubmitFormEvent.observe(this, Observer<Event<UserForm>> { event ->
            event.getContentIfNotHandledOrReturnNull()?.let {
                Toast.makeText(
                    context,
                    "Profile has been successfully updated.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
        loginViewModel.signOutEvent.observe(this, Observer<Event<User>> { event ->
            event.getContentIfNotHandledOrReturnNull()?.let {
                LoginManager.getInstance().logOut()

                val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestEmail()
                    .requestServerAuthCode(getString(R.string.google_client_id))
                    .build()
                val googleSignInClient = GoogleSignIn.getClient(activity!!, gso)
                googleSignInClient.signOut()

                findNavController().navigate(ProfileManagerFragmentDirections.actionProfileManagerFragmentToAuthFlow())
            }
        })

        ivAvatar.setOnClickListener {
            val photoPickerIntent = Intent(Intent.ACTION_PICK)
            photoPickerIntent.type = "image/*"
            startActivityForResult(photoPickerIntent, RC_PICK_AVATAR)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_PICK_AVATAR) {
            if (resultCode == RESULT_OK) {
                val selectedFileUri: Uri = data!!.data!!
                val parcelFileDescriptor = context!!.contentResolver.openFileDescriptor(selectedFileUri, "r", null)

                parcelFileDescriptor?.let {
                    val inputStream = FileInputStream(parcelFileDescriptor.fileDescriptor)
                    val file = File(context!!.cacheDir, context!!.contentResolver.getFileName(selectedFileUri))
                    val outputStream = FileOutputStream(file)
                    IOUtils.copy(inputStream, outputStream)

                    loginViewModel.uploadAvatar(file)

                    Glide.with(this)
                        .load(file)
                        .override(100, 100)
                        .placeholder(R.drawable.ic_baseline_person_24)
                        .into(ivAvatar)
                }
            }
        }
    }

    companion object {
        val RC_PICK_AVATAR = 3
    }
}

fun ContentResolver.getFileName(fileUri: Uri): String {
    var name = ""
    val returnCursor = this.query(fileUri, null, null, null, null)
    if (returnCursor != null) {
        val nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        returnCursor.moveToFirst()
        name = returnCursor.getString(nameIndex)
        returnCursor.close()
    }

    return name
}
