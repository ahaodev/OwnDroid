package com.bintianqi.owndroid

import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Context

class PrivilegeHelper(val context: Context) {
    val myDpm = context.getSystemService(DevicePolicyManager::class.java)!!
    val myDar = ComponentName(context, Receiver::class.java)

    val dpm: DevicePolicyManager get() = myDpm
    val dar: ComponentName get() = myDar

    class SafeDpmCallScope(val dpm: DevicePolicyManager, val dar: ComponentName)

    fun safeDpmCall(block: SafeDpmCallScope.() -> Unit) {
        SafeDpmCallScope(dpm, dar).block()
    }
}
