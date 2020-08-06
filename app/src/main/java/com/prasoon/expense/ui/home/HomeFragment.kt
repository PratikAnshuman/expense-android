package com.prasoon.expense.ui.home

import android.content.DialogInterface
import android.os.Bundle
import android.text.InputType
import android.view.*
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputLayout
import com.prasoon.expense.MainActivity
import com.prasoon.expense.R
import com.prasoon.expense.adapter.HomeCategoryListAdapter
import com.prasoon.expense.utils.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.layout_add_category.*
import kotlinx.android.synthetic.main.layout_home_budget.*

//private const val TAG = "CategoryFragment"

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private val homeViewModel: HomeViewModel by viewModels()
    private lateinit var alertDialog: AlertDialog
    private lateinit var textInputLayout: TextInputLayout

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
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
            notificationsRv.layoutManager = LinearLayoutManager(context)
            notificationsRv.adapter = HomeCategoryListAdapter(arrayList) {
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
            budgetAmountTv.setAmount(it.budgetAmount)
            expenseAmountTv.setAmount(it.expenseAmount)
            balanceAmountTv.setAmount(it.balanceAmount)
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

        homeViewModel.navigateToCategoryExpenseList.observe(viewLifecycleOwner, EventObserver {
            val action = HomeFragmentDirections.actionShowExpense(it.name, it.id)
            Navigation.findNavController(view).navigate(action)
        })
    }

    private fun showBudgetDialog() {
        val builder = MaterialAlertDialogBuilder(requireContext())
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
        textInputLayout.editText?.inputType = InputType.TYPE_CLASS_NUMBER
        textInputLayout.showKeyboard()
    }

}

