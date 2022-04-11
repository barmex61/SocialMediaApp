package com.fatih.instagramapp.view.mainui

import android.app.Activity
import android.app.ProgressDialog
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
import android.widget.ImageView
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
import com.fatih.instagramapp.databinding.FragmentPostBinding
import com.fatih.instagramapp.model.Posts
import com.fatih.instagramapp.util.Status
import com.fatih.instagramapp.util.getNavOptions
import com.fatih.instagramapp.view.mainui.viewodel.PostFragmentViewModel
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import java.io.ByteArrayOutputStream
import javax.inject.Inject

@AndroidEntryPoint
class PostFragment @Inject constructor(private val auth: FirebaseAuth): Fragment() {

    private var choice="sec"
    private var selectedUri:Uri?=null
    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>
    private lateinit var permissionLauncher: ActivityResultLauncher<String>
    private lateinit var binding:FragmentPostBinding
    private lateinit var viewmodel: PostFragmentViewModel
    private lateinit var dialog:ProgressDialog


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding=DataBindingUtil.inflate(inflater, R.layout.fragment_post,container,false)
        doInit()
        return binding.root
    }

    private fun doInit(){
        viewmodel=ViewModelProvider(requireActivity())[PostFragmentViewModel::class.java]
        registerLauncher()
        setOnClickListeners()
        observeLiveData()
    }

    private fun observeLiveData(){
        viewmodel.controlMessage.observe(viewLifecycleOwner){
            when(it.status){
                Status.ERROR->{
                    println(it.message)}
                Status.LOADING->{
                    println("loading")}
                Status.SUCCESS->{
                    println("success")
                dialog.dismiss()
                findNavController().navigate(R.id.homeFragment,null, getNavOptions())}
            }
        }
    }

    private fun setOnClickListeners(){
        binding.closeImage.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.saveImage.setOnClickListener {
            postImage()
        }
        binding.selectPhotoText.setOnClickListener {
            choice="sec"
            checkPermission(it,choice)
        }
        binding.cutPhotoText.setOnClickListener {
            choice="kirp"
            checkPermission(it,choice)
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
                    val cropIntent = Intent("com.android.camera.action.CROP")
                    cropIntent.setDataAndType(selectedUri, "image/*")
                    cropIntent.putExtra("crop", true)
                    cropIntent.putExtra("aspectX", 1)
                    cropIntent.putExtra("aspectY", 1)
                    cropIntent.putExtra("outputX", 128)
                    cropIntent.putExtra("outputY", 128)
                    cropIntent.putExtra("return-data", true)
                    activityResultLauncher.launch(cropIntent)
                }  catch (e: ActivityNotFoundException) {
                    // display an error message
                    val errorMessage = "Whoops - your device doesn't support the crop action!"
                    Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
                }
            } else if(s=="sec"){
                val intent= Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                activityResultLauncher.launch(intent)
            }

        }
    }

    private fun registerLauncher(){
        permissionLauncher=registerForActivityResult(ActivityResultContracts.RequestPermission()){
            if(it){
                val intent= Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                val cropIntent = Intent("com.android.camera.action.CROP", MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
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
                it.data?.extras?.let { bundle->
                    val selectedBitmap: Bitmap? =bundle.getParcelable("data")
                    selectedBitmap?.let { bitmap->
                        selectedUri=getImageUri(bitmap)
                        binding.postImage.setImageURI(selectedUri)
                    }
                }
                it.data?.data?.let { uri->
                    selectedUri= uri
                    binding.postImage.setImageURI(selectedUri)
                }
            }
        }

    }

    private fun getImageUri(inImage: Bitmap): Uri {
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, ByteArrayOutputStream())
        val path = MediaStore.Images.Media.insertImage(requireContext().contentResolver, inImage, "Title", null)
        return Uri.parse(path)
    }

    private fun postImage(){
        try {
            dialog= ProgressDialog(requireContext())
            dialog.setCanceledOnTouchOutside(false)
            dialog.setTitle("Creating a new post")
            dialog.setMessage("Please wait ...")
            dialog.show()
            selectedUri?.let {
                val title=binding.postText.text.toString()
                val date= Timestamp.now().toDate().time
                val post=Posts(null,date,null,0,0,"",auth.uid!!,title)
                if(title.isNotEmpty()){
                    viewmodel.postImage(post,it)
                }
            }
        }catch (e:Exception){
            Toast.makeText(requireContext(),e.message,Toast.LENGTH_SHORT).show()
        }

    }

    override fun onDestroyView() {
        viewmodel.resetControlMessage()
        super.onDestroyView()
    }

}