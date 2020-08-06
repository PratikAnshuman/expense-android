package com.prasoon.expense.adapter

import android.content.Context
import android.content.res.Resources
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.prasoon.expense.R
import com.prasoon.expense.model.Category
import com.prasoon.expense.utils.setAmount
import kotlinx.android.synthetic.main.item_category.view.*
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList

class HomeCategoryListAdapter(
    private val categoryList: ArrayList<Category>,
    private inline val onCategoryNameClicked: (category: Category) -> Unit
) :
    RecyclerView.Adapter<HomeCategoryListAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        internal val name = view.nameTv
        internal val amount = view.expenseAmountTv
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder = ViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.item_home_category, parent, false)
    )

    override fun getItemCount(): Int {
        return categoryList.size
    }

    @ExperimentalStdlibApi
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val category = categoryList[position]

        holder.name.text = category.name.capitalize(Locale.ROOT)
        holder.amount.setAmount(category.totalExpense)

        holder.name.setOnClickListener { onCategoryNameClicked.invoke(category) }
    }
}