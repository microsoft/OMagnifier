/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License.
 */

package com.microsoft.office.outlook.magnifierlib.frame

import android.app.Application
import android.app.Service
import android.view.WindowManager

class FPSMonitorConfig private constructor(
    val context: Application,
    val refreshRate: Float,
    val mediumPercentage: Float,
    val lowPercentage: Float
) {

    data class Builder(
        val context: Application,
        var mediumPercentage: Float,
        var lowPercentage: Float,
        var refreshRate: Float
    ) {
        constructor(context: Application) : this(
            context,
            FLAG_PERCENTAGE_YELLOW,
            FLAG_PERCENTAGE_RED,
            (context.getSystemService(Service.WINDOW_SERVICE) as WindowManager).defaultDisplay.refreshRate
        )

        fun mediumPercentage(mediumPercentage: Float) = apply { this.mediumPercentage = mediumPercentage }
        fun lowPercentage(lowPercentage: Float) = apply { this.lowPercentage = lowPercentage }
        fun refreshRate(refreshRate: Float) = apply { this.refreshRate = refreshRate }
        fun build() = FPSMonitorConfig(context, refreshRate, mediumPercentage, lowPercentage)
    }

    companion object {
        const val FLAG_PERCENTAGE_RED = 2 / 3f
        const val FLAG_PERCENTAGE_YELLOW = 5 / 6f
    }
}



