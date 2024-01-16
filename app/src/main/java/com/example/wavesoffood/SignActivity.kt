package com.example.wavesoffood

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.wavesoffood.databinding.ActivitySignBinding

class SignActivity : AppCompatActivity() {
    private val binding: ActivitySignBinding by lazy {
        ActivitySignBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        binding.alreadyhavebutton.setOnClickListener {
            val intent = Intent(this,LoginActivity::class.java)
            startActivity(intent)
        }
    }
}