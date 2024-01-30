package com.example.wavesoffood

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.bumptech.glide.Glide
import com.example.wavesoffood.databinding.ActivityDetailsBinding
import com.example.wavesoffood.model.CartItems
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class DetailsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailsBinding
    private var foodName: String? = null
    private var foodImage: String? = null
    private var foodDescription: String? = null
    private var foodIngredients: String? = null
    private var foodPrice: String? = null
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //initialize FirebaseAuth
        auth = FirebaseAuth.getInstance()
        foodName = intent.getStringExtra("MenuItemName")
        foodDescription = intent.getStringExtra("MenuItemDescription")
        foodIngredients = intent.getStringExtra("MenuItemIngredients")
        foodPrice = intent.getStringExtra("MenuItemPrice")
        foodImage = intent.getStringExtra("MenuItemImage")

        with(binding) {
            detailsFoodName.text = foodName
            detailsDescription.text = foodDescription
            detailsIngredients.text = foodIngredients
            Glide.with(this@DetailsActivity).load(Uri.parse(foodImage)).into(detailsFoodImage)
        }

        binding.imageButton.setOnClickListener {
            finish()
        }
        binding.addItemButton.setOnClickListener {
            addItemToCart()
        }
    }

    private fun addItemToCart() {
        val database:DatabaseReference = FirebaseDatabase.getInstance().reference
        val userId: String = auth.currentUser?.uid?:""

        //create a cartItems object
        val cartItem = CartItems(foodName.toString(),foodPrice.toString(),foodDescription.toString(),foodImage.toString(),1)
        // save data cat item on firebase
        database.child("user").child(userId).child("CartItems").push().setValue(cartItem).addOnSuccessListener {
            Toast.makeText( this,"Items added into cart successfully", Toast.LENGTH_SHORT).show()
        }.addOnFailureListener {
            Toast.makeText( this,"Item not added", Toast.LENGTH_SHORT).show()
        }
    }
}
