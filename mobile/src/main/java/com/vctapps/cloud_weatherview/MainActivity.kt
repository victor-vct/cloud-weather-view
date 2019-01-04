package com.vctapps.cloud_weatherview

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.FirebaseApp
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val database = FirebaseDatabase.getInstance().getReference("cloud").child("state")

        buttonClear.setOnClickListener { database.setValue(0) }
        buttonFewClouds.setOnClickListener { database.setValue(1) }
        buttonClouds.setOnClickListener { database.setValue(2) }
        buttonRain.setOnClickListener { database.setValue(3) }
        buttonThunderstorm.setOnClickListener { database.setValue(4) }

    }
}
