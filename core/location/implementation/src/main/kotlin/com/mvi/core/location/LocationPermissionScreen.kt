package com.mvi.core.location

import android.Manifest
import android.content.Intent
import android.content.res.Configuration
import android.provider.Settings
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import com.mvi.core.location.permissions.MultiplePermissionsState
import com.mvi.core.location.permissions.rememberMutableMultiplePermissionsState
import com.mvi.core.ui.MviTheme
import com.mvi.core.ui.button.MviPrimaryButton
import androidx.core.net.toUri

/**
 * Displays a permission request screen for location access.
 *
 * This composable presents a user-friendly interface requesting location permissions
 * required for the weather app functionality. It handles permission states, shows
 * rationales, and provides a button to open app settings when permissions are denied.
 *
 * @param permissions List of permission strings to request (default: coarse and fine location)
 * @param onGranted Callback to invoke when all required permissions are granted
 */
@Composable
fun LocationPermissionScreen(
    permissions: List<String> = listOf(
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION,
    ),
    onGranted: @Composable () -> Unit,
) {
    var errorText by remember { mutableStateOf("") }

    val permissionsState = rememberMutableMultiplePermissionsState(
        permissions = permissions,
    ) { states ->
        val rejected = states.filterValues { !it }.keys
        errorText = if (rejected.none { it in permissions }) {
            ""
        } else {
            "${rejected.joinToString()} required for Weather data"
        }
    }

    val permissionsGranted = permissionsState.revokedPermissions.none { it.permission in permissions }
    if (permissionsGranted) {
        onGranted()
    } else {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            PermissionScreen(
                state = permissionsState,
                description = "App needs to know your location in order to show weather and stories on that location",
                errorText = errorText,
            )

            val context = LocalContext.current

            FloatingActionButton(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(MviTheme.dimens.standard16),
                onClick = {
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK
                        data = "package:${context.packageName}".toUri()
                    }
                    context.startActivity(intent)
                },
            ) {
                Icon(imageVector = Icons.Rounded.Settings, contentDescription = "App settings")
            }
        }
    }
}

/**
 * Displays a permission screen explaining required location permissions to the user.
 *
 * This composable presents the rationale for requiring location permissions, lists
 * the specific permissions needed, and provides a button to grant them. It also
 * handles showing a rationale dialog when users deny permissions unexpectedly.
 *
 * @param state The mutable permissions state tracking granted and revoked permissions
 * @param description An optional description explaining why location access is needed
 * @param errorText An optional error message to display when permissions are rejected
 */
@Composable
private fun PermissionScreen(
    state: MultiplePermissionsState,
    description: String?,
    errorText: String,
) {
    var showRationale by remember(state) {
        mutableStateOf(false)
    }

    val permissions = remember(state.revokedPermissions) {
        state.revokedPermissions.joinToString("\n") {
            " - " + it.permission.removePrefix("android.permission.")
        }
    }
    val configuration = LocalConfiguration.current
    val isPortrait = remember(configuration) {
        configuration.orientation == Configuration.ORIENTATION_PORTRAIT
    }

    val sizeModifier  = Modifier.fillMaxWidth(
        if (isPortrait) 1f else 0.5f
    )

    Column(
        modifier = sizeModifier
            .padding(horizontal = MviTheme.dimens.standard16)
            .animateContentSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = "Weather App requires permission/s:",
            style = MviTheme.typography.titlePrimary,
            modifier = Modifier.padding(vertical = MviTheme.dimens.standard16),
        )
        Text(
            text = permissions,
            style = MviTheme.typography.bodyPrimary,
            modifier = Modifier.padding(vertical = MviTheme.dimens.standard16),
        )
        if (description != null) {
            Text(
                text = description,
                style = MviTheme.typography.bodyPrimary,
                modifier = Modifier.padding(vertical = MviTheme.dimens.standard16),
            )
        }
        val onClick = remember {
            {
                if (state.shouldShowRationale) {
                    showRationale = true
                } else {
                    state.launchMultiplePermissionRequest()
                }
            }
        }
        MviPrimaryButton(
            text = "Grant permissions",
            onClick = onClick,
        )
        LaunchedEffect(Unit) { onClick() }
        if (errorText.isNotBlank()) {
            Text(
                text = errorText,
                style = MviTheme.typography.spotPrimary,
                modifier = Modifier.padding(vertical = MviTheme.dimens.standard16),
            )
        }
    }
    if (showRationale) {
        AlertDialog(
            onDismissRequest = {
                showRationale = false
            },
            title = {
                Text(text = "Permissions required by the sample")
            },
            text = {
                Text(text = "The sample requires the following permissions to work:\n $permissions")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showRationale = false
                        state.launchMultiplePermissionRequest()
                    },
                ) {
                    Text("Continue")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showRationale = false
                    },
                ) {
                    Text("Dismiss")
                }
            },
        )
    }
}
