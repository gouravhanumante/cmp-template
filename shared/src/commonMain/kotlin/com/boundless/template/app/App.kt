package com.boundless.template.app

import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.rememberNavController
import com.boundless.template.app.gate.AppGateScreen
import com.boundless.template.app.gate.storeUrl
import com.boundless.template.app.navigation.AppNavHost
import com.boundless.template.core.designsystem.theme.AppTheme
import com.boundless.template.core.resources.Res
import com.boundless.template.core.resources.UiText
import com.boundless.template.core.resources.app_maintenance_message
import com.boundless.template.core.resources.app_maintenance_title
import com.boundless.template.core.resources.app_update_action
import com.boundless.template.core.resources.app_update_message
import com.boundless.template.core.resources.app_update_title
import com.boundless.template.core.resources.ds_try_again
import com.boundless.template.core.resources.resolve
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun App(viewModel: AppViewModel = koinViewModel()) {
    AppTheme {
        val destination by viewModel.destination.collectAsStateWithLifecycle()
        val navController = rememberNavController()
        val snackbarHostState = remember { SnackbarHostState() }
        // A UiText can only be resolved in a composable scope, which a LaunchedEffect body is not.
        var pendingMessage by remember { mutableStateOf<UiText?>(null) }

        LaunchedEffect(viewModel) {
            viewModel.messages.collect { pendingMessage = it }
        }

        pendingMessage?.let { message ->
            val text = message.resolve()
            LaunchedEffect(message) {
                snackbarHostState.showSnackbar(text)
                pendingMessage = null
            }
        }

        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = MaterialTheme.colorScheme.background,
            snackbarHost = { SnackbarHost(snackbarHostState) },
        ) { padding ->
            when (destination) {
                AppDestination.Maintenance ->
                    AppGateScreen(
                        title = stringResource(Res.string.app_maintenance_title),
                        message = stringResource(Res.string.app_maintenance_message),
                        actionLabel = stringResource(Res.string.ds_try_again),
                        onAction = viewModel::onRetry,
                        modifier = Modifier.padding(padding),
                    )

                AppDestination.UpdateRequired -> {
                    val uriHandler = LocalUriHandler.current
                    AppGateScreen(
                        title = stringResource(Res.string.app_update_title),
                        message = stringResource(Res.string.app_update_message),
                        actionLabel = stringResource(Res.string.app_update_action),
                        onAction = { storeUrl()?.let(uriHandler::openUri) },
                        modifier = Modifier.padding(padding),
                    )
                }

                AppDestination.Main ->
                    AppNavHost(
                        navController = navController,
                        // Screens apply their own safe-drawing padding, so consume what the Scaffold inset.
                        modifier = Modifier.padding(padding).consumeWindowInsets(padding),
                    )
            }
        }
    }
}
