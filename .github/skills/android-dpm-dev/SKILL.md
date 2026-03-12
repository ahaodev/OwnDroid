---
name: android-dpm-dev
description: >
  Manage Android devices using Device Policy Manager (DPM) via ADB or root shell.
  Use this skill whenever you need to interact with an Android device remotely or
  programmatically — setting device policies, disabling camera or screenshots,
  managing apps (install/uninstall/suspend/hide), configuring user restrictions,
  setting up kiosk/lock-task mode, managing users, or configuring network policies.
  Works with ADB-connected devices and embedded rooted Android hardware. Invoke this
  skill even when the user phrases things casually, e.g. "disable camera on the device",
  "block the user from changing settings", "set up kiosk mode", "wipe the device",
  or "grant permission to an app" — these all belong here. Does not require Shizuku or Dhizuku.
---

# android-dpm-dev

MCP server for Android Device Policy Manager operations on ADB-connected or rooted devices.

**MCP server location:** `/home/ahao/StudioProjects/android-dpm-dev/dist/index.js`

---

## First: Configure the Connection

Always call `configure` at the start of every session before any other tool.

```
configure(mode: "adb", serial: "192.168.1.10:5555", use_root: true)
```

| Parameter  | Values                   | Notes                                                        |
|------------|--------------------------|--------------------------------------------------------------|
| `mode`     | `adb` \| `shell`         | `adb` = run from host via `adb shell`; `shell` = on-device  |
| `serial`   | IP:port or device serial | Omit if only one device is connected                         |
| `use_root` | `true` \| `false`        | Wraps commands in `su -c`; required for most privileged ops  |

Then run `device_info` to confirm the connection is working.

---

## Privilege Model

Three privilege levels unlock progressively more powerful operations:

| Level | How to obtain | What it unlocks |
|---|---|---|
| ADB shell | USB debug or `adb connect` | `pm`, `am`, `settings`, `dpm set-device-owner` |
| Root (`use_root: true`) | Pre-rooted device | All shell ops, `su -c` wrapping, keyguard/settings fallbacks |
| Device Owner | Call `set_device_owner` once | Camera, screenshot, keyguard, status bar, user restrictions, lock task, VPN, network lockdown |

**Most DPM policy tools require Device Owner.** If a policy tool fails, the likely fix is to set up a device owner first — then retry.

### Setting Up Device Owner (one-time)

```
set_device_owner(component: "com.example.app/.DeviceAdminReceiver")
```

Requirements: no Google account on the device, no other device owner currently set.
Verify with `list_admins` afterwards.

---

## Typical Workflow

```
1. configure(mode: "adb", serial: "device-serial", use_root: true)
2. device_info()                                                  # verify + check API level
3. set_device_owner("com.example.app/.DeviceAdminReceiver")      # one-time setup
4. set_camera_disabled(disabled: true)
5. set_user_restriction(restriction: "no_factory_reset", enabled: true)
6. set_lock_task_packages(packages: ["com.example.kiosk"])
```

---

## Tool Reference

### Connection & Diagnostics
| Tool | What it does |
|---|---|
| `configure` | Set mode / serial / root flag |
| `shell` | Run an arbitrary shell command |
| `device_info` | Model, Android version, API level, root status, active device owner |

### Device Control
| Tool | Notes |
|---|---|
| `lock_screen` | Immediately locks the screen |
| `reboot` | Targets: `normal` / `recovery` / `bootloader` |
| `wipe_device` | ⚠️ Factory reset — requires `confirm: true`; tries DPM first, then root broadcast |

### Device Admin Setup
| Tool | Notes |
|---|---|
| `list_admins` | Shows active admins and owner from `dumpsys` |
| `set_device_owner` | `dpm set-device-owner <component>` |
| `set_profile_owner` | Per-user, `--user <id>` |
| `remove_active_admin` | Removes admin or owner |
| `set_organization_name` | Lock screen org label (API 24+) |

### Application Management
| Tool | Notes |
|---|---|
| `list_packages` | Filter: `all/system/user/disabled/enabled`; optional `search` |
| `get_app_info` | `pm dump` summary |
| `set_app_enabled` | `pm enable` / `pm disable-user` |
| `set_app_suspended` | Visible but unlaunachable — device owner, API 24+ |
| `set_app_hidden` | Invisible and unlaunachable — device owner |
| `set_uninstall_blocked` | Prevents user uninstall — device owner |
| `clear_app_data` | `pm clear` |
| `install_apk` | `pm install`; flags: `-r` replace, `-g` auto-grant |
| `uninstall_app` | Per-user or global; optional `keep_data` |
| `grant_permission` | `pm grant --user <id> <pkg> <permission>` |
| `revoke_permission` | `pm revoke` |
| `set_permission_grant_state` | DPM-level: `0`=default, `1`=granted, `2`=denied (device owner) |
| `list_permissions` | Granted runtime permissions for a package |

### Device Policies (all require Device Owner unless noted)
| Tool | Notes |
|---|---|
| `set_camera_disabled` | Camera on/off |
| `set_screen_capture_disabled` | Screenshots + screen recording on/off |
| `set_keyguard_disabled` | Disable lock screen; falls back to `settings put secure lockscreen.disabled` with root |
| `set_status_bar_disabled` | Hide status bar — API 34+ |
| `set_master_volume_muted` | Mute/unmute device master volume |
| `set_screen_timeout` | `screen_off_timeout` in milliseconds (root or device owner) |
| `set_stay_on_while_plugged_in` | `1`=AC, `2`=USB, `4`=wireless, `7`=all; `0`=off |
| `set_auto_time` | `auto_time` / `auto_time_zone` settings |
| `set_user_restriction` | Add/remove a `UserRestriction` key (see common keys below) |
| `list_user_restrictions` | Active restrictions from `dumpsys device_policy` |
| `set_lock_task_packages` | Kiosk mode — package allowlist (API 26+) |
| `set_global_setting` | `settings put global` enforced via DPM |
| `set_secure_setting` | `settings put secure` enforced via DPM |

**Common `set_user_restriction` keys:** `no_camera`, `no_factory_reset`, `no_install_apps`,
`no_uninstall_apps`, `no_usb_file_transfer`, `no_safe_boot`, `no_debugging_features`,
`no_config_wifi`, `no_config_vpn`, `no_config_date_time`.

### User Management
| Tool | Notes |
|---|---|
| `list_users` | `pm list users` |
| `create_user` | Supports `--managed`, `--ephemeral` |
| `create_and_manage_user` | DPM-managed user with flags (device owner, API 24+) |
| `remove_user` | `pm remove-user <id>` |
| `switch_user` | `am switch-user <id>` |
| `start_user` | Background start without switching (API 28+) |
| `stop_user` | Stop background user |
| `set_user_session_message` | Login/logout message (device owner, API 28+) |
| `set_affiliation_ids` | Affiliation ID set — unlocks cross-user management (device owner, API 26+) |
| `logout_user` | Return to primary user (device owner, API 28+) |

### Network
| Tool | Notes |
|---|---|
| `get_network_info` | Wi-Fi status, IP routes, DNS |
| `set_private_dns` | Mode: `off` / `opportunistic` / `hostname <server>` |
| `set_always_on_vpn` | Package + lockdown flag (device owner, API 24+) |
| `set_wifi_enabled` | `svc wifi enable/disable` |
| `set_airplane_mode` | Sets setting + sends broadcast |
| `set_network_lockdown` | Prevent Wi-Fi config changes (device owner, API 30+) |
