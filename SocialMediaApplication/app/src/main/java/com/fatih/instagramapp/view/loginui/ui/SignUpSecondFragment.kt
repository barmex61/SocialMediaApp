package com.fatih.instagramapp.view.loginui.ui

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.fatih.instagramapp.R
import com.fatih.instagramapp.databinding.FragmentSignUpSecondBinding
import com.fatih.instagramapp.util.Status
import com.fatih.instagramapp.view.loginui.viewmodel.SignUpFragmentViewModel
import com.fatih.instagramapp.view.mainui.MainActivity
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SignUpSecondFragment @Inject constructor(private val auth:FirebaseAuth):Fragment() {

    private lateinit var binding:FragmentSignUpSecondBinding
    private lateinit var viewModel: SignUpFragmentViewModel
    private lateinit var activityEntrance: EntranceActivity
    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>
    private lateinit var permissionLauncher: ActivityResultLauncher<String>
    private var selectedUri:Uri?=null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding=DataBindingUtil.inflate(inflater,R.layout.fragment_sign_up_second,container,false)
        doInit()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activityEntrance=requireActivity() as EntranceActivity
    }

    private fun doInit(){
        registerLauncher()
        viewModel=ViewModelProvider(this)[SignUpFragmentViewModel::class.java]
        setOnClickListeners()
        binding.emailText.performClick()
    }

    private fun observeLiveData(){
        var justOnce=0
        viewModel.controlMessage.observe(viewLifecycleOwner){
            when(it.status){
                Status.ERROR->{
                    if(justOnce==0){
                        Toast.makeText(requireContext(),it.message,Toast.LENGTH_SHORT).show()
                        activityEntrance.activityEntranceBinding.isLoading=false
                        justOnce++ }
                   }
                Status.LOADING->{
                    println("loading")
                    activityEntrance.activityEntranceBinding.isLoading=true}
                Status.SUCCESS->{
                    println("success")
                    activityEntrance.activityEntranceBinding.isLoading=false
                    val intent=Intent(requireActivity(),MainActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    startActivity(intent)
                }
            }
        }
    }

    private fun setOnClickListeners(){

        binding.logInText.setOnClickListener {
            findNavController().navigate(R.id.action_signUpSecondFragment_to_signInFragment)
        }
        binding.cameraImage.setOnClickListener {
            checkPermissions(it)
        }
        binding.signUpLayout.setOnClickListener {
            val email=binding.phoneEditText.text.toString()
            val password=binding.passwordText.text.toString()
            val name=binding.nameText.text.toString()
            val username=binding.userNameText.text.toString()
            val date= Timestamp.now().toDate().time
            viewModel.signUp(email,password,name,username,date,selectedUri,auth)
            observeLiveData()
        }
        binding.phoneText.setOnClickListener {
          /*  binding.tr90.visibility=View.VISIBLE
            binding.dividerPhone.visibility=View.VISIBLE
            binding.phoneEditText.hint="Phone"
            binding.phoneText.setTextColor(ContextCompat.getColor(requireContext(),R.color.black))
            binding.phoneDivider.setBackgroundColor(ContextCompat.getColor(requireContext(),R.color.black))
            binding.emailText.setTextColor(ContextCompat.getColor(requireContext(),R.color.unselected))
            binding.emailDivider.setBackgroundColor(ContextCompat.getColor(requireContext(),R.color.unselected)) */
            Toast.makeText(requireContext(),"Later",Toast.LENGTH_SHORT).show()
        }
        binding.emailText.setOnClickListener {
            binding.tr90.visibility=View.GONE
            binding.dividerPhone.visibility=View.GONE
            binding.phoneEditText.hint = "Email"
            binding.phoneText.setTextColor(ContextCompat.getColor(requireContext(),R.color.unselected))
            binding.phoneDivider.setBackgroundColor(ContextCompat.getColor(requireContext(),R.color.unselected))
            binding.emailText.setTextColor(ContextCompat.getColor(requireContext(),R.color.black))
            binding.emailDivider.setBackgroundColor(ContextCompat.getColor(requireContext(),R.color.black))
        }

    }
    private fun checkPermissions(view:View){
        if(ContextCompat.checkSelfPermission(requireContext(),android.Manifest.permission.READ_EXTERNAL_STORAGE)!=PackageManager.PERMISSION_GRANTED){
            if(ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(),android.Manifest.permission.READ_EXTERNAL_STORAGE)){
                Snackbar.make(view,"Need Permission",Snackbar.LENGTH_SHORT).setAction("Give Permission") {
                    permissionLauncher.launch(android.Manifest.permission.READ_EXTERNAL_STORAGE)
                }.show()
            }else{
                permissionLauncher.launch(android.Manifest.permission.READ_EXTERNAL_STORAGE)
            }
        }else{
            val intentToGallery=Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            activityResultLauncher.launch(intentToGallery)
        }
    }

    private fun registerLauncher(){
        permissionLauncher=registerForActivityResult(ActivityResultContracts.RequestPermission()){
            if(it){
                val intentToGallery=Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                activityResultLauncher.launch(intentToGallery)
            }
            Toast.makeText(requireContext(),"Need Permission",Toast.LENGTH_SHORT).show()
        }
        activityResultLauncher=registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
            if(it.resultCode== Activity.RESULT_OK){
                it.data?.data?.let { uri->
                    selectedUri= uri
                    binding.fragmentSignUpProfileImage.setImageURI(selectedUri)
                }
            }
        }
    }

}