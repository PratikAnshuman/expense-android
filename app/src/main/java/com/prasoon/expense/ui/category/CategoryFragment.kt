package com.prasoon.expense.ui.category

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputLayout
import com.prasoon.expense.MainActivity
import com.prasoon.expense.R
import com.prasoon.expense.adapter.CategoryListAdapter
import com.prasoon.expense.model.Category
import com.prasoon.expense.utils.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_category.*
import kotlinx.android.synthetic.main.layout_add_category.*


//private const val TAG = "CategoryFragment"

@AndroidEntryPoint
class CategoryFragment : Fragment() {

    private val args: CategoryFragmentArgs by navArgs()

    private val categoryViewModel: CategoryViewModel by viewModels()
    private lateinit var alertDialog: AlertDialog
    private lateinit var textInputLayout: TextInputLayout

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_category, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        (activity as MainActivity).toolbar.visibility = View.GONE
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        categoryViewModel.onFragmentLoaded()

        if (args.notificationId != -1L) {
            categoryViewModel.isFromNotification(args.notificationId)
        }

        createFab.setOnClickListener { categoryViewModel.onAddCategory() }

        categoryViewModel.categoryList.observe(viewLifecycleOwner, EventObserver { arrayList ->
            categoryRv.layoutManager =
                GridLayoutManager(context, 2)
            categoryRv.adapter = CategoryListAdapter(arrayList, {
                categoryViewModel.onEditCategoryPressed(it)
            }, {
                categoryViewModel.onCategoryNameClicked(it)
            })
        })

        categoryViewModel.showEmptyAnimation.observe(viewLifecycleOwner, EventObserver {
            if (it) emptyAnimCl.visibility = View.VISIBLE else emptyAnimCl.visibility = View.GONE
        })

        categoryViewModel.showAddCategoryError.observe(viewLifecycleOwner, EventObserver {
            textInputLayout.error = it
        })

        categoryViewModel.showToast.observe(viewLifecycleOwner, EventObserver {
            this.showToast(it)
        })

        categoryViewModel.showKeyboard.observe(viewLifecycleOwner, EventObserver {
            if (it) textInputLayout.showKeyboard() else textInputLayout.hideKeyboard()
        })

        categoryViewModel.showAlert.observe(viewLifecycleOwner, EventObserver {
            if (it) showAddCategoryDialog() else {
                alertDialog.dismiss()
            }
        })

        categoryViewModel.showEditDialog.observe(viewLifecycleOwner, EventObserver {
            showEditDialog(it)
        })

        categoryViewModel.navigateToCategoryExpenseList.observe(viewLifecycleOwner, EventObserver {
            val action = CategoryFragmentDirections.actionShowExpense(it.name, it.id)
            Navigation.findNavController(view).navigate(action)
        })

        categoryViewModel.navigateToNotification.observe(viewLifecycleOwner, EventObserver {
            val action =
                CategoryFragmentDirections.actionCategoryToNotification(it.first, it.second)
            Navigation.findNavController(view).navigate(action)
        })
    }

    private fun showEditDialog(category: Category) {
        val builder = MaterialAlertDialogBuilder(requireContext())
        builder.setTitle("Edit Category")
        builder.setView(R.layout.layout_add_category)

        builder.setPositiveButton("Update Category") { _, _ -> }
        builder.setNegativeButton("Delete Category") { _, _ ->
            categoryViewModel.onDeleteCategory(category)
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
        val builder = MaterialAlertDialogBuilder(requireContext())
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

