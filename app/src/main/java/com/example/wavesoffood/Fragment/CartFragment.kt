package com.example.wavesoffood.Fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.wavesoffood.CongratsBottomSheet
import com.example.wavesoffood.PayOutActivity
import com.example.wavesoffood.R
import com.example.wavesoffood.adaptar.CartAdapter
import com.example.wavesoffood.databinding.FragmentCartBinding
import com.example.wavesoffood.databinding.FragmentHomeBinding
import com.example.wavesoffood.model.CartItems
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.getValue


class CartFragment : Fragment() {
    private lateinit var binding: FragmentCartBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var foodNames: MutableList<String>
    private lateinit var foodPrices: MutableList<String>
    private lateinit var foodDescriptions: MutableList<String>
    private lateinit var foodImagesUri: MutableList<String>
    private lateinit var foodIngredients: MutableList<String>
    private lateinit var quantity: MutableList<Int>
    private lateinit var cartAdapter: CartAdapter
    private lateinit var userId: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentCartBinding.inflate(inflater, container, false)

        auth = FirebaseAuth.getInstance()
        retrieveCartItems()



        binding.proceedButton.setOnClickListener {
            // get order items details before processsing to check out
            getOrderItemsDetails()
            val intent = Intent(requireContext(), PayOutActivity::class.java)
            startActivity(intent)
        }




        return binding.root
    }

    private fun getOrderItemsDetails() {

        val orderIdReference: DatabaseReference =
            database.reference.child("user").child(userId).child("CartItems")
        val foodName = mutableListOf<String>()
        val foodPrice = mutableListOf<String>()
        val foodImage = mutableListOf<String>()
        val foodDescription = mutableListOf<String>()
        val foodIngredient = mutableListOf<String>()
        //get items quantities
        val foodQuantities = cartAdapter.getUpdatedItemsQuantities()

        orderIdReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                for (foodSnapshot: DataSnapshot in snapshot.children) {
                    //get items respective list
                    val orderItems = foodSnapshot.getValue(CartItems::class.java)
                    //add items details in to the list
                    orderItems?.foodName?.let { foodName.add(it) }
                    orderItems?.foodPrice?.let { foodPrice.add(it) }
                    orderItems?.foodDescription?.let { foodDescription.add(it) }
                    orderItems?.foodImage?.let { foodImage.add(it) }
                    orderItems?.foodIngredient?.let { foodIngredient.add(it) }
                }
                orderNow(
                    foodName,
                    foodPrice,
                    foodDescription,
                    foodImage,
                    foodIngredient,
                    foodQuantities
                )
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(
                    requireContext(),
                    "Order making failed, Please Try again",
                    Toast.LENGTH_SHORT
                ).show()
            }

        })


    }

    private fun orderNow(
        foodName: MutableList<String>,
        foodPrice: MutableList<String>,
        foodDescription: MutableList<String>,
        foodImage: MutableList<String>,
        foodIngredient: MutableList<String>,
        foodQuantities: MutableList<Int>,
    ) {
        if (isAdded && context != null) {
            val intent = Intent(requireContext(), PayOutActivity::class.java)
            intent.putExtra("foodItemName", foodName as ArrayList<String>)
            intent.putExtra("foodItemPrice", foodPrice as ArrayList<String>)
            intent.putExtra("foodItemImage", foodImage as ArrayList<String>)
            intent.putExtra("foodItemDescription", foodDescription as ArrayList<String>)
            intent.putExtra("foodItemIngredient", foodIngredient as ArrayList<String>)
            intent.putExtra("foodItemQuantities", foodQuantities as ArrayList<Int>)
            startActivity(intent)
        }
    }

    private fun retrieveCartItems() {
        // database reference to the firebase
        database = FirebaseDatabase.getInstance()
        userId = auth.currentUser?.uid ?: ""
        val foodReference: DatabaseReference =
            database.reference.child("user").child(userId).child("CartItems")
        //list to store cart items
        foodNames = mutableListOf()
        foodPrices = mutableListOf()
        foodDescriptions = mutableListOf()
        foodImagesUri = mutableListOf()
        foodIngredients = mutableListOf()
        quantity = mutableListOf()

        //fetch data rom the databae
        foodReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (foodSnapshot in snapshot.children) {
                    //get the cart items object from the child node
                    val cartItems: CartItems? = foodSnapshot.getValue(CartItems::class.java)
                    //add cart items to the list
                    cartItems?.foodName?.let { foodNames.add(it) }
                    cartItems?.foodPrice?.let { foodPrices.add(it) }
                    cartItems?.foodDescription?.let { foodDescriptions.add(it) }
                    cartItems?.foodImage?.let { foodImagesUri.add(it) }
                    cartItems?.foodQuantity?.let { quantity.add(it) }
                    cartItems?.foodIngredient?.let { foodIngredients.add(it) }


                }

                setAdapter()
            }

            private fun setAdapter() {
                val adapter = CartAdapter(
                    requireContext(),
                    foodNames,
                    foodPrices,
                    foodDescriptions,
                    foodImagesUri,
                    quantity,
                    foodIngredients
                )
                binding.cartRecyclerView.layoutManager =
                    LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
                binding.cartRecyclerView.adapter = adapter
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, "data not fetch", Toast.LENGTH_SHORT).show()
            }

        })
    }
}




























