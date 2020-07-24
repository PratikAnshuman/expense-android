package com.prasoon.expense.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.prasoon.expense.R
import com.prasoon.expense.model.ExpenseItem
import com.prasoon.expense.utils.convertLongToTime
import kotlinx.android.synthetic.main.item_category.view.deleteIv
import kotlinx.android.synthetic.main.item_expense.view.*

class ExpenseListAdapter(
    private val expenseList: ArrayList<ExpenseItem>
//    inline val onDeleteCategoryClicked: (id: Long) -> Unit,
//    inline val onCategoryNameClicked: (name: String) -> Unit
) :
    RecyclerView.Adapter<ExpenseListAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        internal val amount = view.amountTv
        internal val note = view.noteTv
        internal val delete = view.deleteIv
        internal val date = view.dateTv
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder = ViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.item_expense, parent, false)
    )

    override fun getItemCount(): Int {
        return expenseList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val expense = expenseList[position]
        holder.amount.text = expense.amount.toString()
        holder.note.text = expense.note
        holder.date.text = expense.id.convertLongToTime()

//        holder.delete.setOnClickListener { onDeleteCategoryClicked.invoke(category.id) }
//        holder.name.setOnClickListener { onCategoryNameClicked.invoke(category.name) }
    }
}