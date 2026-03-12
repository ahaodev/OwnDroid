package com.bintianqi.owndroid.utils

import android.Manifest
import android.app.admin.ConnectEvent
import android.app.admin.DevicePolicyManager
import android.app.admin.DnsEvent
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageInstaller
import android.os.Binder
import android.os.Build.VERSION
import android.os.UserHandle
import android.os.UserManager
import android.util.Log
import androidx.annotation.RequiresApi
import com.bintianqi.owndroid.MyApplication
import com.bintianqi.owndroid.PrivilegeHelper
import com.bintianqi.owndroid.R
import com.bintianqi.owndroid.feature.network.NetworkLog
import com.bintianqi.owndroid.feature.settings.SettingsRepository
import com.bintianqi.owndroid.feature.users.UserOperationType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

data class PermissionItem(
    val id: String,
    val label: Int,
    val icon: Int,
    val profileOwnerRestricted: Boolean = false,
    val requiresApi: Int = 23
)

@Suppress("InlinedApi")
val runtimePermissions = listOf(
    PermissionItem(Manifest.permission.POST_NOTIFICATIONS, R.string.permission_POST_NOTIFICATIONS, R.drawable.notifications_fill0, requiresApi = 33),
    PermissionItem(Manifest.permission.READ_EXTERNAL_STORAGE, R.string.permission_READ_EXTERNAL_STORAGE, R.drawable.folder_fill0),
    PermissionItem(Manifest.permission.WRITE_EXTERNAL_STORAGE, R.string.permission_WRITE_EXTERNAL_STORAGE, R.drawable.folder_fill0),
    PermissionItem(Manifest.permission.READ_MEDIA_AUDIO, R.string.permission_READ_MEDIA_AUDIO, R.drawable.music_note_fill0, requiresApi = 33),
    PermissionItem(Manifest.permission.READ_MEDIA_VIDEO, R.string.permission_READ_MEDIA_VIDEO, R.drawable.movie_fill0, requiresApi = 33),
    PermissionItem(Manifest.permission.READ_MEDIA_IMAGES, R.string.permission_READ_MEDIA_IMAGES, R.drawable.image_fill0, requiresApi = 33),
    PermissionItem(Manifest.permission.CAMERA, R.string.permission_CAMERA, R.drawable.photo_camera_fill0, true),
    PermissionItem(Manifest.permission.RECORD_AUDIO, R.string.permission_RECORD_AUDIO, R.drawable.mic_fill0, true),
    PermissionItem(Manifest.permission.ACCESS_COARSE_LOCATION, R.string.permission_ACCESS_COARSE_LOCATION, R.drawable.location_on_fill0, true),
    PermissionItem(Manifest.permission.ACCESS_FINE_LOCATION, R.string.permission_ACCESS_FINE_LOCATION, R.drawable.location_on_fill0, true),
    PermissionItem(Manifest.permission.ACCESS_BACKGROUND_LOCATION, R.string.permission_ACCESS_BACKGROUND_LOCATION, R.drawable.location_on_fill0, true, 29),
    PermissionItem(Manifest.permission.READ_CONTACTS, R.string.permission_READ_CONTACTS, R.drawable.contacts_fill0),
    PermissionItem(Manifest.permission.WRITE_CONTACTS, R.string.permission_WRITE_CONTACTS, R.drawable.contacts_fill0),
    PermissionItem(Manifest.permission.READ_CALENDAR, R.string.permission_READ_CALENDAR, R.drawable.calendar_month_fill0),
    PermissionItem(Manifest.permission.WRITE_CALENDAR, R.string.permission_WRITE_CALENDAR, R.drawable.calendar_month_fill0),
    PermissionItem(Manifest.permission.BLUETOOTH_CONNECT, R.string.permission_BLUETOOTH_CONNECT, R.drawable.bluetooth_fill0, requiresApi = 31),
    PermissionItem(Manifest.permission.BLUETOOTH_SCAN, R.string.permission_BLUETOOTH_SCAN, R.drawable.bluetooth_searching_fill0, requiresApi = 31),
    PermissionItem(Manifest.permission.BLUETOOTH_ADVERTISE, R.string.permission_BLUETOOTH_ADVERTISE, R.drawable.bluetooth_fill0, requiresApi = 31),
    PermissionItem(Manifest.permission.NEARBY_WIFI_DEVICES, R.string.permission_NEARBY_WIFI_DEVICES, R.drawable.wifi_fill0, requiresApi = 33),
    PermissionItem(Manifest.permission.CALL_PHONE, R.string.permission_CALL_PHONE, R.drawable.call_fill0),
    PermissionItem(Manifest.permission.ANSWER_PHONE_CALLS, R.string.permission_ANSWER_PHONE_CALLS, R.drawable.call_fill0, requiresApi = 26),
    PermissionItem(Manifest.permission.READ_PHONE_NUMBERS, R.string.permission_READ_PHONE_STATE, R.drawable.mobile_phone_fill0, requiresApi = 26),
    PermissionItem(Manifest.permission.READ_PHONE_STATE, R.string.permission_READ_PHONE_STATE, R.drawable.mobile_phone_fill0),
    PermissionItem(Manifest.permission.USE_SIP, R.string.permission_USE_SIP, R.drawable.call_fill0),
    PermissionItem(Manifest.permission.UWB_RANGING, R.string.permission_UWB_RANGING, R.drawable.cell_tower_fill0, requiresApi = 31),
    PermissionItem(Manifest.permission.READ_SMS, R.string.permission_READ_SMS, R.drawable.sms_fill0),
    PermissionItem(Manifest.permission.RECEIVE_SMS, R.string.permission_RECEIVE_SMS, R.drawable.sms_fill0),
    PermissionItem(Manifest.permission.SEND_SMS, R.string.permission_SEND_SMS, R.drawable.sms_fill0),
    PermissionItem(Manifest.permission.READ_CALL_LOG, R.string.permission_READ_CALL_LOG, R.drawable.call_log_fill0),
    PermissionItem(Manifest.permission.WRITE_CALL_LOG, R.string.permission_WRITE_CALL_LOG, R.drawable.call_log_fill0),
    PermissionItem(Manifest.permission.RECEIVE_WAP_PUSH, R.string.permission_RECEIVE_WAP_PUSH, R.drawable.wifi_fill0),
    PermissionItem(Manifest.permission.BODY_SENSORS, R.string.permission_BODY_SENSORS, R.drawable.sensors_fill0, true),
    PermissionItem(Manifest.permission.BODY_SENSORS_BACKGROUND, R.string.permission_BODY_SENSORS_BACKGROUND, R.drawable.sensors_fill0, requiresApi = 33),
    PermissionItem(Manifest.permission.ACTIVITY_RECOGNITION, R.string.permission_ACTIVITY_RECOGNITION, R.drawable.history_fill0, true, 29)
).filter { VERSION.SDK_INT >= it.requiresApi }

@RequiresApi(26)
fun retrieveNetworkLogs(app: MyApplication, token: Long) {
    CoroutineScope(Dispatchers.IO).launch {
        val ph = app.container.privilegeHelper
        val logs = ph.myDpm.retrieveNetworkLogs(ph.myDar, token)?.mapNotNull {
            when (it) {
                is DnsEvent -> NetworkLog(
                    if (VERSION.SDK_INT >= 28) it.id else null, it.packageName, it.timestamp, "dns",
                    it.hostname, it.totalResolvedAddressCount,
                    it.inetAddresses.mapNotNull { address -> address.hostAddress }, null, null
                )
                is ConnectEvent -> NetworkLog(
                    if (VERSION.SDK_INT >= 28) it.id else null, it.packageName, it.timestamp,
                    "connect", null, null, null, it.inetAddress.hostAddress, it.port
                )
                else -> null
            }
        }
        if (logs.isNullOrEmpty()) return@launch
        app.container.networkLoggingRepo.writeNetworkLogs(logs)
        NotificationUtils.sendBasicNotification(
            app, NotificationType.NetworkLogsCollected,
            app.getString(R.string.n_logs_in_total, logs.size)
        )
    }
}

val activateOrgProfileCommand = "dpm mark-profile-owner-on-organization-owned-device --user " +
        "${Binder.getCallingUid() / 100000} com.bintianqi.owndroid/com.bintianqi.owndroid.Receiver"

@RequiresApi(24)
fun retrieveSecurityLogs(app: MyApplication) {
    CoroutineScope(Dispatchers.IO).launch {
        val ph = app.container.privilegeHelper
        val logs = ph.myDpm.retrieveSecurityLogs(ph.myDar)
        if (logs.isNullOrEmpty()) return@launch
        app.container.securityLoggingRepo.writeSecurityLogs(logs)
        NotificationUtils.sendBasicNotification(
            app, NotificationType.SecurityLogsCollected,
            app.getString(R.string.n_logs_in_total, logs.size)
        )
    }
}

fun setDefaultAffiliationID(ph: PrivilegeHelper, sr: SettingsRepository) {
    if (VERSION.SDK_INT >= 26 && !sr.data.privilege.defaultAffiliationIdSet) {
        try {
            ph.dpm.setAffiliationIds(ph.dar, setOf("OwnDroid_default_affiliation_id"))
            sr.update { it.privilege.defaultAffiliationIdSet = true }
            Log.d("DPM", "Default affiliation id set")
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}

fun parsePackageInstallerMessage(context: Context, result: Intent): String {
    val status = result.getIntExtra(PackageInstaller.EXTRA_STATUS, 999)
    val statusMessage = result.getStringExtra(PackageInstaller.EXTRA_STATUS_MESSAGE)
    val otherPackageName = result.getStringExtra(PackageInstaller.EXTRA_OTHER_PACKAGE_NAME)
    return when(status) {
        PackageInstaller.STATUS_FAILURE_BLOCKED ->
            context.getString(
                R.string.status_failure_blocked,
                otherPackageName ?: context.getString(R.string.unknown)
            )
        PackageInstaller.STATUS_FAILURE_ABORTED ->
            context.getString(R.string.status_failure_aborted)
        PackageInstaller.STATUS_FAILURE_INVALID ->
            context.getString(R.string.status_failure_invalid)
        PackageInstaller.STATUS_FAILURE_CONFLICT ->
            context.getString(R.string.status_failure_conflict, otherPackageName ?: "???")
        PackageInstaller.STATUS_FAILURE_STORAGE ->
            context.getString(R.string.status_failure_storage) +
                    result.getStringExtra(PackageInstaller.EXTRA_STORAGE_PATH).let { if(it == null) "" else "\n$it" }
        PackageInstaller.STATUS_FAILURE_INCOMPATIBLE ->
            context.getString(R.string.status_failure_incompatible)
        PackageInstaller.STATUS_FAILURE_TIMEOUT ->
            context.getString(R.string.timeout)
        else -> ""
    } + statusMessage.let { if(it == null) "" else "\n$it" }
}


fun handlePrivilegeChange(
    context: Context, ps: PrivilegeStatus, ph: PrivilegeHelper, sr: SettingsRepository
) {
    if (ps.activated) {
        ShortcutUtils.setAllShortcuts(context, sr, ph, true)
        setDefaultAffiliationID(ph, sr)
    } else {
        sr.update {
            it.privilege.defaultAffiliationIdSet = false
            it.apiKeyHash = ""
        }
        ShortcutUtils.setAllShortcuts(context, sr, ph, false)
    }
}

fun doUserOperationWithContext(
    context: Context, dpm: DevicePolicyManager, dar: ComponentName,
    type: UserOperationType, id: Int, isUserId: Boolean
): Boolean {
    val um = context.getSystemService(Context.USER_SERVICE) as UserManager
    val handle = if (isUserId && VERSION.SDK_INT >= 24) {
        UserHandle.getUserHandleForUid(id * 100000)
    } else {
        um.getUserForSerialNumber(id.toLong())
    }
    if (handle == null) return false
    return when (type) {
        UserOperationType.Start -> {
            if (VERSION.SDK_INT >= 28)
                dpm.startUserInBackground(dar, handle) == UserManager.USER_OPERATION_SUCCESS
            else false
        }
        UserOperationType.Switch -> dpm.switchUser(dar, handle)
        UserOperationType.Stop -> {
            if (VERSION.SDK_INT >= 28)
                dpm.stopUser(dar, handle) == UserManager.USER_OPERATION_SUCCESS
            else false
        }
        UserOperationType.Delete -> dpm.removeUser(dar, handle)
    }
}

const val ACTIVATE_DEVICE_OWNER_COMMAND = "dpm set-device-owner com.bintianqi.owndroid/.Receiver"

class PrivilegeStatus(
    val device: Boolean = false,
    val profile: Boolean = false,
    val work: Boolean = false,
    val org: Boolean = false,
    val affiliated: Boolean = false
) {
    val activated = device || profile
}

fun getPrivilegeStatus(ph: PrivilegeHelper): PrivilegeStatus {
    val profile = ph.dpm.isProfileOwnerApp(ph.dar.packageName)
    val work = profile && VERSION.SDK_INT >= 24 && ph.dpm.isManagedProfile(ph.dar)
    return PrivilegeStatus(
        device = ph.dpm.isDeviceOwnerApp(ph.dar.packageName),
        profile = profile,
        work = work,
        org = work && VERSION.SDK_INT >= 30 && ph.dpm.isOrganizationOwnedDeviceWithManagedProfile,
        affiliated = VERSION.SDK_INT >= 28 && ph.dpm.isAffiliatedUser
    )
}
