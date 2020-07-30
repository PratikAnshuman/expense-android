package com.prasoon.expense.ui.home

import android.content.DialogInterface
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputLayout
import com.prasoon.expense.ExpenseViewModelFactory
import com.prasoon.expense.MainActivity
import com.prasoon.expense.R
import com.prasoon.expense.adapter.HomeCategoryListAdapter
import com.prasoon.expense.utils.hideKeyboard
import com.prasoon.expense.utils.showKeyboard
import com.prasoon.expense.utils.showToast
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_category.categoryRv
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.layout_add_category.*
import kotlinx.android.synthetic.main.layout_home_budget.*


//private const val TAG = "CategoryFragment"

class HomeFragment : Fragment() {

    private lateinit var homeViewModel: HomeViewModel
    private lateinit var alertDialog: AlertDialog
    private lateinit var textInputLayout: TextInputLayout

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        homeViewModel =
            ExpenseViewModelFactory(activity!!.application).create(HomeViewModel::class.java)

        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        (activity as MainActivity).toolbar.visibility = View.GONE
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        homeViewModel.onFragmentLoaded()

        budgetAmountTv.setOnClickListener { homeViewModel.onSetBudget() }

        homeViewModel.categoryList.observe(viewLifecycleOwner, Observer { arrayList ->
            categoryRv.layoutManager = LinearLayoutManager(context)
            categoryRv.adapter = HomeCategoryListAdapter(arrayList) {
                homeViewModel.onCategoryNameClicked(it)
            }
        })

        homeViewModel.showDialog.observe(viewLifecycleOwner, Observer {
            if (it) showBudgetDialog() else alertDialog.dismiss()
        })

        homeViewModel.showEmptyAnimation.observe(viewLifecycleOwner, Observer {
            if (it) categoryCv.visibility = View.GONE else categoryCv.visibility = View.VISIBLE
        })

        homeViewModel.currentBudget.observe(viewLifecycleOwner, Observer {
            monthTv.text = it.monthName
            budgetAmountTv.text = getString(R.string.rs).plus(it.budgetAmount)
            expenseAmountTv.text = getString(R.string.rs).plus(it.expenseAmount.toString())
            balanceAmountTv.text = getString(R.string.rs).plus(it.balanceAmount.toString())
        })

        homeViewModel.showKeyboard.observe(viewLifecycleOwner, Observer {
            if (it) textInputLayout.showKeyboard() else textInputLayout.hideKeyboard()
        })

        homeViewModel.showToast.observe(viewLifecycleOwner, Observer {
            this.showToast(it)
        })

        homeViewModel.budgetAmount.observe(viewLifecycleOwner, Observer {
            budgetAmountTv.text = getString(R.string.rs).plus(it)
        })

        homeViewModel.showUpdateBudgetError.observe(viewLifecycleOwner, Observer {
            textInputLayout.error = it
        })

        homeViewModel.navigateToCategoryExpenseList.observe(viewLifecycleOwner, Observer {
            val action = HomeFragmentDirections.actionShowExpense(it.name, it.id)
            Navigation.findNavController(view).navigate(action)
        })
    }

    private fun showBudgetDialog() {
        val builder = MaterialAlertDialogBuilder(context!!)
        builder.setTitle("Set Monthly Budget")
        builder.setView(R.layout.layout_add_category)

        builder.setPositiveButton("Update Budget") { _, _ -> }

        builder.setNegativeButton("Cancel") { _, _ ->
            homeViewModel.onCancelDialog()
        }
        alertDialog = builder.create()
        alertDialog.show()

        alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener {
            homeViewModel.onUpdateBudget(textInputLayout.editText?.text.toString())
        }

        textInputLayout = alertDialog.addCategoryTil
        textInputLayout.hint = "Enter Budget Amount"
        textInputLayout.showKeyboard()
    }

}

