package com.prasoon.expense.adapter

import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.prasoon.expense.R
import com.prasoon.expense.model.Sms
import com.prasoon.expense.utils.convertLongToTime
import com.prasoon.expense.utils.setAmount
import kotlinx.android.synthetic.main.item_notification.view.*

class NotificationListAdapter(
    private val smsList: ArrayList<Sms>,
    private inline val onAddAmountPressed: (id: Long) -> Unit
) :
    RecyclerView.Adapter<NotificationListAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val amount: TextView = view.amountTv
        val time: TextView = view.timeTv
        val sender: TextView = view.senderTv
        val add: Button = view.addExpenseBtn
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
        holder.sender.text = sms.address

        val amount = sms.amount
        if (amount != 0.0) {
            holder.amount.setAmount(amount)
            holder.amount.setTextAppearance(R.style.NotificationAmountViewStyle)
        } else {
            holder.amount.setTextAppearance(android.R.style.TextAppearance_Material_Small)
        }

        holder.add.setOnClickListener {
            onAddAmountPressed.invoke(sms.id)
        }
    }

    fun removeNotification(id: Long) {
        val pos = smsList.indexOf(smsList.single { it.id == id })
        smsList.removeAt(pos)
        notifyItemRemoved(pos)
        notifyItemRangeChanged(pos, smsList.size)
    }
}
