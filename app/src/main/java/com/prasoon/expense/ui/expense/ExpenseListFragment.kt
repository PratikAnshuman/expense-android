package com.prasoon.expense.ui.expense

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputLayout
import com.prasoon.expense.MainActivity
import com.prasoon.expense.R
import com.prasoon.expense.adapter.ExpenseListAdapter
import com.prasoon.expense.model.ExpenseItem
import com.prasoon.expense.utils.hideKeyboard
import com.prasoon.expense.utils.showKeyboard
import com.prasoon.expense.utils.showToast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_expense_list.*
import kotlinx.android.synthetic.main.layout_add_expense.*
import kotlinx.android.synthetic.main.layout_empty_anim.*
import java.util.*


private const val TAG = "ExpenseListFragment"

@AndroidEntryPoint
class ExpenseListFragment : Fragment() {

    private val args: ExpenseListFragmentArgs by navArgs()

    private val expenseListViewModel: ExpenseListViewModel by viewModels()
    private lateinit var alertDialog: AlertDialog
    private lateinit var amountInputLayout: TextInputLayout
    private lateinit var noteInputLayout: TextInputLayout

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_expense_list, container, false)
    }

    @ExperimentalStdlibApi
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        (activity as MainActivity).toolbar.visibility = View.VISIBLE

        activity?.toolbar?.title = args.title.capitalize(Locale.ROOT)

        activity?.toolbar?.setNavigationOnClickListener {
            findNavController().popBackStack()
        }

        createFab.setOnClickListener { expenseListViewModel.onAddExpensePressed() }

        expenseRv.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (dy > 0 || dy < 0 && createFab.isShown) {
                    createFab.shrink()
                }
            }

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    createFab.extend()
                }
                super.onScrollStateChanged(recyclerView, newState)
            }
        })
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
            if (it) emptyAnimCl.apply {
                emptyTv1.text =
                    resources.getString(R.string.its_empty_expense1).plus(args.title)
                emptyTv2.setText(R.string.its_empty_expense2)
                visibility = View.VISIBLE
            }
            else emptyAnimCl.visibility = View.GONE
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
        val builder = MaterialAlertDialogBuilder(requireContext())
        builder.setTitle("Edit Expense")
        builder.setView(R.layout.layout_add_expense)

        builder.setPositiveButton("Update Expense") { _, _ -> }
        builder.setNeutralButton("Cancel") { _, _ ->
            expenseListViewModel.cancelAlertPressed()
        }
        builder.setNegativeButton("Delete Expense") { _, _ ->
            expenseListViewModel.onDeleteExpense(expenseItem)
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
        val builder = MaterialAlertDialogBuilder(requireContext())
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