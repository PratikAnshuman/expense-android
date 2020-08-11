package com.prasoon.expense.ui

import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.prasoon.expense.utils.getPermission
import com.prasoon.expense.utils.showToast

const val SMS_REQUEST_CODE = 1001

abstract class PermissionsView : Fragment() {

    enum class Permission {
        READ_SMS
    }

    fun checkPermission(requestedPermission: Permission): Boolean {
        val permission = requestedPermission.getPermission()
        when {
            ContextCompat.checkSelfPermission(
                requireContext(),
                permission
            ) == PackageManager.PERMISSION_GRANTED -> {
                onPermissionGranted()
                return true
            }
            shouldShowRequestPermissionRationale(permission)
            -> {
                // In an educational UI, explain to the user why your app requires this
                // permission for a specific feature to behave as expected. In this UI,
                // include a "cancel" or "no thanks" button that allows the user to
                // continue using your app without granting the permission.
                showAlert()
                return false
            }
            else -> {
                // You can directly ask for the permission.
                requestPermissions(
                    arrayOf(permission),
                    SMS_REQUEST_CODE
                )
                return false
            }
        }
    }

    private fun showAlert() {
        showToast("Please enable sms permission from settings to use this feature.")
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>, grantResults: IntArray
    ) {
        when (requestCode) {
            SMS_REQUEST_CODE -> {
                // If request is cancelled, the result arrays are empty.
                if ((grantResults.isNotEmpty() &&
                            grantResults[0] == PackageManager.PERMISSION_GRANTED)
                ) {
                    // Permission is granted. Continue the action or workflow
                    // in your app.
                    onPermissionGranted()
                } else {
                    // Explain to the user that the feature is unavailable because
                    // the features requires a permission that the user has denied.
                    // At the same time, respect the user's decision. Don't link to
                    // system settings in an effort to convince the user to change
                    // their decision.
                    onPermissionDenied()
                }
                return
            }

            // Add other 'when' lines to check for other
            // permissions this app might request.
            else -> {
                // Ignore all other requests.
            }
        }
    }

    abstract fun onPermissionGranted()
    abstract fun onPermissionDenied()

}