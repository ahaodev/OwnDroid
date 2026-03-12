package com.bintianqi.owndroid.feature.privilege

import android.os.Build.VERSION
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bintianqi.owndroid.R
import com.bintianqi.owndroid.ui.NavIcon
import com.bintianqi.owndroid.ui.navigation.Destination
import com.bintianqi.owndroid.utils.HorizontalPadding
import com.bintianqi.owndroid.utils.adaptiveInsets
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkModesScreen(
    vm: WorkingModesViewModel, params: Destination.WorkingModes, onNavigateUp: () -> Unit,
    onActivate: () -> Unit, onDeactivate: () -> Unit, onNavigate: (Destination) -> Unit
) {
    val privilege by vm.ps.collectAsStateWithLifecycle()

    if (!params.canNavigateUp) {
        AutoActivateScreen(vm, onActivate, onNavigate)
    } else {
        PrivilegeStatusScreen(vm, onNavigateUp, onDeactivate, onNavigate)
    }
}

/** Initial launch screen: auto-activate via root, no manual steps. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AutoActivateScreen(
    vm: WorkingModesViewModel, onActivate: () -> Unit, onNavigate: (Destination) -> Unit
) {
    // 0 = activating(root), 1 = activating(adb), 2 = failed
    var state by rememberSaveable { mutableIntStateOf(0) }
    var errorText by rememberSaveable { mutableStateOf("") }

    fun tryActivate() {
        state = 0
        vm.activateDoByRoot { success, rootOutput ->
            if (success) {
                onActivate()
            } else {
                state = 1
                vm.activateDoByAdb { adbSuccess, adbOutput ->
                    if (adbSuccess) {
                        onActivate()
                    } else {
                        errorText = buildString {
                            if (!rootOutput.isNullOrBlank()) appendLine("Root: $rootOutput")
                            if (!adbOutput.isNullOrBlank()) append("ADB: $adbOutput")
                        }
                        state = 2
                    }
                }
            }
        }
    }

    LaunchedEffect(Unit) { tryActivate() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(stringResource(R.string.app_name))
                    }
                },
                actions = {
                    IconButton({ onNavigate(Destination.Settings) }) {
                        Icon(Icons.Default.Settings, null)
                    }
                }
            )
        },
        contentWindowInsets = adaptiveInsets()
    ) { paddingValues ->
        Box(
            Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            when (state) {
                0, 1 -> Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator()
                    Text(
                        stringResource(R.string.activating),
                        Modifier.padding(top = 16.dp),
                        style = typography.bodyLarge
                    )
                }
                2 -> Column(
                    Modifier
                        .fillMaxSize()
                        .padding(HorizontalPadding, 24.dp)
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        stringResource(R.string.failed),
                        color = colorScheme.error,
                        style = typography.titleLarge
                    )
                    if (errorText.isNotEmpty()) {
                        Text(
                            errorText,
                            Modifier.padding(vertical = 12.dp),
                            style = typography.bodyMedium
                        )
                    }
                    Button(
                        onClick = { tryActivate() },
                        modifier = Modifier.padding(top = 8.dp)
                    ) {
                        Text(stringResource(R.string.retry))
                    }
                }
            }
        }
    }
}

/** Settings screen: shows current privilege status, allows deactivation and management. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PrivilegeStatusScreen(
    vm: WorkingModesViewModel, onNavigateUp: () -> Unit,
    onDeactivate: () -> Unit, onNavigate: (Destination) -> Unit
) {
    val privilege by vm.ps.collectAsStateWithLifecycle()
    var showDeactivateDialog by rememberSaveable { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.working_mode)) },
                navigationIcon = { NavIcon(onNavigateUp) },
                actions = {
                    var expanded by remember { mutableStateOf(false) }
                    if (privilege.device || privilege.profile) Box {
                        IconButton({ expanded = true }) {
                            Icon(Icons.Default.MoreVert, null)
                        }
                        DropdownMenu(expanded, { expanded = false }) {
                            DropdownMenuItem(
                                { Text(stringResource(R.string.deactivate)) },
                                { expanded = false; showDeactivateDialog = true },
                                leadingIcon = { Icon(Icons.Default.Close, null) }
                            )
                            if (VERSION.SDK_INT >= 26) DropdownMenuItem(
                                { Text(stringResource(R.string.delegated_admins)) },
                                { expanded = false; onNavigate(Destination.DelegatedAdmins) },
                                leadingIcon = {
                                    Icon(painterResource(R.drawable.admin_panel_settings_fill0), null)
                                }
                            )
                            if (VERSION.SDK_INT >= 28) DropdownMenuItem(
                                { Text(stringResource(R.string.transfer_ownership)) },
                                { expanded = false; onNavigate(Destination.TransferOwnership) },
                                leadingIcon = {
                                    Icon(painterResource(R.drawable.swap_horiz_fill0), null)
                                }
                            )
                        }
                    }
                }
            )
        },
        contentWindowInsets = adaptiveInsets()
    ) { paddingValues ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (!privilege.profile) {
                PrivilegeModeItem(R.string.device_owner, privilege.device)
            }
            if (privilege.profile) {
                PrivilegeModeItem(R.string.profile_owner, true)
            }
            if (privilege.work) {
                PrivilegeModeItem(R.string.work_profile, true)
                PrivilegeModeItem(R.string.org_owned_work_profile, privilege.org)
            }
        }
    }

    if (showDeactivateDialog) AlertDialog(
        title = { Text(stringResource(R.string.deactivate)) },
        text = { Text(stringResource(R.string.info_deactivate)) },
        confirmButton = {
            var time by remember { mutableIntStateOf(3) }
            LaunchedEffect(Unit) {
                for (i in (0..2).reversed()) {
                    delay(1000)
                    time = i
                }
            }
            TextButton(
                { vm.deactivate(); showDeactivateDialog = false; onDeactivate() },
                enabled = time == 0,
                colors = ButtonDefaults.textButtonColors(contentColor = colorScheme.error)
            ) {
                Text(stringResource(R.string.confirm) + if (time != 0) " (${time}s)" else "")
            }
        },
        dismissButton = {
            TextButton({ showDeactivateDialog = false }) { Text(stringResource(R.string.cancel)) }
        },
        onDismissRequest = { showDeactivateDialog = false }
    )
}

@Composable
private fun PrivilegeModeItem(text: Int, active: Boolean) {
    Row(
        Modifier
            .fillMaxWidth()
            .background(if (active) colorScheme.primaryContainer else Color.Transparent)
            .padding(HorizontalPadding, 10.dp),
        Arrangement.SpaceBetween, Alignment.CenterVertically
    ) {
        Text(stringResource(text), style = typography.titleLarge)
        Icon(
            if (active) Icons.Default.Check else Icons.AutoMirrored.Default.KeyboardArrowRight,
            null,
            tint = if (active) colorScheme.primary else colorScheme.onBackground
        )
    }
}
