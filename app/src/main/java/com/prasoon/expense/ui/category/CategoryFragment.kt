package com.prasoon.expense.ui.category

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
import com.prasoon.expense.adapter.CategoryListAdapter
import com.prasoon.expense.model.Category
import com.prasoon.expense.ui.home.HomeFragmentDirections
import com.prasoon.expense.utils.hideKeyboard
import com.prasoon.expense.utils.showKeyboard
import com.prasoon.expense.utils.showToast
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_category.*
import kotlinx.android.synthetic.main.layout_add_category.*


//private const val TAG = "CategoryFragment"

class CategoryFragment : Fragment() {

    private lateinit var categoryViewModel: CategoryViewModel
    private lateinit var alertDialog: AlertDialog
    private lateinit var textInputLayout: TextInputLayout

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        categoryViewModel =
            ExpenseViewModelFactory(activity!!.application).create(CategoryViewModel::class.java)

        return inflater.inflate(R.layout.fragment_category, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        (activity as MainActivity).toolbar.visibility = View.VISIBLE
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        categoryViewModel.onFragmentLoaded()

        createFab.setOnClickListener { categoryViewModel.onAddCategory() }

        categoryViewModel.categoryList.observe(viewLifecycleOwner, Observer { arrayList ->
            categoryRv.layoutManager = LinearLayoutManager(context)
            categoryRv.adapter = CategoryListAdapter(arrayList, {
                categoryViewModel.onEditCategoryPressed(it)
            }, {
                categoryViewModel.onCategoryNameClicked(it)
            })
        })

        categoryViewModel.showAddCategoryError.observe(viewLifecycleOwner, Observer {
            textInputLayout.error = it
        })

        categoryViewModel.showToast.observe(viewLifecycleOwner, Observer {
            this.showToast(it)
        })

        categoryViewModel.showKeyboard.observe(viewLifecycleOwner, Observer {
            if (it) textInputLayout.showKeyboard() else textInputLayout.hideKeyboard()
        })

        categoryViewModel.showAlert.observe(viewLifecycleOwner, Observer {
            if (it) showAddCategoryDialog() else alertDialog.dismiss()
        })

        categoryViewModel.showEditDialog.observe(viewLifecycleOwner, Observer {
            showEditDialog(it)
        })

        categoryViewModel.navigateToCategoryExpenseList.observe(viewLifecycleOwner, Observer {
            val action = HomeFragmentDirections.actionShowExpense(it.name, it.id)
            Navigation.findNavController(view).navigate(action)
        })
    }

    private fun showEditDialog(category: Category) {
        val builder = MaterialAlertDialogBuilder(context!!)
        builder.setTitle("Edit Category")
        builder.setView(R.layout.layout_add_category)

        builder.setPositiveButton("Update Category") { _, _ -> }
        builder.setNegativeButton("Delete Category") { _, _ ->
            categoryViewModel.onDeleteCategory(category.id)
        }
        builder.setNeutralButton("Cancel") { _, _ ->
            categoryViewModel.onCancelDialog()
        }
        alertDialog = builder.create()
        alertDialog.show()

        alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener {
            categoryViewModel.onUpdateCategory(
                textInputLayout.editText?.text.toString(),
                category.id
            )
        }

        textInputLayout = alertDialog.addCategoryTil
        textInputLayout.editText?.setText(category.name)
        textInputLayout.showKeyboard()
    }

    private fun showAddCategoryDialog() {
        val builder = MaterialAlertDialogBuilder(context!!)
        builder.setTitle("Add Category")
        builder.setView(R.layout.layout_add_category)

        builder.setPositiveButton("Confirm") { _, _ -> }
        builder.setNegativeButton("Cancel") { _, _ ->
            categoryViewModel.onCancelDialog()
        }
        alertDialog = builder.create()
        alertDialog.show()

        alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener {
            val categoryName = textInputLayout.editText?.text.toString()
            categoryViewModel.confirmCategoryPressed(categoryName)
        }

        textInputLayout = alertDialog.addCategoryTil
        textInputLayout.showKeyboard()
    }
}

