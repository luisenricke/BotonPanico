package com.luisenricke.botonpanico

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity()
    //BottomNavigationView.OnNavigationItemSelectedListener {
{

    lateinit var toolbar:Toolbar

    lateinit var navigation: BottomNavigationView

    lateinit var navController: NavController
    lateinit var appBarConfiguration: AppBarConfiguration
    val navigationIds = setOf(
        R.id.nav_home, R.id.nav_alert, R.id.nav_profile
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//        toolbar = findViewById(R.id.toolbar)
//        setSupportActionBar(toolbar)

        navigation = findViewById(R.id.navigation)
        navController = findNavController(R.id.fragment_host)

        appBarConfiguration = AppBarConfiguration(navigationIds)
        setupActionBarWithNavController(navController, appBarConfiguration)
        navigation.setupWithNavController(navController)
    }

/*
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_first -> {
                navController.navigate(R.id.firstFragment)
            }
            R.id.nav_second -> {
                navController.navigate(R.id.secondFragment)
            }
            else -> return super.onOptionsItemSelected(item)
        }
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        return navigateUp(navController, appBarConfiguration) || super.onSupportNavigateUp()
    }

 */
}
