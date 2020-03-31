package com.anastasiu.staytfhome.ui.fragment

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.auth.api.signin.GoogleSignInOptions.Builder
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeTokenRequest
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.jackson2.JacksonFactory
import com.anastasiu.staytfhome.R
import com.anastasiu.staytfhome.data.auth.CredentialsAuthenticatorProvider
import com.anastasiu.staytfhome.data.forms.SignInForm
import com.anastasiu.staytfhome.ui.viewmodel.LoginViewModel
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.fragment_signin.*
import javax.inject.Inject

class SigninFragment : Fragment() {
    @Inject
    lateinit var credentialsAuthenticatorProvider: CredentialsAuthenticatorProvider

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    lateinit var loginViewModel: LoginViewModel

    private var callbackManager: CallbackManager? = null
    private var mGoogleSignInClient: GoogleSignInClient? = null

    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        callbackManager = CallbackManager.Factory.create()

        LoginManager.getInstance().registerCallback(callbackManager,
            object : FacebookCallback<LoginResult> {
                @SuppressLint("CheckResult")
                override fun onSuccess(loginResult: LoginResult) {
                    Log.d(TAG, "Facebook token: " + loginResult.accessToken.token)
                    credentialsAuthenticatorProvider.facebookAuthenticateToken(loginResult.accessToken)
                        .subscribe({ response ->
                            if (response.isSuccessful) {
                                onLoginSuccess()
                            }
                        }, {
                            Toast.makeText(activity, "There was a problem reaching the server.", Toast.LENGTH_LONG).show()
                        })
                }

                override fun onCancel() {
                    Log.d(TAG, "Facebook onCancel.")
                }

                override fun onError(error: FacebookException) {
                    Log.d(TAG, "Facebook onError.")
                }
            })
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_signin, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fbLoginButton.fragment = this
        fbLoginButton.setPermissions(listOf(EMAIL, PUBLIC_PROFILE))

        val gso = Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .requestServerAuthCode(getString(R.string.google_client_id))
            .build()
        mGoogleSignInClient = GoogleSignIn.getClient(activity!!, gso)
        googleLoginButton.setOnClickListener {
            val signInIntent: Intent = mGoogleSignInClient!!.signInIntent
            startActivityForResult(signInIntent, RC_GOOGLE_SIGN_IN)
        }

        btSignIn.setOnClickListener {
            val email = etEmail.editText?.text.toString()
            val password = etPassword.editText?.text.toString()
            if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
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

            credentialsAuthenticatorProvider.signIn(
                SignInForm(email, password, true))
                .subscribe({ response ->
                    if (response.isSuccessful || response.code() == 403) {
                        onLoginSuccess()
                    } else {
                        when (response.errorBody()?.string()) {
                            "INVALID_CREDENTIALS" -> {
                                etEmail.error = "Invalid credentials!"
                                etPassword.error = "Invalid credentials!"
                                btSignIn.error = "Invalid credentials!"
                            }
                            "USER_NOT_ACTIVATED" -> {
                                etEmail.error = "The account is not activated!"
                                btSignIn.error = "User not activated!"
                            }
                            "USER_BLOCKED" -> {
                                etEmail.error = "The account is temporary blocked! Please contact the administrator at tg.anastasiu@gmail.com."
                                btSignIn.error = "Account is blocked!"
                            }
                        }
                    }
                }, {
                    Toast.makeText(activity, "There was a problem reaching the server.", Toast.LENGTH_LONG).show()
                })
        }
        btSignUp.setOnClickListener {
            findNavController().navigate(SigninFragmentDirections.actionSigninFragmentToSignupFragment())
        }
    }

    private fun onLoginSuccess() {
        loginViewModel = ViewModelProviders.of(activity!!, viewModelFactory)
            .get(LoginViewModel::class.java)
            .apply { fetchUser() }
        findNavController().navigate(SigninFragmentDirections.actionSigninFragmentPop())
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...)
        if (requestCode == RC_GOOGLE_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)

            handleGoogleSignInResult(task)
        } else {
            callbackManager?.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun handleGoogleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account: GoogleSignInAccount? = completedTask.getResult(ApiException::class.java)

            class ExchangeAccessTokenTask : AsyncTask<Void, Void, String>() {
                override fun doInBackground(vararg params: Void?): String {
                    val tokenResponse: GoogleTokenResponse = GoogleAuthorizationCodeTokenRequest(
                        NetHttpTransport(),
                        JacksonFactory.getDefaultInstance(),
                        "https://oauth2.googleapis.com/token",
                        getString(R.string.google_client_id),
                        getString(R.string.google_client_secret),
                        account!!.serverAuthCode,
                        ""
                    )
                        .execute()
                    return tokenResponse.accessToken
                }

                @SuppressLint("CheckResult")
                override fun onPostExecute(accessToken: String) {
                    credentialsAuthenticatorProvider.googleAuthenticateToken(accessToken)
                        .subscribe({ response ->
                            if (response.isSuccessful) {
                                onLoginSuccess()
                            }
                        }, {
                            Toast.makeText(activity, "There was a problem reaching the server.", Toast.LENGTH_LONG).show()
                        })
                }
            }
            ExchangeAccessTokenTask().execute()
        } catch (e: ApiException) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w(TAG, e.toString())
        }
    }

    companion object {
        private val TAG: String = "SigninFragment"
        private val EMAIL = "email"
        private val PUBLIC_PROFILE = "public_profile"

        val RC_GOOGLE_SIGN_IN: Int = 1
    }
}
