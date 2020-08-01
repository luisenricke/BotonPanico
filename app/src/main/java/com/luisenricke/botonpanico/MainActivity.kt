package com.luisenricke.botonpanico

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI.navigateUp
import androidx.navigation.ui.setupWithNavController
import com.luisenricke.botonpanico.databinding.ActivityMainBinding

// https://developer.android.com/training/tv/playback/onboarding
// TODO: Make service to INPUT_METHOD
class MainActivity : AppCompatActivity() {

//    private val inputMethodManager: InputMethodManager by lazy {
//        getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
//    }

    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!

    private val appBarConfiguration: AppBarConfiguration by lazy {
        AppBarConfiguration(setOf(R.id.nav_home, R.id.nav_alert, R.id.nav_profile))
    }

    private val navController: NavController by lazy {
//        fragment
        findNavController(R.id.fragment_host)
//        FragmentContainerView
//        (supportFragmentManager.findFragmentById(binding.fragmentHost.id) as NavHostFragment)
//            .navController
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)

        binding.apply {
            setContentView(root)
            bottom.setupWithNavController(navController)
        }
    }

    override fun onStop() {
        super.onStop()
        _binding = null
    }

    override fun onSupportNavigateUp(): Boolean {
        return navigateUp(navController, appBarConfiguration) || super.onSupportNavigateUp()
    }

    fun setBottomNavigationViewVisibility(isVisible: Boolean) {
        if(isVisible) binding.bottom.visibility = View.VISIBLE
        else binding.bottom.visibility = View.GONE
    }

//    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
//        if (ev?.action == MotionEvent.ACTION_DOWN) {
//            val actual = currentFocus
//            if (actual is TextInputEditText) {
//                val borderView = Rect()
//                actual.getGlobalVisibleRect(borderView)
//                if (!borderView.contains(ev.rawX.toInt(), ev.rawY.toInt())) {
//                    inputMethodManager.hideSoftInputFromWindow(actual.windowToken, 0)
//                }
//            }
//        }
//        return super.dispatchTouchEvent(ev)
//    }
}
