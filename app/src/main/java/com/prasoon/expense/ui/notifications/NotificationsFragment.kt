package com.prasoon.expense.ui.notifications

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.prasoon.expense.MainActivity
import com.prasoon.expense.R
import com.prasoon.expense.adapter.NotificationListAdapter
import com.prasoon.expense.model.Sms
import com.prasoon.expense.ui.PermissionsView
import com.prasoon.expense.utils.EventObserver
import com.prasoon.expense.utils.showToast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_notifications.*
import kotlin.collections.ArrayList


//private const val TAG = "NotificationsFragment"

@AndroidEntryPoint
class NotificationsFragment : PermissionsView() {

    private val notificationsViewModel: NotificationsViewModel by viewModels()
    private val args: NotificationsFragmentArgs by navArgs()
    private lateinit var notificationListAdapter: NotificationListAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_notifications, container, false)
    }

    override fun onPermissionGranted() {
        notificationsViewModel.onPermissionGranted()
    }

    override fun onPermissionDenied() {
        showToast("This feature won't work until sms permission is enabled")
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (args.categoryId != -1L) {
            notificationsViewModel.onReceivedFromCategory(args.categoryId, args.notificationId)
        }

        notificationsViewModel.onViewCreated()

        notificationsViewModel.checkPermission.observe(viewLifecycleOwner, EventObserver {
            checkPermission(Permission.READ_SMS)
        })

        notificationsViewModel.messageList.observe(viewLifecycleOwner, EventObserver { list ->
            if (list.isEmpty()) emptyAnimCl.visibility = View.VISIBLE
            else emptyAnimCl.visibility = View.GONE
            notificationsRv.layoutManager = LinearLayoutManager(context)
            notificationListAdapter = NotificationListAdapter(list as ArrayList<Sms>) {
                notificationsViewModel.onAddExpensePressed(it)
            }
            notificationsRv.adapter = notificationListAdapter
        })

        notificationsViewModel.navigateToCategory.observe(viewLifecycleOwner, EventObserver {
            val action = NotificationsFragmentDirections.actionNotificationToCategory(it)
            Navigation.findNavController(view).navigate(action)
        })

        notificationsViewModel.showToast.observe(viewLifecycleOwner, EventObserver {
            this.showToast(it)
        })

        notificationsViewModel.removeNotification.observe(viewLifecycleOwner, EventObserver {
            notificationListAdapter.removeNotification(it)
        })
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        (activity as MainActivity).notificationBadge.apply {
            isVisible = false
        }
    }

}