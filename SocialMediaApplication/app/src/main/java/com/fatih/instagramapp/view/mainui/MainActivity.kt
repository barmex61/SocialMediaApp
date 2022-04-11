package com.fatih.instagramapp.view.mainui

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.fatih.instagramapp.R
import com.fatih.instagramapp.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var _binding: ActivityMainBinding
    val binding:ActivityMainBinding
        get() = _binding
    private lateinit var _navController: NavController
    private var myTimer:Long=0
    private val onBackPressedTimer:Long=2000


    @Inject
    lateinit var fragmentFactory: MainFragmentFactory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportFragmentManager.fragmentFactory=fragmentFactory
        _binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        _binding.navView.background=null
        doInit()

    }

    private fun doInit(){
        setBottomNavigationView()
    }

    private fun setBottomNavigationView(){
        val navHostFragment=supportFragmentManager.findFragmentById(R.id.fragmentContainer) as NavHostFragment
        _navController=navHostFragment.findNavController()
        _binding.navView.setupWithNavController(_navController)
    }

    override fun onBackPressed() {
        when(_navController.currentDestination?.id){

            R.id.homeFragment->{
                if(myTimer+onBackPressedTimer>System.currentTimeMillis()){
                    finishAffinity()
                }else{
                    Toast.makeText(this@MainActivity,"Press back button in order to exit",
                        Toast.LENGTH_SHORT).show()
                }
                myTimer=System.currentTimeMillis()
            }
            R.id.editProfileFragment->{
                binding.bottomAppBar.performShow(true)
                super.onBackPressed()
            }
            else->{
                super.onBackPressed()
            }
        }
    }

}