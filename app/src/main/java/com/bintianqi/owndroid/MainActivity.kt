package com.bintianqi.owndroid

import android.Manifest
import android.content.BroadcastReceiver
import android.content.pm.PackageManager
import android.os.Build.VERSION
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.bintianqi.owndroid.feature.applications.AppChooserViewModel
import com.bintianqi.owndroid.ui.NavTransition
import com.bintianqi.owndroid.ui.navigation.Destination
import com.bintianqi.owndroid.ui.navigation.myEntryProvider
import com.bintianqi.owndroid.ui.navigation.rememberSharedViewModelStoreNavEntryDecorator
import com.bintianqi.owndroid.ui.screen.AppLockDialog
import com.bintianqi.owndroid.ui.theme.OwnDroidTheme
import com.bintianqi.owndroid.utils.AdbLocalClient
import com.bintianqi.owndroid.utils.ACTIVATE_DEVICE_OWNER_COMMAND
import com.bintianqi.owndroid.utils.getPrivilegeStatus
import com.bintianqi.owndroid.utils.popToast
import com.bintianqi.owndroid.utils.registerPackageRemovedReceiver
import com.bintianqi.owndroid.utils.viewModelFactory
import com.topjohnwu.superuser.Shell
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@ExperimentalMaterial3Api
class MainActivity : FragmentActivity() {
    private var packageRemovedReceiver: BroadcastReceiver? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        val context = this
        val myApp = (application as MyApplication)
        val settingsRepo = myApp.container.settingsRepo
        if (
            VERSION.SDK_INT >= 33 &&
            checkSelfPermission(
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            val launcher = registerForActivityResult(ActivityResultContracts.RequestPermission()) {}
            launcher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
        val appChooserVm: AppChooserViewModel by viewModels(
            factoryProducer = {
                viewModelFactory { AppChooserViewModel(myApp) }
            }
        )
        packageRemovedReceiver = registerPackageRemovedReceiver(this) {
            appChooserVm.onPackageRemoved(it)
        }
        if (
            myApp.container.privilegeState.value.work &&
            !settingsRepo.data.privilege.managedProfileActivated
        ) {
            myApp.container.privilegeHelper.dpm.setProfileEnabled(
                myApp.container.privilegeHelper.dar
            )
            settingsRepo.update {
                it.privilege.managedProfileActivated = true
            }
            context.popToast(R.string.work_profile_activated)
        }
        lifecycleScope.launch {
            while (true) {
                val text = myApp.container.toastChannel.channel.receive()
                context.popToast(text)
            }
        }
        setContent {
            var appLockDialog by rememberSaveable { mutableStateOf(false) }
            val theme by myApp.container.themeState.collectAsState()
            OwnDroidTheme(theme) {
                Box(
                    Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background)
                )
                val backstack = rememberNavBackStack(Destination.Home)
                LaunchedEffect(Unit) {
                    if (!myApp.container.privilegeState.value.activated) {
                        val rootResult = withContext(Dispatchers.IO) {
                            try { Shell.cmd(ACTIVATE_DEVICE_OWNER_COMMAND).exec() } catch (e: Exception) { null }
                        }
                        val activated = if (rootResult?.isSuccess == true) {
                            true
                        } else {
                            withContext(Dispatchers.IO) {
                                try { AdbLocalClient.exec(ACTIVATE_DEVICE_OWNER_COMMAND); true }
                                catch (e: Exception) { false }
                            }
                        }
                        if (activated) {
                            myApp.container.privilegeState.value = getPrivilegeStatus(myApp.container.privilegeHelper)
                        } else {
                            backstack.add(Destination.WorkingModes(false))
                            backstack.removeFirstOrNull()
                        }
                    }
                }
                NavDisplay(
                    backstack,
                    onBack = {
                        backstack.removeLastOrNull()
                    },
                    entryDecorators = listOf(
                        rememberSaveableStateHolderNavEntryDecorator(),
                        rememberSharedViewModelStoreNavEntryDecorator()
                    ),
                    transitionSpec = {
                        NavTransition.transition
                    },
                    popTransitionSpec = {
                        NavTransition.popTransition
                    },
                    predictivePopTransitionSpec = {
                        NavTransition.popTransition
                    }
                ) {
                    myEntryProvider(it as Destination, backstack, appChooserVm, myApp.container)
                }
                val lifecycleOwner = LocalLifecycleOwner.current
                if (appLockDialog) {
                    AppLockDialog(
                        myApp.container.settingsRepo.data.appLock, { appLockDialog = false }
                    ) { moveTaskToBack(true) }
                }
                DisposableEffect(lifecycleOwner) {
                    val observer = LifecycleEventObserver { _, event ->
                        if (
                            settingsRepo.data.appLock.passwordHash.isNotEmpty() &&
                            (event == Lifecycle.Event.ON_CREATE ||
                                    (event == Lifecycle.Event.ON_RESUME &&
                                            settingsRepo.data.appLock.lockWhenLeaving))
                        ) {
                            appLockDialog = true
                        }
                    }
                    lifecycleOwner.lifecycle.addObserver(observer)
                    onDispose {
                        lifecycleOwner.lifecycle.removeObserver(observer)
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        packageRemovedReceiver?.let { unregisterReceiver(it) }
        packageRemovedReceiver = null
    }
}
