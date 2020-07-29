package com.prasoon.expense.ui.home

import android.content.DialogInterface
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.textfield.TextInputLayout
import com.prasoon.expense.ExpenseViewModelFactory
import com.prasoon.expense.MainActivity
import com.prasoon.expense.R
import com.prasoon.expense.adapter.CategoryListAdapter
import com.prasoon.expense.adapter.HomeCategoryListAdapter
import com.prasoon.expense.utils.hideKeyboard
import com.prasoon.expense.utils.showKeyboard
import com.prasoon.expense.utils.showToast
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_category.*
import kotlinx.android.synthetic.main.layout_add_category.*


//private const val TAG = "CategoryFragment"

class HomeFragment : Fragment() {

    private lateinit var homeViewModel: HomeViewModel

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
        setHasOptionsMenu(true)
        homeViewModel.onFragmentLoaded()

        homeViewModel.categoryList.observe(viewLifecycleOwner, Observer { arrayList ->
            categoryRv.layoutManager = LinearLayoutManager(context)
            categoryRv.adapter = HomeCategoryListAdapter(arrayList) {
                homeViewModel.onCategoryNameClicked(it)
            }
        })

        homeViewModel.navigateToCategoryExpenseList.observe(viewLifecycleOwner, Observer {
            val action = HomeFragmentDirections.actionShowExpense(it.name, it.id)
            Navigation.findNavController(view).navigate(action)
        })

    }

}

