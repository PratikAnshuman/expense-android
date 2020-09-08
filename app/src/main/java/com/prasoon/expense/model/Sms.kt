package com.prasoon.expense.model

import android.util.Log
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Sms(
    @PrimaryKey val id: Long,
    @ColumnInfo val address: String,
    @ColumnInfo val time: String,
    @ColumnInfo val body: String
) {
    val amount: Double
        get() = getAmountFromBody(body)

    //change to regex later, usage is temp
    private fun getAmountFromBody(body: String): Double {
        val bodyArray = body.split(" ")
        var amount: Double
        try {
            val index = bodyArray.indexOf("Rs.") + 1
            amount = bodyArray[index].toDouble()
            Log.i("amount", amount.toString())
            return amount
        } catch (e: NumberFormatException) {
            Log.i("catched", "block1")
        }
        try {
            bodyArray.forEach {
                if (it.startsWith("Rs.")) {
                    amount = it.substring(3).toDouble()
                    Log.i("amount", amount.toString())
                    return amount
                }
            }
        } catch (e: Exception) {
            Log.i("catched", "block2")
            e.printStackTrace()
        }
        try {
            bodyArray.forEach {
                if (it.startsWith("INR")) {
                    amount = it.substring(3).toDouble()
                    Log.i("amount", amount.toString())
                    return amount
                }
            }
        } catch (e: Exception) {
            Log.i("catched", "block3")
            e.printStackTrace()
        }
        try {
            val index = bodyArray.indexOf("INR") + 1
            amount = bodyArray[index].replace(",", "").toDouble()
            Log.i("amount", amount.toString())
            return amount
        } catch (e: Exception) {
            Log.i("catched", "block4")
            e.printStackTrace()
        }

        return 0.0
    }
}