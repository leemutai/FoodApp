package com.example.wavesoffood

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.wavesoffood.databinding.ActivityPayOutBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class PayOutActivity : AppCompatActivity() {
    private lateinit var binding : ActivityPayOutBinding

    private lateinit var auth: FirebaseAuth
    private lateinit var databaseReference: DatabaseReference
    private lateinit var name:String
    private lateinit var address:String
    private lateinit var phone:String
    private lateinit var totalAmount:String
    private lateinit var foodItemName:ArrayList<String>
    private lateinit var foodItemPrice:ArrayList<String>
    private lateinit var foodItemImage:ArrayList<String>
    private lateinit var foodItemDescription:ArrayList<String>
    private lateinit var foodItemIngredient:ArrayList<String>
    private lateinit var foodItemQuantities:ArrayList<Int>
    private lateinit var userId:String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPayOutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //initialize firebse and user details
        auth = FirebaseAuth.getInstance()
        databaseReference = FirebaseDatabase.getInstance().getReference()

        //set user date
        setUserData()
        binding.placeMyOrder.setOnClickListener{
            val bottomSheetDialog = CongratsBottomSheet()
            bottomSheetDialog.show(supportFragmentManager,"Test")
        }
    }

    private fun setUserData() {
        val user:FirebaseUser? = auth.currentUser
         if (user!=null){
             val userId:String =user.uid
             val userReference = databaseReference.child("user").child(userId)

             userReference.addListenerForSingleValueEvent(object :ValueEventListener{
                 override fun onDataChange(snapshot: DataSnapshot) {
                     if (snapshot.exists()){
                         val names = snapshot.child("name").getValue(String::class.java)?:""
                         val addresses = snapshot.child("address").getValue(String::class.java)?:""
                         val phones = snapshot.child("phone").getValue(String::class.java)?:""
                         binding.apply {
                             name.setText(names)
                             address.setText(addresses)
                             phone.setText(phones)
                     }

                     }
                 }

                 override fun onCancelled(error: DatabaseError) {

                  }

             })
         }
    }
}













