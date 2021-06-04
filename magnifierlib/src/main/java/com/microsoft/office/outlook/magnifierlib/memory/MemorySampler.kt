/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License.
 */

package com.microsoft.office.outlook.magnifierlib.memory

import android.os.Handler

class MemorySampler<T>(
    private val collector: IMetricCollector<T>,
    private val policy: ISamplePolicy,
    private val handler: Handler,
    private val onSampleListener: (t: T) -> Unit
) : Runnable, MemoryMonitor.IMemoryMonitor {

    override fun run() {
        if (policy.needSample()) {
            val sample = collector.collect()
            onSampleListener.invoke(sample)
        }
        if (policy.needPostDelayed()) {
            handler.postDelayed(this, policy.postDelayedThreshold())
        }
    }

    override fun start() {
        handler.post(this)
    }

    override fun stop() {
        handler.removeCallbacks(this)
    }
}

interface IMetricCollector<T> {
    fun collect(): T
}

interface ISamplePolicy {
    fun needSample(): Boolean

    fun needPostDelayed(): Boolean

    fun postDelayedThreshold(): Long
}
