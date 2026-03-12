package com.bintianqi.owndroid.feature.privilege

import android.app.admin.DevicePolicyManager
import android.os.Build.VERSION
import androidx.lifecycle.ViewModel
import com.bintianqi.owndroid.MyApplication
import com.bintianqi.owndroid.PrivilegeHelper
import com.bintianqi.owndroid.R
import com.bintianqi.owndroid.feature.settings.SettingsRepository
import com.bintianqi.owndroid.utils.ACTIVATE_DEVICE_OWNER_COMMAND
import com.bintianqi.owndroid.utils.AdbLocalClient
import com.bintianqi.owndroid.utils.MyAdminComponent
import com.bintianqi.owndroid.utils.PrivilegeStatus
import com.bintianqi.owndroid.utils.ToastChannel
import com.bintianqi.owndroid.utils.getPrivilegeStatus
import com.bintianqi.owndroid.utils.handlePrivilegeChange
import com.topjohnwu.superuser.Shell
import kotlinx.coroutines.flow.MutableStateFlow

class WorkingModesViewModel(
    val application: MyApplication, val ph: PrivilegeHelper, val sr: SettingsRepository,
    val ps: MutableStateFlow<PrivilegeStatus>, val toastChannel: ToastChannel
) : ViewModel() {

    fun isCreatingWorkProfileAllowed(): Boolean {
        return if (VERSION.SDK_INT >= 24)
            ph.myDpm.isProvisioningAllowed(DevicePolicyManager.ACTION_PROVISION_MANAGED_PROFILE)
        else false
    }

    fun activateDoByRoot(callback: (Boolean, String?) -> Unit) {
        Shell.getShell { shell ->
            if (shell.isRoot) {
                val result = Shell.cmd(ACTIVATE_DEVICE_OWNER_COMMAND).exec()
                val output = result.out.joinToString("\n") + "\n" + result.err.joinToString("\n")
                if (result.isSuccess) updateStatus()
                callback(result.isSuccess, output.trim())
            } else {
                callback(false, application.getString(R.string.permission_denied))
            }
        }
    }

    fun activateDoByAdb(callback: (Boolean, String?) -> Unit) {
        Thread {
            try {
                val output = AdbLocalClient.exec(ACTIVATE_DEVICE_OWNER_COMMAND)
                updateStatus()
                callback(true, output)
            } catch (e: Exception) {
                callback(false, e.message)
            }
        }.start()
    }

    fun deactivate() {
        if (ps.value.device) {
            ph.myDpm.clearDeviceOwnerApp(application.packageName)
        } else if (VERSION.SDK_INT >= 24) {
            ph.myDpm.clearProfileOwner(MyAdminComponent)
        }
        updateStatus()
    }

    private fun updateStatus() {
        ps.value = getPrivilegeStatus(ph)
        handlePrivilegeChange(application, ps.value, ph, sr)
    }
}
