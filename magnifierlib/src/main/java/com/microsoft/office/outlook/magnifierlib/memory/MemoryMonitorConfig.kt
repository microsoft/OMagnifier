/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License.
 */

package com.microsoft.office.outlook.magnifierlib.memory

class MemoryMonitorConfig private constructor(
    val monitorType: MemoryMonitorType,
    val openTimingSample: Boolean,
    val openExceedLimitSample: Boolean,
    val timingThreshold: Long,
    val exceedLimitRatio: Float,
    val exceedLimitThreshold: Long,
    val onSampleListener: MemoryMonitor.OnSampleListener?
) {

    data class Builder(
        val monitorType: MemoryMonitorType,
        var openTimingSample: Boolean = false,
        var openExceedLimitSample: Boolean = false,
        var timingThreshold: Long = DEFAULT_TIMING_THRESHOLD_1_MIN,
        var exceedLimitRatio: Float = DEFAULT_EXCEED_LIMIT_RATIO_80_PERCENTAGE,
        var exceedLimitThreshold: Long = DEFAULT_EXCEED_LIMIT_RATIO_THRESHOLD_10_SECONDS,
        var onSampleListener: MemoryMonitor.OnSampleListener? = null
    ) {

        constructor(monitorType: MemoryMonitorType = MemoryMonitorType.ASYNC) : this(
            monitorType,
            false,
            false,
            DEFAULT_TIMING_THRESHOLD_1_MIN,
            DEFAULT_EXCEED_LIMIT_RATIO_80_PERCENTAGE,
            DEFAULT_EXCEED_LIMIT_RATIO_THRESHOLD_10_SECONDS,
            null
        )

        fun enableTimingSample(
            timingThreshold: Long
        ) = apply {
            this.openTimingSample = true
            this.timingThreshold = timingThreshold
        }

        fun enableExceedLimitSample(
            exceedLimitRatio: Float,
            exceedLimitThreshold: Long
        ) = apply {
            this.openExceedLimitSample = true
            this.exceedLimitRatio = exceedLimitRatio
            this.exceedLimitThreshold = exceedLimitThreshold
        }

        fun onSampleListener(onSampleListener: MemoryMonitor.OnSampleListener) = apply { this.onSampleListener = onSampleListener }

        fun build() =
            MemoryMonitorConfig(monitorType, openTimingSample, openExceedLimitSample, timingThreshold, exceedLimitRatio, exceedLimitThreshold, onSampleListener)
    }

    enum class MemoryMonitorType {
        ASYNC, SYNC
    }

    companion object {
        private const val DEFAULT_TIMING_THRESHOLD_1_MIN = 1000 * 60L
        private const val DEFAULT_EXCEED_LIMIT_RATIO_80_PERCENTAGE = 0.8f
        private const val DEFAULT_EXCEED_LIMIT_RATIO_THRESHOLD_10_SECONDS = 1000 * 10L
    }
}