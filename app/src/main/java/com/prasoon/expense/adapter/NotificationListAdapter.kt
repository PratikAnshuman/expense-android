package com.prasoon.expense.adapter

import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.prasoon.expense.R
import com.prasoon.expense.model.Sms
import com.prasoon.expense.utils.convertLongToTime
import com.prasoon.expense.utils.setAmount
import kotlinx.android.synthetic.main.item_notification.view.*
import java.lang.NumberFormatException


class NotificationListAdapter(
    private val smsList: ArrayList<Sms>
//    private inline val onCategoryNameClicked: (sms: Sms) -> Unit
) :
    RecyclerView.Adapter<NotificationListAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val amount: TextView = view.amountTv
        val time: TextView = view.timeTv
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder = ViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.item_notification, parent, false)
    )

    override fun getItemCount(): Int {
        return smsList.size
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val sms = smsList[position]

        holder.time.text = sms.time.toLong().convertLongToTime()

        val bodyArray = sms.body.split(" ")
        try {
            val index = bodyArray.indexOf("Rs.") + 1
            val amount = bodyArray[index].toDouble()
            Log.i("body", sms.toString())
            holder.amount.setAmount(amount)
        } catch (e: NumberFormatException) {
            try {
                bodyArray.forEach {
                    if (it.startsWith("Rs.")) {
                        val amount = it.substring(3).toDouble()
                        Log.i("amount", amount.toString())
                        holder.amount.setAmount(amount)
                        return
                    }
                }
            } catch (e: NumberFormatException) {
                holder.amount.setTextAppearance(android.R.style.TextAppearance_Material_Small)
                holder.amount.text = "Payment recorded but amount couldn't be retrieved."
            }
        }
    }
}