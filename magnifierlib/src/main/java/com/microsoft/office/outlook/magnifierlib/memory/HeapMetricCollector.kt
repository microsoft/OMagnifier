/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License.
 */

package com.microsoft.office.outlook.magnifierlib.memory

import android.os.Debug

class HeapMetricCollector : IMetricCollector<HeapMemoryInfo> {

    override fun collect(): HeapMemoryInfo {
        return HeapMemoryInfo(
            maxMemoryMB = Runtime.getRuntime().maxMemory() / M,
            usedMemoryMB = (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / M,
            pssMemoryMB = Debug.getPss() / K,
            vssMemoryMB = readVss() / K,
            rssMemoryMB = readVmRss() / K
        )
    }

    companion object {
        private const val K = 1024
        private const val M = K * K
    }
}

data class HeapMemoryInfo(
    val maxMemoryMB: Long,
    val usedMemoryMB: Long,
    val pssMemoryMB: Long,
    val vssMemoryMB: Long,
    val rssMemoryMB: Long
)
