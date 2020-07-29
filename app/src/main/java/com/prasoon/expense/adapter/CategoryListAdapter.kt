package com.prasoon.expense.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.prasoon.expense.R
import com.prasoon.expense.adapter.CategoryListAdapter.*
import com.prasoon.expense.model.Category
import kotlinx.android.synthetic.main.item_category.view.*

class CategoryListAdapter(
    private val categoryList: ArrayList<Category>,
    private inline val onEditCategoryClicked: (category: Category) -> Unit,
    private inline val onCategoryNameClicked: (category: Category) -> Unit
) :
    RecyclerView.Adapter<ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        internal val name = view.categoryTv
        internal val delete = view.deleteIv
        internal val amount = view.expenseAmountTv

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder = ViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.item_category, parent, false)
    )

    override fun getItemCount(): Int {
        return categoryList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val category = categoryList[position]

        holder.name.text = category.name
        holder.amount.text = category.totalExpense.toString()

        holder.delete.setOnClickListener { onEditCategoryClicked.invoke(category) }
        holder.name.setOnClickListener { onCategoryNameClicked.invoke(category) }
    }
}