package com.anastasiu.staytfhome.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.Navigation.findNavController
import androidx.navigation.ui.NavigationUI
import com.anastasiu.staytfhome.R
import com.anastasiu.staytfhome.data.model.User
import com.anastasiu.staytfhome.ui.fragment.MainFragmentDirections
import com.anastasiu.staytfhome.ui.viewmodel.LoginViewModel
import dagger.android.AndroidInjection
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.HasSupportFragmentInjector
import kotlinx.android.synthetic.main.activity_main.*
import javax.inject.Inject

class MainActivity : AppCompatActivity(), HasSupportFragmentInjector {
    @Inject
    lateinit var dispatchingAndroidInjector: DispatchingAndroidInjector<Fragment>

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    lateinit var loginViewModel: LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.toolbar))

        loginViewModel = ViewModelProviders.of(this, viewModelFactory).get(
            LoginViewModel::class.java)

        setupNavigation()

        loginViewModel.user.observe(this, Observer<User> { user ->
            if (user === null) {
                if (findNavController(this, R.id.nav_host_fragment).currentDestination!!.id == R.id.mainFragment) {
                    findNavController(this, R.id.nav_host_fragment)
                        .navigate(MainFragmentDirections.actionMainFragmentToAuthFlow())
                }
            } else {
                if (!user.roles.split(",").contains("user")) {
                    bottomNavigation.visibility = View.GONE
                    findNavController(this, R.id.nav_host_fragment)
                        .navigate(MainFragmentDirections.actionMainFragmentToProfileManagerFragment())
                }
            }
        })
        loginViewModel.fetchUser()
    }

    private fun setupNavigation() {
        val navController = findNavController(this, R.id.nav_host_fragment)
        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.mainFragment -> showNavigationComponents()
                R.id.reportManagerFragment -> showNavigationComponents()
                else -> hideNavigationComponents()
            }
        }
        NavigationUI.setupActionBarWithNavController(this, navController)
        NavigationUI.setupWithNavController(bottomNavigation, navController)
    }

    private fun showNavigationComponents() {
        toolbar.visibility = View.VISIBLE
        bottomNavigation.visibility = View.VISIBLE
    }

    private fun hideNavigationComponents() {
        toolbar.visibility = View.GONE
        bottomNavigation.visibility = View.GONE
    }

    override fun supportFragmentInjector() = dispatchingAndroidInjector

    companion object {
        const val MY_PERMISSIONS_REQUEST_READ_CONTACTS: Int = 10
    }
}
