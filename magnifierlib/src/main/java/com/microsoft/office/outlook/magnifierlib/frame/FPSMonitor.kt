/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License.
 */

package com.microsoft.office.outlook.magnifierlib.frame

import android.annotation.SuppressLint
import androidx.annotation.UiThread
import com.microsoft.office.outlook.magnifierlib.drawOverlaysPermission

class FPSMonitor {

    private var frameCalculator: FrameCalculator? = null

    @SuppressLint("StaticFieldLeak")
    private var frameViewer: FrameViewer? = null

    @UiThread
    @Synchronized
    fun startMonitorFPS(config: FPSMonitorConfig) {
        if (drawOverlaysPermission(config.context)) {
            return
        }

        if (isFPSMonitorEnabled()) {
            return
        }

        frameViewer = FrameViewer(config.context, config.refreshRate, config.mediumPercentage, config.lowPercentage)
        frameCalculator = FrameCalculator {
            frameViewer?.display(it)
        }

        frameViewer?.show()
        frameCalculator?.start()
    }

    @UiThread
    @Synchronized
    fun stopMonitorFPS() {
        if (!isFPSMonitorEnabled()) {
            return
        }

        frameViewer?.hide()
        frameCalculator?.stop()

        frameViewer = null
        frameCalculator = null
    }

    @Synchronized
    fun isFPSMonitorEnabled(): Boolean {
        return frameViewer != null && frameCalculator != null
    }
}