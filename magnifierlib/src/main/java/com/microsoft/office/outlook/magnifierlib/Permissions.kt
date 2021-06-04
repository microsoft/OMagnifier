/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License.
 */

package com.microsoft.office.outlook.magnifierlib

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings

fun drawOverlaysPermission(context: Context): Boolean {
    return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(context).also {
        if (!it) {
            val intent = Intent(
                Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + context.packageName)
            )
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            context.startActivity(intent)
        }
    }
}