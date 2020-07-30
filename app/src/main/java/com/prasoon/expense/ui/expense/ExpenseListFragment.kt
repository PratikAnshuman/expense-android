package com.prasoon.expense.ui.expense

import android.content.DialogInterface
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.prasoon.expense.ExpenseViewModelFactory
import com.prasoon.expense.R
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputLayout
import com.prasoon.expense.MainActivity
import com.prasoon.expense.adapter.ExpenseListAdapter
import com.prasoon.expense.model.ExpenseItem
import com.prasoon.expense.utils.hideKeyboard
import com.prasoon.expense.utils.showKeyboard
import com.prasoon.expense.utils.showToast
import kotlinx.android.synthetic.main.fragment_expense_list.*
import kotlinx.android.synthetic.main.fragment_expense_list.emptyAnimCl
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.layout_add_expense.*

//private const val TAG = "ExpenseListFragment"

class ExpenseListFragment : Fragment() {

    private val args: ExpenseListFragmentArgs by navArgs()

    private lateinit var expenseListViewModel: ExpenseListViewModel
    private lateinit var alertDialog: AlertDialog
    private lateinit var amountInputLayout: TextInputLayout
    private lateinit var noteInputLayout: TextInputLayout

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        expenseListViewModel =
            ExpenseViewModelFactory(activity!!.application).create(ExpenseListViewModel::class.java)
        setHasOptionsMenu(true)

        return inflater.inflate(R.layout.fragment_expense_list, container, false)
    }

    @ExperimentalStdlibApi
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        (activity as MainActivity).toolbar.visibility = View.VISIBLE

        val category = args.title
        activity?.toolbar?.title = category.capitalize(Locale.ROOT)
        activity?.toolbar?.setNavigationOnClickListener { activity?.onBackPressed() }

        createFab.setOnClickListener { expenseListViewModel.onAddExpensePressed() }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        expenseListViewModel.onFragmentLoaded(args.id)

        expenseListViewModel.expenseItemList.observe(viewLifecycleOwner, Observer { expenseList ->
            expenseRv.layoutManager = LinearLayoutManager(context)
            expenseRv.adapter = ExpenseListAdapter(expenseList) {
                expenseListViewModel.onEditExpense(it)
            }
        })

        expenseListViewModel.showEmptyAnimation.observe(viewLifecycleOwner, Observer {
            if (it) emptyAnimCl.visibility = View.VISIBLE else emptyAnimCl.visibility = View.GONE
        })

        expenseListViewModel.showToast.observe(viewLifecycleOwner, Observer {
            this.showToast(it)
        })

        expenseListViewModel.showEditExpenseDialog.observe(viewLifecycleOwner, Observer {
            showEditExpenseDialog(it)
        })

        expenseListViewModel.showKeyboard.observe(viewLifecycleOwner, Observer {
            if (it) amountInputLayout.showKeyboard() else amountInputLayout.hideKeyboard()
        })

        expenseListViewModel.showAlert.observe(viewLifecycleOwner, Observer {
            if (it) showAddExpenseDialog() else alertDialog.dismiss()
        })

        expenseListViewModel.showAddExpenseError.observe(viewLifecycleOwner, Observer {
            amountInputLayout.error = it
        })
    }

    private fun showEditExpenseDialog(expenseItem: ExpenseItem) {
        val builder = MaterialAlertDialogBuilder(context!!)
        builder.setTitle("Edit Expense")
        builder.setView(R.layout.layout_add_expense)

        builder.setPositiveButton("Update Expense") { _, _ -> }
        builder.setNeutralButton("Cancel") { _, _ ->
        }
        builder.setNegativeButton("Delete Expense") { _, _ ->
//            expenseListViewModel.onDeleteExpense(expenseItem.id)
            expenseListViewModel.cancelAlertPressed()
        }
        alertDialog = builder.create()
        alertDialog.show()

        alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener {
            val amount = amountInputLayout.editText?.text.toString()
            val note = noteInputLayout.editText?.text.toString()
            expenseListViewModel.updateExpensePressed(
                amount,
                note,
                expenseItem.id,
                expenseItem.amount
            )
        }

        amountInputLayout = alertDialog.addAmountTil
        noteInputLayout = alertDialog.addNoteTil
        amountInputLayout.showKeyboard()
        amountInputLayout.editText?.setText(expenseItem.amount.toString())
        noteInputLayout.editText?.setText(expenseItem.note)
    }

    private fun showAddExpenseDialog() {
        val builder = MaterialAlertDialogBuilder(context!!)
        builder.setTitle("Add Expense")
        builder.setView(R.layout.layout_add_expense)

        builder.setPositiveButton("Confirm") { _, _ -> }
        builder.setNegativeButton("Cancel") { _, _ ->
            expenseListViewModel.cancelAlertPressed()
        }
        alertDialog = builder.create()
        alertDialog.show()

        alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener {
            val amount = amountInputLayout.editText?.text.toString()
            val note = noteInputLayout.editText?.text.toString()
            expenseListViewModel.confirmExpensePressed(amount, note)
        }

        amountInputLayout = alertDialog.addAmountTil
        noteInputLayout = alertDialog.addNoteTil
        amountInputLayout.showKeyboard()
    }
}