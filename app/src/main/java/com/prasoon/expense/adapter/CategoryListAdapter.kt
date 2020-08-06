package com.prasoon.expense.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.prasoon.expense.R
import com.prasoon.expense.adapter.CategoryListAdapter.*
import com.prasoon.expense.model.Budget
import com.prasoon.expense.model.Category
import com.prasoon.expense.utils.getIcon
import kotlinx.android.synthetic.main.item_category.view.*
import java.util.*
import kotlin.collections.ArrayList

class CategoryListAdapter(
    private val categoryList: ArrayList<Category>,
    private inline val onEditCategoryClicked: (category: Category) -> Unit,
    private inline val onCategoryNameClicked: (category: Category) -> Unit
) :
    RecyclerView.Adapter<ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        internal val card = view.categoryCv
        internal val name = view.nameTv
        internal val icon = view.categoryIv
        internal val amount = view.expenseAmountTv
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder = ViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.item_category, parent, false)
    )

    override fun getItemCount(): Int {
        return categoryList.size
    }

    @ExperimentalStdlibApi
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val category = categoryList[position]

        holder.name.text = category.name.capitalize(Locale.ROOT)
        holder.amount.text =
            holder.itemView.context.getString(R.string.rs).plus(category.totalExpense)
        holder.icon.setImageResource(category.getIcon())

        holder.card.setOnClickListener { onCategoryNameClicked.invoke(category) }
        holder.card.setOnLongClickListener {
            holder.card.isChecked = !holder.card.isChecked
            if (holder.card.isChecked) {
                onEditCategoryClicked.invoke(category)
            }
            true
        }
    }
}