package com.fatih.instagramapp.view.mainui.profileui

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
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
import androidx.core.app.ActivityOptionsCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.fatih.instagramapp.R
import com.fatih.instagramapp.databinding.FragmentEditProfileBinding
import com.fatih.instagramapp.model.Users
import com.fatih.instagramapp.util.Status
import com.fatih.instagramapp.view.loginui.ui.EntranceActivity
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.AndroidEntryPoint
import java.io.ByteArrayOutputStream
import javax.inject.Inject

@AndroidEntryPoint
class EditProfileFragment @Inject constructor(private val firestore: FirebaseFirestore,private val auth: FirebaseAuth): Fragment(R.layout.fragment_edit_profile) {

    private lateinit var binding: FragmentEditProfileBinding
    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>
    private lateinit var permissionLauncher: ActivityResultLauncher<String>
    private lateinit var viewModel: EditProfileFragmentViewModel
    private var selectedUri: Uri?=null
    private var selectedUser:Users?=null
    private var choice=""


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding= DataBindingUtil.inflate(inflater,R.layout.fragment_edit_profile,container,false)
        doInit()

        return binding.root
    }

    private fun doInit(){
        viewModel=ViewModelProvider(requireActivity())[EditProfileFragmentViewModel::class.java]
        setOnClickListeners()
        getUserFromBundle()
        registerLauncher()
        observeLiveData()
    }

    private fun setOnClickListeners(){
        binding.closeImage.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.saveImage.setOnClickListener {
            try {
                val profileName=binding.editProfileNameText.text.toString()
                val userName=binding.editProfileUsernameText.text.toString()
                val bioText=binding.editProfileBioText.text.toString()
                val website=binding.editProfileWebsiteText.text.toString()
                val date= Timestamp.now().toDate().time
                if(profileName.isNotEmpty()&&userName.isNotEmpty()){
                    val user=Users(userName = userName, name = profileName, bio = bioText, website = website, date = date, searchName = profileName.lowercase(), searchUserName = userName.lowercase())
                    saveUserData(user)
                }
            }catch (e:Exception){
                Toast.makeText(requireContext(),e.message,Toast.LENGTH_SHORT).show()
            }

        }

        binding.logoutButton.setOnClickListener {
            auth.signOut()
            val intent= Intent(requireActivity(), EntranceActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
        }

        binding.selectPhotoText.setOnClickListener {
            choice="kirp"
            checkPermission(it,choice)
        }
        binding.cutPhotoText.setOnClickListener {
            choice="sec"
            checkPermission(it,choice)
        }

    }

    private fun  getUserFromBundle(){
        arguments?.let {
            selectedUser =EditProfileFragmentArgs.fromBundle(it).user
            binding.user=selectedUser
        }
    }

    private fun saveUserData(user:Users){
        selectedUser?.let {
            viewModel.editUserInformation(it.uid!!,user,selectedUri)
        }
    }

    private fun observeLiveData(){
        viewModel.controlMessage.observe(viewLifecycleOwner){
            when(it.status){
                Status.ERROR->{Toast.makeText(requireContext(),it.message,Toast.LENGTH_SHORT).show()}
                Status.LOADING->{
                    println("loading")}
                Status.SUCCESS->{
                    println("success")
                    Toast.makeText(requireContext(),"Success",Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun checkPermission(view:View,s:String){
        if(ContextCompat.checkSelfPermission(requireContext(),android.Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
                if(ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(),android.Manifest.permission.READ_EXTERNAL_STORAGE)){
                    Snackbar.make(view,"Need Permission", Snackbar.LENGTH_SHORT).setAction("Give Permission") {
                        permissionLauncher.launch(android.Manifest.permission.READ_EXTERNAL_STORAGE)
                    }.show()
                }else{
                    permissionLauncher.launch(android.Manifest.permission.READ_EXTERNAL_STORAGE)
                }
         }else{
             if(s=="kirp"){
                 try {
                     val cropIntent =Intent("com.android.camera.action.CROP")
                     cropIntent.setDataAndType(selectedUri, "image/*")
                     cropIntent.putExtra("crop", true)
                     cropIntent.putExtra("aspectX", 1)
                     cropIntent.putExtra("aspectY", 1)
                     cropIntent.putExtra("outputX", 128)
                     cropIntent.putExtra("outputY", 128)
                     cropIntent.putExtra("return-data", true)
                     activityResultLauncher.launch(cropIntent)
                 }  catch (e:ActivityNotFoundException) {
                     // display an error message
                     val errorMessage = "Whoops - your device doesn't support the crop action!"
                     Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
                 }
             } else if(s=="sec"){
                 val intent=Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                 activityResultLauncher.launch(intent)
             }

         }
    }

    private fun registerLauncher(){
        permissionLauncher=registerForActivityResult(ActivityResultContracts.RequestPermission()){
            if(it){
                val intent=Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                val cropIntent =Intent("com.android.camera.action.CROP",MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                if(choice=="sec"){
                    activityResultLauncher.launch(intent)
                }else if(choice=="kirp"){
                    activityResultLauncher.launch(cropIntent)
                }
            }
            Toast.makeText(requireContext(),"Need Permission", Toast.LENGTH_SHORT).show()
        }
        activityResultLauncher=registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
            if(it.resultCode== Activity.RESULT_OK){
                it.data?.extras?.let { it->
                    val selectedBitmap: Bitmap? =it.getParcelable("data")
                    selectedBitmap?.let { bitmap->
                        selectedUri=getImageUri(bitmap)
                        binding.editProfileImage.setImageURI(selectedUri)
                    }
                }
                it.data?.data?.let { uri->
                    selectedUri= uri
                    binding.editProfileImage.setImageURI(selectedUri)
                }
            }
        }

    }
    private fun getImageUri(inImage:Bitmap):Uri {
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, ByteArrayOutputStream());
        val path = MediaStore.Images.Media.insertImage(requireContext().contentResolver, inImage, "Title", null);
        return Uri.parse(path);
    }

}