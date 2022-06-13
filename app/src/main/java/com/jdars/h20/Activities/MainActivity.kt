package com.jdars.h20

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.core.view.GravityCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.jdars.h20.Activities.CartListActivity
import com.jdars.h20.Activities.LoginActivity
import com.jdars.h20.Activities.ProfileActivity
import com.jdars.h20.databinding.ActivityMainBinding
import com.jdars.shared_online_business.CallBacks.DrawerHandler
import com.jdars.shared_online_business.Utils.BaseUtils.Companion.phoneIntent

class MainActivity : AppCompatActivity(), DrawerHandler, NavigationView.OnNavigationItemSelectedListener {

    private lateinit var binding: ActivityMainBinding
    private lateinit var bottomNavView: BottomNavigationView
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setUpUi()
        setUpBottomNavigation()

    }

    private fun setUpNavigation() {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment_home) as NavHostFragment
        navController = navHostFragment.navController
    }

    private fun setUpUi() {
        bottomNavView = binding.bottomNavView
        binding.navMenu.setNavigationItemSelectedListener(this)
        setUpNavigation()
    }

    private fun setUpBottomNavigation() {
        bottomNavView.setupWithNavController(navController)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.menu_home ->{
                binding.drawerLayout.closeDrawers()
                when {
                    navController.currentDestination!!.id != R.id.dashboardFragment ->
                        navController . navigate (R.id.dashboardFragment)
                }
            }
            R.id.menu_logout ->{
                binding.drawerLayout.closeDrawers()
                FirebaseAuth.getInstance().signOut()
                startActivity(Intent(this,LoginActivity::class.java))
                finish()
            }
            R.id.menu_cart ->{
                binding.drawerLayout.closeDrawers()
                startActivity(Intent(this,CartListActivity::class.java))
            }
            R.id.menu_profile ->{
                binding.drawerLayout.closeDrawers()
                startActivity(Intent(this,ProfileActivity::class.java))
            }
            R.id.menu_call ->{
                binding.drawerLayout.closeDrawers()
                phoneIntent(this@MainActivity)
            }
            R.id.menu_mail ->{
                binding.drawerLayout.closeDrawers()
                composeEmail("Bcsm-f18-049superior.edu.pk")
            }
        }
        return false
    }

    override fun openDrawer() {
        binding.drawerLayout.openDrawer(GravityCompat.START)
    }

    private fun composeEmail(addresses: String) {
        val email = Intent(Intent.ACTION_SEND).apply {
            data = Uri.parse("mailto:")
            putExtra(Intent.EXTRA_EMAIL, arrayOf(addresses))
            putExtra(Intent.EXTRA_SUBJECT, "Subject Text Here..")
            putExtra(Intent.EXTRA_TEXT, "")
            type = "message/rfc822"
        }
        startActivity(Intent.createChooser(email, "Send Mail Using :"))
    }
}