package com.prasoon.expense.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.prasoon.expense.R
import com.prasoon.expense.model.ExpenseItem
import com.prasoon.expense.utils.convertLongToTime
import kotlinx.android.synthetic.main.item_expense.view.*
import java.util.*
import kotlin.collections.ArrayList

class ExpenseListAdapter(
    private val expenseList: ArrayList<ExpenseItem>,
    private inline val onEditExpenseClicked: (expenseItem: ExpenseItem) -> Unit
) :
    RecyclerView.Adapter<ExpenseListAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        internal val amount = view.expenseAmountTv
        internal val note = view.noteTv
        internal val edit = view.editIv
        internal val date = view.dateTv
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder = ViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.item_expense, parent, false)
    )

    override fun getItemCount(): Int {
        return expenseList.size
    }

    @ExperimentalStdlibApi
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val expense = expenseList[position]
        holder.amount.text =
            holder.itemView.context.getString(R.string.rs).plus(expense.amount)

        holder.note.text = expense.note.capitalize(Locale.ROOT)
        holder.date.text = expense.id.convertLongToTime()

        holder.edit.setOnClickListener { onEditExpenseClicked.invoke(expense) }
    }
}