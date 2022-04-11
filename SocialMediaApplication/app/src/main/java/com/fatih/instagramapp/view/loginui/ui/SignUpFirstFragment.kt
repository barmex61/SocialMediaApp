package com.fatih.instagramapp.view.loginui.ui

import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.fatih.instagramapp.R
import com.fatih.instagramapp.databinding.FragmentSignUpFirstBinding
import com.fatih.instagramapp.util.Resource
import com.fatih.instagramapp.util.Status
import com.fatih.instagramapp.view.loginui.viewmodel.SignUpFirstFragmentViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.Timestamp
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SignUpFirstFragment @Inject constructor(private val auth:FirebaseAuth):Fragment() {

    private lateinit var binding:FragmentSignUpFirstBinding
    private lateinit var callbackManager: CallbackManager
    private lateinit var viewModel: SignUpFirstFragmentViewModel
    private lateinit var googleSignInClient: GoogleSignInClient

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding=DataBindingUtil.inflate(inflater,R.layout.fragment_sign_up_first,container,false)
        doInit()
        return binding.root
    }

    private fun doInit(){
        viewModel=ViewModelProvider(this)[SignUpFirstFragmentViewModel::class.java]
        setOnClickListeners()
        setGoogleOptions()
        setOnClickListeners()
        setFacebookLogIn()
    }

    private fun setOnClickListeners(){
        binding.signUpEmailText.setOnClickListener {
            findNavController().navigate(R.id.action_signUpFirstFragment_to_signUpSecondFragment)
        }
        binding.logInText.setOnClickListener {
            findNavController().navigate(R.id.action_signUpFirstFragment_to_signInFragment)
        }
        binding.facebookLinearLayout.setOnClickListener {
            binding.facebookLogInButton.performClick()
        }
        binding.googleLinearLayout.setOnClickListener {
            viewModel.resetControlMessage()
            signGoogle()
        }
    }

    private fun setGoogleOptions(){
        val gsc= GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestIdToken(
            "262915445007-07q52j6b9ck1ckm2h3j2qplsj9tpc0nl.apps.googleusercontent.com").requestEmail().build()
        googleSignInClient= GoogleSignIn.getClient(requireActivity(),gsc)
    }

    private fun signGoogle(){

        val intent=googleSignInClient.signInIntent
        startActivityForResult(intent, Resource.GOOGLE_SIGN_IN)

    }

    private fun setFacebookLogIn(){
        val buttonFacebookLogin=binding.facebookLogInButton
        callbackManager = CallbackManager.Factory.create()
        LoginManager.getInstance().logOut()
        buttonFacebookLogin.setPermissions("email", "public_profile")
        buttonFacebookLogin.fragment = this
        buttonFacebookLogin.registerCallback(callbackManager, object :
            FacebookCallback<LoginResult> {
            override fun onSuccess(result: LoginResult) {
                Log.d(ContentValues.TAG, "facebook:onSuccess:$result")
                handleFacebookAccessToken(result.accessToken)
            }

            override fun onCancel() {
                Log.d(ContentValues.TAG, "facebook:onCancel")
            }

            override fun onError(error: FacebookException) {
                Log.d(ContentValues.TAG, "facebook:onError", error)

            }
        })


    }

    private fun handleFacebookAccessToken(token: AccessToken) {

        Log.d(ContentValues.TAG, "handleFacebookAccessToken:$token")

        val credential = FacebookAuthProvider.getCredential(token.token)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(ContentValues.TAG, "signInWithCredential:success")
                    Toast.makeText(requireContext(),"Success", Toast.LENGTH_SHORT).show()

                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(ContentValues.TAG, "signInWithCredential:failure", task.exception)
                    Toast.makeText(requireContext(), "Authentication failed.",
                        Toast.LENGTH_SHORT).show()

                }
            }
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        callbackManager.onActivityResult(requestCode, resultCode, data)

        if(requestCode== Resource.GOOGLE_SIGN_IN){
            val task= GoogleSignIn.getSignedInAccountFromIntent(data)
            try {

                val account=task.getResult(ApiException::class.java)
                val credential= GoogleAuthProvider.getCredential(account.idToken,null)
                auth.signInWithCredential(credential).addOnCompleteListener {
                    if(task.isSuccessful){
                        val user=auth.currentUser
                        val date= Timestamp.now().toDate().time
                        viewModel.addGoogleUserIntoDatabase(user,account,date)
                        observeLiveData()
                    }else{
                        Toast.makeText(requireContext(),"Request failed !", Toast.LENGTH_SHORT).show()
                    }
                }
            }catch (e:Exception){
                Toast.makeText(requireContext(),e.message, Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun observeLiveData(){
        var justOnce=0
        viewModel.controlMessage.observe(viewLifecycleOwner){
            println(it.status)
            when(it.status){
                Status.ERROR->{if(justOnce==0){
                    Toast.makeText(requireContext(),it.message, Toast.LENGTH_SHORT).show()
                    justOnce++ }
                }
                Status.LOADING->{ }
                Status.SUCCESS->{
                    Toast.makeText(requireContext(),"Success", Toast.LENGTH_SHORT).show()}

            }
        }

    }
}