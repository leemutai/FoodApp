package com.example.wavesoffood

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.wavesoffood.databinding.ActivityRecentOrderItemsBinding
import com.example.wavesoffood.model.OrderDetails

class recentOrderItems : AppCompatActivity() {
    private val binding :ActivityRecentOrderItemsBinding by lazy {
        ActivityRecentOrderItemsBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val recentOrderItems = intent.getParcelableArrayListExtra<OrderDetails>("RecentBuyOrderItem")
        recentOrderItems?.let {orderDetails ->
            if (orderDetails.isNotEmpty()){
                val recentOrderItems = OrderDetails[0]
            }
        }
    }
}


















