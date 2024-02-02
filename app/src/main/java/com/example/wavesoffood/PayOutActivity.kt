import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.wavesoffood.CongratsBottomSheet
import com.example.wavesoffood.databinding.ActivityPayOutBinding
import com.example.wavesoffood.model.OrderDetails
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*

class PayOutActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPayOutBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var databaseReference: DatabaseReference
    private lateinit var name: String
    private lateinit var address: String
    private lateinit var phone: String
    private lateinit var totalAmount: String
    private lateinit var foodItemName: ArrayList<String>
    private lateinit var foodItemPrice: ArrayList<String>
    private lateinit var foodItemImage: ArrayList<String>
    private lateinit var foodItemDescription: ArrayList<String>
    private lateinit var foodItemIngredient: ArrayList<String>
    private lateinit var foodItemQuantities: ArrayList<Int>
    private lateinit var userId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPayOutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        databaseReference = FirebaseDatabase.getInstance().reference

        setUserData()

        val intent = intent
        foodItemName = intent.getStringArrayListExtra("foodItemName") ?: ArrayList()
        foodItemPrice = intent.getStringArrayListExtra("foodItemPrice") ?: ArrayList()
        foodItemImage = intent.getStringArrayListExtra("foodItemImage") ?: ArrayList()
        foodItemDescription = intent.getStringArrayListExtra("foodItemDescription") ?: ArrayList()
        foodItemIngredient = intent.getStringArrayListExtra("foodItemIngredient") ?: ArrayList()
        foodItemQuantities = intent.getIntegerArrayListExtra("foodItemQuantities") ?: ArrayList()

        totalAmount = calculateTotalAmount().toString() + "$"
        binding.totalAmount.isEnabled = false
        binding.totalAmount.setText(totalAmount)

        binding.backButton.setOnClickListener {
            finish()
        }

        binding.placeMyOrder.setOnClickListener {
            name = binding.name.text.toString().trim()
            address = binding.address.text.toString().trim()
            phone = binding.phone.text.toString().trim()

            if (name.isBlank() || address.isBlank() || phone.isBlank()) {
                Toast.makeText(this, "Please Enter all the details", Toast.LENGTH_SHORT).show()
            } else {
                placeOrder()
            }
        }
    }

    private fun placeOrder() {
        try {
            userId = auth.currentUser?.uid ?: ""
            val time: Long = System.currentTimeMillis()
            val itemPushKey: String? = databaseReference.child("OrderDetails").push().key

            if (itemPushKey != null) {
                val orderDetails = OrderDetails(
                    userId, name, foodItemName, foodItemPrice, foodItemImage,
                    foodItemQuantities, address, phone, time, itemPushKey, false, false
                )

                val orderReference: DatabaseReference =
                    databaseReference.child("OrderDetails").child(itemPushKey)
                orderReference.setValue(orderDetails).addOnSuccessListener {
                    Log.d("PayOutActivity", "Order placed successfully")
                    val bottomSheetDialog = CongratsBottomSheet()
                    bottomSheetDialog.show(supportFragmentManager, "Test")
                    removeItemFromCart()
                    finish()
                }.addOnFailureListener { e ->
                    Log.e("PayOutActivity", "Failed to place order: ${e.message}", e)
                    Toast.makeText(this, "Failed to place order. Please try again.", Toast.LENGTH_SHORT).show()
                }
            } else {
                Log.e("PayOutActivity", "Failed to generate order key.")
                Toast.makeText(this, "Failed to generate order key. Please try again.", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Log.e("PayOutActivity", "Exception in placeOrder(): ${e.message}", e)
            Toast.makeText(this, "An unexpected error occurred. Please try again.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun removeItemFromCart() {
        val cartItemsReference: DatabaseReference =
            databaseReference.child("user").child(userId).child("CartItems")
        cartItemsReference.removeValue().addOnFailureListener { e ->
            Log.e("PayOutActivity", "Failed to remove items from the cart: ${e.message}", e)
            Toast.makeText(this, "Failed to remove items from the cart. Please try again.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun calculateTotalAmount(): Int {
        var totalAmount = 0
        for (i: Int in 0 until foodItemPrice.size) {
            val price: String = foodItemPrice[i]
            val lastChar: Char = price.last()
            val priceIntValue: Int = if (lastChar == '$') {
                price.dropLast(1).toInt()
            } else {
                price.toInt()
            }
            val quantity: Int = foodItemQuantities[i]
            totalAmount += priceIntValue * quantity
        }
        return totalAmount
    }

    private fun setUserData() {
        val user: FirebaseUser? = auth.currentUser
        if (user != null) {
            userId = user.uid
            val userReference = databaseReference.child("user").child(userId)

            userReference.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val names = snapshot.child("name").getValue(String::class.java) ?: ""
                        val addresses = snapshot.child("address").getValue(String::class.java) ?: ""
                        val phones = snapshot.child("phone").getValue(String::class.java) ?: ""
                        binding.apply {
                            name.setText(names)
                            address.setText(addresses)
                            phone.setText(phones)
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("PayOutActivity", "Failed to retrieve user data: ${error.message}", error.toException())
                    Toast.makeText(
                        this@PayOutActivity,
                        "Failed to retrieve user data. Please try again.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
        }
    }
}
