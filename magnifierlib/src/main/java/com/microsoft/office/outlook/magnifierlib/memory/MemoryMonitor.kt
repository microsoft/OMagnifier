/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License.
 */

package com.microsoft.office.outlook.magnifierlib.memory

import android.os.Handler
import android.os.HandlerThread

class MemoryMonitor {

    private val handlerThread: HandlerThread = HandlerThread(HANDLER_THREAD_MEMORY_MONITOR).apply { this.start() }

    private val handler: Handler = Handler(handlerThread.looper)

    private val factory = MemorySamplersFactory()

    private var syncSamplerList: ArrayList<MemorySampler<out Any>> = ArrayList()

    private var asyncSamplerList: ArrayList<MemorySampler<out Any>> = ArrayList()

    @Volatile
    private var isMonitorEnabled = false

    @Synchronized
    fun start(config: MemoryMonitorConfig) {

        if (isMonitorEnabled) {
            return
        }

        isMonitorEnabled = true

        asyncSamplerList = factory.createSamplers(config, handler)

        for (memorySampler in asyncSamplerList) {
            memorySampler.start()
        }
    }

    @Synchronized
    fun stop() {

        if (!isMonitorEnabled) {
            return
        }

        isMonitorEnabled = false

        for (memorySampler in asyncSamplerList) {
            memorySampler.stop()
        }

        asyncSamplerList.clear()
    }

    @Synchronized
    fun dumpImmediately(onSampleListener: OnSampleListener) {

        if (syncSamplerList.isEmpty()) {
            syncSamplerList = factory.createSamplers(
                MemoryMonitorConfig.Builder(MemoryMonitorConfig.MemoryMonitorType.SYNC).onSampleListener(onSampleListener).build(), handler
            )
        }

        for (memorySampler in syncSamplerList) {
            memorySampler.start()
        }
    }

    @Synchronized
    fun isMonitorEnabled(): Boolean {
        return isMonitorEnabled
    }

    interface IMemoryMonitor {

        fun start()

        fun stop()
    }

    interface OnSampleListener {

        fun onSampleHeap(
            heapMemoryInfo: HeapMemoryInfo,
            sampleType: SampleType
        )

        fun onSampleFile(
            fileDescriptorInfo: FileDescriptorInfo,
            sampleType: SampleType
        )

        fun onSampleThread(
            threadInfo: ThreadInfo,
            sampleType: SampleType
        )
    }

    enum class SampleType {
        TIMING, EXCEED_LIMIT, IMMEDIATE
    }

    companion object {
        private const val HANDLER_THREAD_MEMORY_MONITOR = "magnifier_memory"
    }
}