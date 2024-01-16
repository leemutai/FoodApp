package com.example.wavesoffood

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import com.example.wavesoffood.databinding.ActivityChooseLocationBinding

class ChooseLocationActivity : AppCompatActivity() {
    private val binding: ActivityChooseLocationBinding by lazy {
        ActivityChooseLocationBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        val locationList: Array<String> = arrayOf("Jaiupur", "Odisha", "Bundi","Nairobi","Vasha")
        val adapter:ArrayAdapter<String> = ArrayAdapter(this, android.R.layout.simple_list_item_1,locationList)
        val autoCompleteTextView:AutoCompleteTextView = binding.listOfLocation
        autoCompleteTextView.setAdapter(adapter)
    }
}