package com.anastasiu.staytfhome.ui.fragment

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.anastasiu.staytfhome.ui.fragment.SignupFragmentArgs
import com.anastasiu.staytfhome.ui.fragment.SignupFragmentDirections
import com.anastasiu.staytfhome.R
import com.anastasiu.staytfhome.data.auth.CredentialsAuthenticatorProvider
import com.anastasiu.staytfhome.data.forms.SignUpForm
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.fragment_signin.btSignUp
import kotlinx.android.synthetic.main.fragment_signin.etEmail
import kotlinx.android.synthetic.main.fragment_signin.etPassword
import kotlinx.android.synthetic.main.fragment_signup.*
import javax.inject.Inject

class SignupFragment : Fragment() {
    @Inject
    lateinit var credentialsAuthenticatorProvider: CredentialsAuthenticatorProvider

    private val safeArgs: SignupFragmentArgs by navArgs()

    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_signup, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btSignUp.setOnClickListener {
            var firstName = etFirstName.editText?.text.toString()
            var lastName = etLastName.editText?.text.toString()
            val email = etEmail.editText?.text.toString()
            val password = etPassword.editText?.text.toString()
            val passwordRepeat = etPasswordRepeat.editText?.text.toString()
            if (firstName.isEmpty()) {
                etFirstName.error = "Please enter your first name!"
                return@setOnClickListener
            } else {
                etFirstName.error = null
            }
            if (lastName.isEmpty()) {
                etLastName.error = "Please enter your last name!"
                return@setOnClickListener
            } else {
                etLastName.error = null
            }
            if (email.isEmpty()) {
                etEmail.error = "Please enter an email address!"
                return@setOnClickListener
            } else {
                etEmail.error = null
            }
            if (password.isEmpty()) {
                etPassword.error = "Please enter a password!"
                return@setOnClickListener
            } else {
                etPassword.error = null
            }
            if (passwordRepeat.isEmpty() || passwordRepeat != password) {
                etPasswordRepeat.error = "Entered passwords don't match!"
                return@setOnClickListener
            } else {
                etPasswordRepeat.error = null
            }
            btSignUp.error = null

            credentialsAuthenticatorProvider.signUp(
                SignUpForm(firstName, lastName, email, password, if (safeArgs.invitedBy != -1) safeArgs.invitedBy else null)
            )
                .subscribe({ response ->
                    if (response.isSuccessful) {
                        findNavController().navigate(SignupFragmentDirections.actionSignupFragmentToActivationRequiredFragment())
                    } else {
                        etEmail.error = "Email already exists!"
                        btSignUp.error = "Email already exists!"

                    }
                }, {
                    Toast.makeText(activity, "There was a problem reaching the server.", Toast.LENGTH_LONG).show()
                })
        }
    }
}
