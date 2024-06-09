package com.jackahn.randomfoodgood.view.main

import android.os.Bundle
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.jackahn.randomfoodgood.R
import com.jackahn.randomfoodgood.dto.OnDataPass
import com.jackahn.randomfoodgood.dao.PlaceResult
import com.jackahn.randomfoodgood.dao.User
import com.jackahn.randomfoodgood.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(), OnDataPass {

    private lateinit var binding: ActivityMainBinding
    private var result = ArrayList<PlaceResult>()
    private var user = User()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        user = intent.extras!!.get("user") as User

        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.

//        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }

    override fun onDataPass(data: ArrayList<PlaceResult>) {
        result = data
    }

    fun getResult(): ArrayList<PlaceResult>{
        return result
    }

    fun getUser(): User {
        return user
    }
}