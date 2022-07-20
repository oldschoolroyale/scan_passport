//package com.brm.machinereablezone
//
//import android.os.Bundle
//import androidx.appcompat.app.AppCompatActivity
//import androidx.navigation.findNavController
//import androidx.navigation.ui.setupActionBarWithNavController
//
//
//class MainActivity : AppCompatActivity() {
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_main)
//        setupActionBarWithNavController(findNavController(R.id.mainNavHost))
//    }
//
//
//    override fun onSupportNavigateUp(): Boolean {
//        return super.onSupportNavigateUp() || findNavController(R.id.mainNavHost).navigateUp()
//    }
//
//
//}