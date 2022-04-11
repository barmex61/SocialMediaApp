package com.fatih.instagramapp.view.loginui.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.fatih.instagramapp.R
import com.fatih.instagramapp.databinding.ActivityEntranceBinding
import com.fatih.instagramapp.view.mainui.MainActivity
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class EntranceActivity : AppCompatActivity() {

    private lateinit var navController: NavController
    private lateinit var navHostFragment:NavHostFragment
    private lateinit var _activityEntranceBinding: ActivityEntranceBinding
    val activityEntranceBinding:ActivityEntranceBinding
        get() = _activityEntranceBinding
    private val onBackPressedTimer:Long=2000
    private var myTimer:Long=0
    @Inject
    lateinit var fragmentFactory: MyFragmentFactory
    @Inject
    lateinit var auth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportFragmentManager.fragmentFactory=fragmentFactory
        _activityEntranceBinding=DataBindingUtil.setContentView(this,R.layout.activity_entrance)
        navHostFragment=supportFragmentManager.findFragmentById(R.id.fragmentContainerView2) as NavHostFragment
        navController=navHostFragment.findNavController()
        if(auth.currentUser!=null){
            startActivity(Intent(this,MainActivity::class.java))
            finish()
        }

    }

    override fun onBackPressed() {
        when(navController.currentDestination?.id){

            R.id.signInFragment->{
                if(myTimer+onBackPressedTimer>System.currentTimeMillis()){
                    finishAffinity()
                }else{
                    Toast.makeText(this@EntranceActivity,"Press back button in order to exit",Toast.LENGTH_SHORT).show()
                }
                myTimer=System.currentTimeMillis()
            }
            else->{
                super.onBackPressed()
            }
        }
    }
}