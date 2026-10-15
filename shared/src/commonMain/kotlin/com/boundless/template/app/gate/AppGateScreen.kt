package com.boundless.template.app.gate

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.boundless.template.core.common.AppPlatform
import com.boundless.template.core.common.currentPlatform
import com.boundless.template.core.designsystem.component.AppEmptyState
import com.boundless.template.core.designsystem.component.AppPrimaryButton
import com.boundless.template.core.designsystem.theme.Spacing

// TODO(template): set your store listings.
private const val PLAY_STORE_URL = "https://play.google.com/store/apps/details?id=com.boundless.template"
private const val APP_STORE_URL = "https://apps.apple.com/app/id0000000000"

/** Where an outdated build can be updated; null where there is no store (desktop, web). */
internal fun storeUrl(platform: AppPlatform = currentPlatform): String? =
    when (platform) {
        AppPlatform.ANDROID -> PLAY_STORE_URL
        AppPlatform.IOS -> APP_STORE_URL
        AppPlatform.DESKTOP, AppPlatform.WEB -> null
    }

/** Full-screen block with no way into the app, used for maintenance and for retired builds. */
@Composable
internal fun AppGateScreen(
    title: String,
    message: String,
    actionLabel: String,
    onAction: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxSize().padding(Spacing.screen),
        verticalArrangement = Arrangement.Center,
    ) {
        AppEmptyState(
            title = title,
            body = message,
            action = { AppPrimaryButton(text = actionLabel, onClick = onAction) },
        )
    }
}
