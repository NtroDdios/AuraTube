package com.auratube.app.ui.extensions

import androidx.core.view.isGone
import com.auratube.app.R
import com.auratube.app.constants.PreferenceKeys
import com.auratube.app.helpers.PreferenceHelper
import com.google.android.material.button.MaterialButton

fun MaterialButton.setupNotificationBell(channelId: String) {
    if (!PreferenceHelper.getBoolean(PreferenceKeys.NOTIFICATION_ENABLED, true)) {
        isGone = true
        return
    }

    var isIgnorable = PreferenceHelper.isChannelNotificationIgnorable(channelId)
    setIconResource(iconResource(isIgnorable))

    setOnClickListener {
        isIgnorable = !isIgnorable
        PreferenceHelper.toggleIgnorableNotificationChannel(channelId)
        setIconResource(iconResource(isIgnorable))
    }
}

private fun iconResource(isIgnorable: Boolean) =
    if (isIgnorable) R.drawable.ic_bell else R.drawable.ic_notification