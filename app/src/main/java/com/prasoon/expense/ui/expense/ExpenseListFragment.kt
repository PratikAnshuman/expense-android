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
import com.google.android.material.textfield.TextInputLayout
import com.prasoon.expense.adapter.ExpenseListAdapter
import com.prasoon.expense.utils.hideKeyboard
import com.prasoon.expense.utils.showKeyboard
import com.prasoon.expense.utils.showToast
import kotlinx.android.synthetic.main.fragment_expense_list.*
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

        val category = args.title
        activity?.toolbar?.title = category.capitalize(Locale.ROOT)
        activity?.toolbar?.setNavigationOnClickListener { activity?.onBackPressed() }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        expenseListViewModel.onFragmentLoaded(args.id)

        expenseListViewModel.expenseItemList.observe(viewLifecycleOwner, Observer { expenseList ->
            expenseRv.layoutManager = LinearLayoutManager(context)
            expenseRv.adapter = ExpenseListAdapter(expenseList)
        })

        expenseListViewModel.showToast.observe(viewLifecycleOwner, Observer {
            this.showToast(it)
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

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_main, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.add -> expenseListViewModel.onAddExpensePressed()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun showAddExpenseDialog() {
        val builder = AlertDialog.Builder(context!!)
        builder.setTitle("Add Category")
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