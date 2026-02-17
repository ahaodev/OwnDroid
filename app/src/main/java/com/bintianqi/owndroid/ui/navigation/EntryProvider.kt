package com.bintianqi.owndroid.ui.navigation

import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import com.bintianqi.owndroid.AppChooserScreen
import com.bintianqi.owndroid.MyViewModel
import com.bintianqi.owndroid.R
import com.bintianqi.owndroid.SP
import com.bintianqi.owndroid.ui.screen.AboutScreen
import com.bintianqi.owndroid.ui.screen.AddApnSettingScreen
import com.bintianqi.owndroid.ui.screen.AddDelegatedAdminScreen
import com.bintianqi.owndroid.ui.screen.AddPreferentialNetworkServiceConfigScreen
import com.bintianqi.owndroid.ui.screen.AffiliationIdScreen
import com.bintianqi.owndroid.ui.screen.AlwaysOnVpnPackageScreen
import com.bintianqi.owndroid.ui.screen.ApiSettings
import com.bintianqi.owndroid.ui.screen.AppLockSettingsScreen
import com.bintianqi.owndroid.ui.screen.AppPermissionsManagerScreen
import com.bintianqi.owndroid.ui.screen.AppearanceScreen
import com.bintianqi.owndroid.ui.screen.ApplicationDetailsScreen
import com.bintianqi.owndroid.ui.screen.ApplicationsFeaturesScreen
import com.bintianqi.owndroid.ui.screen.AutoTimePolicyScreen
import com.bintianqi.owndroid.ui.screen.AutoTimeZonePolicyScreen
import com.bintianqi.owndroid.ui.screen.CaCertScreen
import com.bintianqi.owndroid.ui.screen.ChangeTimeScreen
import com.bintianqi.owndroid.ui.screen.ChangeTimeZoneScreen
import com.bintianqi.owndroid.ui.screen.ChangeUsernameScreen
import com.bintianqi.owndroid.ui.screen.ClearAppStorageScreen
import com.bintianqi.owndroid.ui.screen.ContentProtectionPolicyScreen
import com.bintianqi.owndroid.ui.screen.CreateUserScreen
import com.bintianqi.owndroid.ui.screen.CreateWorkProfileScreen
import com.bintianqi.owndroid.ui.screen.CredentialManagerPolicyScreen
import com.bintianqi.owndroid.ui.screen.CrossProfileIntentFilterScreen
import com.bintianqi.owndroid.ui.screen.DefaultInputMethodScreen
import com.bintianqi.owndroid.ui.screen.DelegatedAdminsScreen
import com.bintianqi.owndroid.ui.screen.DeleteWorkProfileScreen
import com.bintianqi.owndroid.ui.screen.DeviceInfoScreen
import com.bintianqi.owndroid.ui.screen.DhizukuServerSettingsScreen
import com.bintianqi.owndroid.ui.screen.DisableAccountManagementScreen
import com.bintianqi.owndroid.ui.screen.EditAppGroupScreen
import com.bintianqi.owndroid.ui.screen.EnableSystemAppScreen
import com.bintianqi.owndroid.ui.screen.FrpPolicyScreen
import com.bintianqi.owndroid.ui.screen.HardwareMonitorScreen
import com.bintianqi.owndroid.ui.screen.HomeScreen
import com.bintianqi.owndroid.ui.screen.InstallExistingAppScreen
import com.bintianqi.owndroid.ui.screen.InstallSystemUpdateScreen
import com.bintianqi.owndroid.ui.screen.KeyguardDisabledFeaturesScreen
import com.bintianqi.owndroid.ui.screen.KeyguardScreen
import com.bintianqi.owndroid.ui.screen.LockScreenInfoScreen
import com.bintianqi.owndroid.ui.screen.LockTaskModeScreen
import com.bintianqi.owndroid.ui.screen.ManageAppGroupsScreen
import com.bintianqi.owndroid.ui.screen.ManagedConfigurationScreen
import com.bintianqi.owndroid.ui.screen.MtePolicyScreen
import com.bintianqi.owndroid.ui.screen.NearbyStreamingPolicyScreen
import com.bintianqi.owndroid.ui.screen.NetworkLoggingScreen
import com.bintianqi.owndroid.ui.screen.NetworkOptionsScreen
import com.bintianqi.owndroid.ui.screen.NetworkScreen
import com.bintianqi.owndroid.ui.screen.NetworkStatsScreen
import com.bintianqi.owndroid.ui.screen.NetworkStatsViewerScreen
import com.bintianqi.owndroid.ui.screen.NotificationsScreen
import com.bintianqi.owndroid.ui.screen.OrganizationOwnedProfileScreen
import com.bintianqi.owndroid.ui.screen.OverrideApnScreen
import com.bintianqi.owndroid.ui.screen.PackageFunctionScreen
import com.bintianqi.owndroid.ui.screen.PasswordInfoScreen
import com.bintianqi.owndroid.ui.screen.PasswordScreen
import com.bintianqi.owndroid.ui.screen.PermissionDetailScreen
import com.bintianqi.owndroid.ui.screen.PermissionManagerScreen
import com.bintianqi.owndroid.ui.screen.PermissionPolicyScreen
import com.bintianqi.owndroid.ui.screen.PermittedAsAndImPackages
import com.bintianqi.owndroid.ui.screen.PreferentialNetworkServiceInfo
import com.bintianqi.owndroid.ui.screen.PreferentialNetworkServiceScreen
import com.bintianqi.owndroid.ui.screen.PrivateDnsScreen
import com.bintianqi.owndroid.ui.screen.RecommendedGlobalProxyScreen
import com.bintianqi.owndroid.ui.screen.RequiredPasswordComplexityScreen
import com.bintianqi.owndroid.ui.screen.RequiredPasswordQualityScreen
import com.bintianqi.owndroid.ui.screen.ResetPasswordScreen
import com.bintianqi.owndroid.ui.screen.ResetPasswordTokenScreen
import com.bintianqi.owndroid.ui.screen.SecurityLoggingScreen
import com.bintianqi.owndroid.ui.screen.SetDefaultDialerScreen
import com.bintianqi.owndroid.ui.screen.SettingsOptionsScreen
import com.bintianqi.owndroid.ui.screen.SettingsScreen
import com.bintianqi.owndroid.ui.screen.SupportMessageScreen
import com.bintianqi.owndroid.ui.screen.SuspendPersonalAppScreen
import com.bintianqi.owndroid.ui.screen.SystemManagerScreen
import com.bintianqi.owndroid.ui.screen.SystemOptionsScreen
import com.bintianqi.owndroid.ui.screen.SystemUpdatePolicyScreen
import com.bintianqi.owndroid.ui.screen.TransferOwnershipScreen
import com.bintianqi.owndroid.ui.screen.UninstallAppScreen
import com.bintianqi.owndroid.ui.screen.UpdateNetworkScreen
import com.bintianqi.owndroid.ui.screen.UserInfoScreen
import com.bintianqi.owndroid.ui.screen.UserOperationScreen
import com.bintianqi.owndroid.ui.screen.UserRestrictionEditorScreen
import com.bintianqi.owndroid.ui.screen.UserRestrictionOptionsScreen
import com.bintianqi.owndroid.ui.screen.UserRestrictionScreen
import com.bintianqi.owndroid.ui.screen.UserSessionMessageScreen
import com.bintianqi.owndroid.ui.screen.UsersOptionsScreen
import com.bintianqi.owndroid.ui.screen.UsersScreen
import com.bintianqi.owndroid.ui.screen.WifiScreen
import com.bintianqi.owndroid.ui.screen.WifiSecurityLevelScreen
import com.bintianqi.owndroid.ui.screen.WifiSsidPolicyScreen
import com.bintianqi.owndroid.ui.screen.WipeDataScreen
import com.bintianqi.owndroid.ui.screen.WorkModesScreen
import com.bintianqi.owndroid.ui.screen.WorkProfileScreen

fun myEntryProvider(
    destination: Destination, backstack: NavBackStack<NavKey>, vm: MyViewModel
) = entryProvider {
    fun navigate(dest: Destination) {
        backstack += dest
    }
    fun navigateUp() {
        if (backstack.size > 1) backstack.removeLastOrNull()
    }
    fun navigateToAppGroups() {
        navigate(Destination.ManageAppGroups)
    }
    fun navigateAndPopAll(dest: Destination) {
        navigate(dest)
        repeat(backstack.size - 1) {
            backstack.removeFirst()
        }
    }
    fun choosePackage() {
        navigate(Destination.ApplicationsList(false, true))
    }
    fun chooseSinglePackage() {
        navigate(Destination.ApplicationsList(false, false))
    }

    entry<Destination.Home> {
        HomeScreen(::navigate)
    }
    entry<Destination.WorkingModes> {
        WorkModesScreen(vm, it, ::navigateUp, {
            navigateAndPopAll(Destination.Home)
        }, {
            navigateAndPopAll(Destination.WorkingModes(false))
        }, ::navigate)
    }
    entry<Destination.DhizukuServerSettings> {
        DhizukuServerSettingsScreen(vm.dhizukuClients, vm::getDhizukuClients,
            vm::updateDhizukuClient, vm::getDhizukuServerEnabled, vm::setDhizukuServerEnabled,
            ::navigateUp)
    }

    entry<Destination.DelegatedAdmins> {
        DelegatedAdminsScreen(vm.delegatedAdmins, vm::getDelegatedAdmins, ::navigateUp, ::navigate)
    }
    entry<Destination.DelegatedAdminDetails>{
        AddDelegatedAdminScreen(vm.chosenPackage, ::chooseSinglePackage, it,
            vm::setDelegatedAdmin,  ::navigateUp)
    }
    entry<Destination.DeviceInfo> { DeviceInfoScreen(vm, ::navigateUp) }
    entry<Destination.LockScreenInfo> {
        LockScreenInfoScreen(vm::getLockScreenInfo, vm::setLockScreenInfo, ::navigateUp)
    }
    entry<Destination.SupportMessage> {
        SupportMessageScreen(vm::getShortSupportMessage, vm::getLongSupportMessage,
            vm::setShortSupportMessage, vm::setLongSupportMessage, ::navigateUp)
    }
    entry<Destination.TransferOwnership> {
        TransferOwnershipScreen(vm.deviceAdminReceivers, vm::getDeviceAdminReceivers,
            vm::transferOwnership, ::navigateUp
        ) {
            navigate(Destination.WorkingModes(false))
        }
    }

    entry<Destination.System> { SystemManagerScreen(vm, ::navigateUp, ::navigate) }
    entry<Destination.SystemOptions> { SystemOptionsScreen(vm, ::navigateUp) }
    entry<Destination.Keyguard> {
        KeyguardScreen(vm::setKeyguardDisabled, vm::lockScreen, ::navigateUp)
    }
    entry<Destination.HardwareMonitor> {
        HardwareMonitorScreen(vm.hardwareProperties, vm::getHardwareProperties,
            vm::setHpRefreshInterval, ::navigateUp)
    }
    entry<Destination.DefaultInputMethod> {
        DefaultInputMethodScreen(vm::getCurrentInputMethod, vm.inputMethodList,
            vm::getInputMethods, vm::setDefaultInputMethod, ::navigateUp)
    }
    entry<Destination.ChangeTime> { ChangeTimeScreen(vm::setTime, ::navigateUp) }
    entry<Destination.ChangeTimezone> { ChangeTimeZoneScreen(vm::setTimeZone, ::navigateUp) }
    entry<Destination.AutoTimePolicy> {
        AutoTimePolicyScreen(vm::getAutoTimePolicy, vm::setAutoTimePolicy, ::navigateUp)
    }
    entry<Destination.AutoTimezonePolicy> {
        AutoTimeZonePolicyScreen(vm::getAutoTimeZonePolicy, vm::setAutoTimeZonePolicy,
            ::navigateUp)
    }
    //entry<Destination.> { KeyPairs(::navigateUp) }
    entry<Destination.ContentProtectionPolicy> {
        ContentProtectionPolicyScreen(vm::getContentProtectionPolicy,
            vm::setContentProtectionPolicy, ::navigateUp)
    }
    entry<Destination.PermissionPolicy> {
        PermissionPolicyScreen(vm::getPermissionPolicy, vm::setPermissionPolicy, ::navigateUp)
    }
    entry<Destination.MtePolicy> {
        MtePolicyScreen(vm::getMtePolicy, vm::setMtePolicy, ::navigateUp)
    }
    entry<Destination.NearbyStreamingPolicy> {
        NearbyStreamingPolicyScreen(vm::getNsAppPolicy, vm::setNsAppPolicy,
            vm::getNsNotificationPolicy, vm::setNsNotificationPolicy, ::navigateUp)
    }
    entry<Destination.LockTaskMode> {
        LockTaskModeScreen(
            vm.chosenPackage, ::chooseSinglePackage, ::choosePackage, vm.lockTaskPackages,
            vm::getLockTaskPackages, vm::setLockTaskPackage, vm::startLockTaskMode,
            vm:: getLockTaskFeatures, vm::setLockTaskFeatures, ::navigateUp
        )
    }
    entry<Destination.CaCert> {
        CaCertScreen(vm.installedCaCerts, vm::getCaCerts, vm.selectedCaCert, vm::selectCaCert, vm::installCaCert, vm::parseCaCert,
            vm::exportCaCert, vm::uninstallCaCert, vm::uninstallAllCaCerts, ::navigateUp)
    }
    entry<Destination.SecurityLogging> {
        SecurityLoggingScreen(vm::getSecurityLoggingEnabled, vm::setSecurityLoggingEnabled,
            vm::exportSecurityLogs, vm::getSecurityLogsCount, vm::deleteSecurityLogs,
            vm::getPreRebootSecurityLogs, vm::exportPreRebootSecurityLogs, ::navigateUp)
    }
    entry<Destination.DisableAccountManagement> {
        DisableAccountManagementScreen(vm.mdAccountTypes, vm::getMdAccountTypes,
            vm::setMdAccountType, ::navigateUp)
    }
    entry<Destination.SystemUpdatePolicy> {
        SystemUpdatePolicyScreen(vm::getSystemUpdatePolicy, vm::setSystemUpdatePolicy,
            vm::getPendingSystemUpdate, ::navigateUp)
    }
    entry<Destination.InstallSystemUpdate> {
        InstallSystemUpdateScreen(vm::installSystemUpdate, ::navigateUp)
    }
    entry<Destination.FrpPolicy> {
        FrpPolicyScreen(vm.getFrpPolicy(), vm::setFrpPolicy, ::navigateUp)
    }
    entry<Destination.WipeData> { WipeDataScreen(vm::wipeData, ::navigateUp) }

    entry<Destination.Network> { NetworkScreen(::navigateUp, ::navigate) }
    entry<Destination.WiFi> {
        WifiScreen(vm, ::navigateUp, ::navigate) { navigate(Destination.UpdateNetwork(it)) }
    }
    entry<Destination.NetworkOptions> {
        NetworkOptionsScreen(vm::getLanEnabled, vm::setLanEnabled, ::navigateUp)
    }
    entry<Destination.UpdateNetwork> {
        val info = vm.configuredNetworks.collectAsStateWithLifecycle().value[
            it.index
        ]
        UpdateNetworkScreen(info, vm::setWifi, ::navigateUp)
    }
    entry<Destination.WifiSecurityLevel> {
        WifiSecurityLevelScreen(vm::getMinimumWifiSecurityLevel,
            vm::setMinimumWifiSecurityLevel, ::navigateUp)
    }
    entry<Destination.WifiSsidPolicy> {
        WifiSsidPolicyScreen(vm::getSsidPolicy, vm::setSsidPolicy, ::navigateUp)
    }
    entry<Destination.NetworkStats> {
        NetworkStatsScreen(vm.chosenPackage, ::chooseSinglePackage, vm::getPackageUid,
            vm::queryNetworkStats, ::navigateUp) { navigate(Destination.NetworkStatsViewer) }
    }
    entry<Destination.NetworkStatsViewer> {
        NetworkStatsViewerScreen(vm.networkStatsData, vm::clearNetworkStats, ::navigateUp)
    }
    entry<Destination.PrivateDns> {
        PrivateDnsScreen(vm::getPrivateDns, vm::setPrivateDns, ::navigateUp)
    }
    entry<Destination.AlwaysOnVpnPackage> {
        AlwaysOnVpnPackageScreen(vm::getAlwaysOnVpnPackage, vm::getAlwaysOnVpnLockdown,
            vm::setAlwaysOnVpn, vm.chosenPackage, ::chooseSinglePackage, ::navigateUp)
    }
    entry<Destination.RecommendedGlobalProxy> {
        RecommendedGlobalProxyScreen(vm::setRecommendedGlobalProxy, ::navigateUp)
    }
    entry<Destination.NetworkLogging> {
        NetworkLoggingScreen(vm::getNetworkLoggingEnabled, vm::setNetworkLoggingEnabled,
            vm::getNetworkLogsCount, vm::exportNetworkLogs, vm::deleteNetworkLogs, ::navigateUp)
    }
    //entry<Destination.WifiAuthKeypair> { WifiAuthKeypairScreen(::navigateUp) }
    entry<Destination.PreferentialNetworkService> {
        PreferentialNetworkServiceScreen(vm::getPnsEnabled, vm::setPnsEnabled, vm.pnsConfigs,
            vm::getPnsConfigs, ::navigateUp, ::navigate)
    }
    entry<Destination.AddPreferentialNetworkServiceConfig> {
        val info = vm.pnsConfigs.collectAsStateWithLifecycle().value.getOrNull(
            it.index
        ) ?: PreferentialNetworkServiceInfo()
        AddPreferentialNetworkServiceConfigScreen(info, vm::setPnsConfig, ::navigateUp)
    }
    entry<Destination.OverrideApn> {
        OverrideApnScreen(vm.apnConfigs, vm::getApnConfigs, vm::getApnEnabled,
            vm::setApnEnabled, ::navigateUp) { navigate(Destination.AddApnSetting(it)) }
    }
    entry<Destination.AddApnSetting> {
        val origin = vm.apnConfigs.collectAsStateWithLifecycle().value.getOrNull(it.index)
        AddApnSettingScreen(vm::setApnConfig, vm::removeApnConfig, origin, ::navigateUp)
    }

    entry<Destination.WorkProfile> { WorkProfileScreen(::navigateUp, ::navigate) }
    entry<Destination.OrganizationOwnedProfile> {
        OrganizationOwnedProfileScreen(vm::activateOrgProfileByShizuku, ::navigateUp)
    }
    entry<Destination.CreateWorkProfile> {
        CreateWorkProfileScreen(vm::createWorkProfile, ::navigateUp)
    }
    entry<Destination.SuspendPersonalApp> {
        SuspendPersonalAppScreen(
            vm::getPersonalAppsSuspendedReason, vm::setPersonalAppsSuspended,
            vm::getProfileMaxTimeOff, vm::setProfileMaxTimeOff, ::navigateUp
        )
    }
    entry<Destination.CrossProfileIntentFilter> {
        CrossProfileIntentFilterScreen(
            vm::addCrossProfileIntentFilter, vm::clearCrossProfileIntentFilters,
            vm::importCrossProfileIntentFilters, vm::exportCrossProfileIntentFilters,
            ::navigateUp
        )
    }
    entry<Destination.DeleteWorkProfile> { DeleteWorkProfileScreen(vm::wipeData, ::navigateUp) }

    entry<Destination.ApplicationsList> { params ->
        AppChooserScreen(
            params, vm.installedPackages, vm.refreshPackagesProgress, { name ->
                if (params.canSwitchView) {
                    if (name == null) {
                        navigateUp()
                    } else {
                        navigate(Destination.ApplicationDetails(name))
                    }
                } else {
                    if (name != null) vm.chosenPackage.trySend(name)
                    navigateUp()
                }
            }, {
                SP.applicationsListView = false
                navigate(Destination.ApplicationFunctions)
                backstack.removeAt(backstack.size - 2)
            }, vm::refreshPackageList, vm::setPackageSuspended, vm::setPackageHidden)
    }
    entry<Destination.ApplicationFunctions> {
        ApplicationsFeaturesScreen(::navigateUp, ::navigate) {
            SP.applicationsListView = true
            navigate(Destination.ApplicationsList(true, true))
            backstack.removeAt(backstack.size - 2)
        }
    }
    entry<Destination.ApplicationDetails> {
        ApplicationDetailsScreen(it, vm, ::navigateUp, ::navigate)
    }
    entry<Destination.Suspend> {
        PackageFunctionScreen(
            R.string.suspend, vm.suspendedPackages, vm::getSuspendedPackaged,
            vm::setPackageSuspended, ::navigateUp, vm.chosenPackage, ::choosePackage,
            ::navigateToAppGroups, vm.appGroups, R.string.info_suspend_app
        )
    }
    entry<Destination.Hide> {
        PackageFunctionScreen(
            R.string.hide, vm.hiddenPackages, vm::getHiddenPackages, vm::setPackageHidden,
            ::navigateUp, vm.chosenPackage, ::choosePackage, ::navigateToAppGroups, vm.appGroups
        )
    }
    entry<Destination.BlockUninstall> {
        PackageFunctionScreen(
            R.string.block_uninstall, vm.ubPackages, vm::getUbPackages, vm::setPackageUb,
            ::navigateUp, vm.chosenPackage, ::choosePackage, ::navigateToAppGroups, vm.appGroups
        )
    }
    entry<Destination.DisableUserControl> {
        PackageFunctionScreen(
            R.string.disable_user_control, vm.ucdPackages, vm::getUcdPackages,
            vm::setPackageUcd, ::navigateUp, vm.chosenPackage, ::choosePackage,
            ::navigateToAppGroups, vm.appGroups, R.string.info_disable_user_control
        )
    }
    entry<Destination.AppPermissionsManager> {
        AppPermissionsManagerScreen(
            vm::getPackagePermissions, vm::setPackagePermission, ::navigateUp, it
        )
    }
    entry<Destination.PermissionManager> {
        PermissionManagerScreen(::navigate, ::navigateUp)
    }
    entry<Destination.PermissionDetail> {
        PermissionDetailScreen(
            it, vm::getPermissionPackages, vm::setPackagePermission, ::navigateUp
        )
    }
    entry<Destination.DisableMeteredData> {
        PackageFunctionScreen(
            R.string.disable_metered_data, vm.mddPackages, vm::getMddPackages,
            vm::setPackageMdd, ::navigateUp, vm.chosenPackage, ::choosePackage,
            ::navigateToAppGroups, vm.appGroups
        )
    }
    entry<Destination.ClearAppStorage> {
        ClearAppStorageScreen(
            vm.chosenPackage, ::chooseSinglePackage, vm::clearAppData, ::navigateUp
        )
    }
    entry<Destination.UninstallApp> {
        UninstallAppScreen(
            vm.chosenPackage, ::chooseSinglePackage, vm::uninstallPackage, ::navigateUp
        )
    }
    entry<Destination.KeepUninstalledPackages> {
        PackageFunctionScreen(
            R.string.keep_uninstalled_packages, vm.kuPackages, vm::getKuPackages,
            vm::setPackageKu, ::navigateUp, vm.chosenPackage, ::choosePackage,
            ::navigateToAppGroups, vm.appGroups, R.string.info_keep_uninstalled_apps
        )
    }
    entry<Destination.InstallExistingApp> {
        InstallExistingAppScreen(
            vm.chosenPackage, ::chooseSinglePackage, vm::installExistingApp, ::navigateUp
        )
    }
    entry<Destination.CrossProfilePackages> {
        PackageFunctionScreen(
            R.string.cross_profile_apps, vm.cpPackages,
            vm::getCpPackages, vm::setPackageCp, ::navigateUp, vm.chosenPackage,
            ::choosePackage, ::navigateToAppGroups, vm.appGroups
        )
    }
    entry<Destination.CrossProfileWidgetProviders> {
        PackageFunctionScreen(R.string.cross_profile_widget, vm.cpwProviders,
            vm::getCpwProviders, vm::setCpwProvider, ::navigateUp, vm.chosenPackage,
            ::choosePackage, ::navigateToAppGroups, vm.appGroups)
    }
    entry<Destination.CredentialManagerPolicy> {
        CredentialManagerPolicyScreen(
            vm.chosenPackage, ::choosePackage, vm.cmPackages, vm::getCmPolicy,
            vm::setCmPackage, vm::setCmPolicy, ::navigateUp
        )
    }
    entry<Destination.PermittedAccessibilityServices> {
        PermittedAsAndImPackages(
            R.string.permitted_accessibility_services,
            R.string.system_accessibility_always_allowed, vm.chosenPackage, ::choosePackage,
            vm.pasPackages, vm::getPasPackages, vm::setPasPackage, vm::setPasPolicy,
            ::navigateUp
        )
    }
    entry<Destination.PermittedInputMethods> {
        PermittedAsAndImPackages(
            R.string.permitted_ime, R.string.system_ime_always_allowed,
            vm.chosenPackage, ::choosePackage, vm.pimPackages, vm::getPimPackages,
            vm::setPimPackage, vm::setPimPolicy, ::navigateUp
        )
    }
    entry<Destination.EnableSystemApp> {
        EnableSystemAppScreen(
            vm.chosenPackage, ::chooseSinglePackage, vm::enableSystemApp, ::navigateUp
        )
    }
    entry<Destination.SetDefaultDialer> {
        SetDefaultDialerScreen(
            vm.chosenPackage, ::chooseSinglePackage, vm::setDefaultDialer, ::navigateUp
        )
    }
    entry<Destination.ManagedConfiguration> {
        ManagedConfigurationScreen(
            it, vm.appRestrictions, vm::setAppRestrictions,
            vm::clearAppRestrictions, ::navigateUp
        )
    }
    entry<Destination.ManageAppGroups> {
        ManageAppGroupsScreen(
            vm.appGroups, vm::exportAppGroups, vm::importAppGroups,
            { id, name, apps -> navigate(Destination.EditAppGroup(id, name, apps)) },
            ::navigateUp
        )
    }
    entry<Destination.EditAppGroup> {
        EditAppGroupScreen(
            it, vm::getAppInfo, ::navigateUp, vm::setAppGroup,
            vm::deleteAppGroup, ::choosePackage, vm.chosenPackage
        )
    }

    entry<Destination.UserRestriction> {
        UserRestrictionScreen(vm::getUserRestrictions, ::navigateUp, ::navigate)
    }
    entry<Destination.UserRestrictionEditor> {
        UserRestrictionEditorScreen(vm.userRestrictions, vm::setUserRestriction, ::navigateUp)
    }
    entry<Destination.UserRestrictionOptions> {
        UserRestrictionOptionsScreen(it, vm.userRestrictions,
            vm::setUserRestriction, vm::createUserRestrictionShortcut, ::navigateUp)
    }

    entry<Destination.Users> { UsersScreen(vm, ::navigateUp, ::navigate) }
    entry<Destination.UserInfo> { UserInfoScreen(vm::getUserInformation, ::navigateUp) }
    entry<Destination.UsersOptions> {
        UsersOptionsScreen(vm::getLogoutEnabled, vm::setLogoutEnabled, ::navigateUp)
    }
    entry<Destination.UserOperation> {
        UserOperationScreen(vm::getUserIdentifiers, vm::doUserOperation,
            vm::createUserOperationShortcut, ::navigateUp)
    }
    entry<Destination.CreateUser> { CreateUserScreen(vm::createUser, ::navigateUp) }
    entry<Destination.ChangeUsername> { ChangeUsernameScreen(vm::setProfileName, ::navigateUp) }
    entry<Destination.UserSessionMessage> {
        UserSessionMessageScreen(vm::getUserSessionMessages, vm::setStartUserSessionMessage,
            vm::setEndUserSessionMessage, ::navigateUp)
    }
    entry<Destination.AffiliationId> {
        AffiliationIdScreen(vm.affiliationIds, vm::getAffiliationIds, vm::setAffiliationId,
            ::navigateUp)
    }

    entry<Destination.Password> { PasswordScreen(vm, ::navigateUp, ::navigate) }
    entry<Destination.PasswordInfo> {
        PasswordInfoScreen(vm::getPasswordComplexity, vm::isPasswordComplexitySufficient,
            vm::isUsingUnifiedPassword, ::navigateUp)
    }
    entry<Destination.ResetPasswordToken> {
        ResetPasswordTokenScreen(vm::getRpTokenState, vm::setRpToken,
            vm::createActivateRpTokenIntent, vm::clearRpToken, ::navigateUp)
    }
    entry<Destination.ResetPassword> { ResetPasswordScreen(vm::resetPassword, ::navigateUp) }
    entry<Destination.RequiredPasswordComplexity> {
        RequiredPasswordComplexityScreen(vm::getRequiredPasswordComplexity,
            vm::setRequiredPasswordComplexity, ::navigateUp)
    }
    entry<Destination.KeyguardDisabledFeatures> {
        KeyguardDisabledFeaturesScreen(vm::getKeyguardDisableConfig,
            vm::setKeyguardDisableConfig, ::navigateUp)
    }
    entry<Destination.RequiredPasswordQuality> { RequiredPasswordQualityScreen(::navigateUp) }

    entry<Destination.Settings> { SettingsScreen(::navigate, ::navigateUp) }
    entry<Destination.SettingsOptions> {
        SettingsOptionsScreen(
            vm::getDisplayDangerousFeatures, vm::getShortcutsEnabled,
            vm::setDisplayDangerousFeatures, vm::setShortcutsEnabled, ::navigateUp
        )
    }
    entry<Destination.AppearanceSettings> {
        AppearanceScreen(::navigateUp, vm.theme, vm::changeTheme)
    }
    entry<Destination.AppLockSettings> {
        AppLockSettingsScreen(vm.getAppLockConfig(), vm::setAppLockConfig, ::navigateUp)
    }
    entry<Destination.ApiSettings> {
        ApiSettings(vm::getApiEnabled, vm::setApiKey, ::navigateUp)
    }
    entry<Destination.NotificationSettings> {
        NotificationsScreen(
            vm.enabledNotifications, vm::getEnabledNotifications,
            vm::setNotificationEnabled, ::navigateUp
        )
    }
    entry<Destination.About> { AboutScreen(::navigateUp) }
}(destination) as NavEntry<NavKey>
