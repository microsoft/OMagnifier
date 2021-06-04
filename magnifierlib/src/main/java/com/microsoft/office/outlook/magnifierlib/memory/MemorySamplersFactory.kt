/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License.
 */

package com.microsoft.office.outlook.magnifierlib.memory

import android.os.Handler

class MemorySamplersFactory {

    fun createSamplers(
        config: MemoryMonitorConfig,
        handler: Handler
    ): ArrayList<MemorySampler<out Any>> {

        return when (config.monitorType) {
            MemoryMonitorConfig.MemoryMonitorType.ASYNC -> createAsyncSamplers(config, handler)
            MemoryMonitorConfig.MemoryMonitorType.SYNC -> createSyncSamplers(config, handler)
        }
    }

    private fun createAsyncSamplers(
        config: MemoryMonitorConfig,
        handler: Handler
    ): ArrayList<MemorySampler<out Any>> {

        val list = ArrayList<MemorySampler<out Any>>()

        if (config.openTimingSample) {

            val timingHeapSampler = MemorySampler(HeapMetricCollector(), TimingSamplePolicy(config.timingThreshold), handler) {
                config.onSampleListener?.onSampleHeap(it, MemoryMonitor.SampleType.TIMING)
            }

            val timingFileSampler = MemorySampler(FileDescriptorMetricCollector(), TimingSamplePolicy(config.timingThreshold), handler) {
                config.onSampleListener?.onSampleFile(it, MemoryMonitor.SampleType.TIMING)
            }

            val timingThreadSampler = MemorySampler(ThreadMetricCollector(), TimingSamplePolicy(config.timingThreshold), handler) {
                config.onSampleListener?.onSampleThread(it, MemoryMonitor.SampleType.TIMING)
            }

            list.add(timingHeapSampler)
            list.add(timingFileSampler)
            list.add(timingThreadSampler)
        }

        // create the top hit samplers
        if (config.openExceedLimitSample) {

            // build the top hit heap sampler
            val topHitHeapNeedSampleFunc: () -> Boolean = {
                (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()).toFloat() >= Runtime.getRuntime()
                    .maxMemory() * config.exceedLimitRatio
            }
            val topHitHeapSampler = MemorySampler(
                HeapMetricCollector(), ExceedLimitSamplePolicy(config.exceedLimitThreshold, topHitHeapNeedSampleFunc), handler
            ) {
                config.onSampleListener?.onSampleHeap(it, MemoryMonitor.SampleType.EXCEED_LIMIT)
            }

            // build the top hit file sampler
            val topHitFileNeedSampleFunc: () -> Boolean = { readFileDescriptors().size.toFloat() >= readMaxOpenFiles() * config.exceedLimitRatio }
            val topHitFileSampler =
                MemorySampler(FileDescriptorMetricCollector(), ExceedLimitSamplePolicy(config.exceedLimitThreshold, topHitFileNeedSampleFunc), handler) {
                    config.onSampleListener?.onSampleFile(it, MemoryMonitor.SampleType.EXCEED_LIMIT)
                }

            // build the top hit thread sampler
            val topHitThreadNeedSampleFunc: () -> Boolean = { readThreadsCount() >= MAX_THREADS_COUNT * config.exceedLimitRatio }
            val topHitThreadSampler =
                MemorySampler(ThreadMetricCollector(), ExceedLimitSamplePolicy(config.exceedLimitThreshold, topHitThreadNeedSampleFunc), handler) {
                    config.onSampleListener?.onSampleThread(it, MemoryMonitor.SampleType.EXCEED_LIMIT)
                }

            list.add(topHitHeapSampler)
            list.add(topHitFileSampler)
            list.add(topHitThreadSampler)
        }

        return list
    }

    private fun createSyncSamplers(
        config: MemoryMonitorConfig,
        handler: Handler
    ): ArrayList<MemorySampler<out Any>> {

        val list = ArrayList<MemorySampler<out Any>>()

        // for just dumping once
        val immediateHeapSampler = MemorySampler(HeapMetricCollector(), ImmediateSamplePolicy(), handler) {
            config.onSampleListener?.onSampleHeap(it, MemoryMonitor.SampleType.IMMEDIATE)
        }

        val immediateFileSampler = MemorySampler(FileDescriptorMetricCollector(), ImmediateSamplePolicy(), handler) {
            config.onSampleListener?.onSampleFile(it, MemoryMonitor.SampleType.IMMEDIATE)
        }

        val immediateThreadSampler = MemorySampler(ThreadMetricCollector(), ImmediateSamplePolicy(), handler) {
            config.onSampleListener?.onSampleThread(it, MemoryMonitor.SampleType.IMMEDIATE)
        }

        list.add(immediateHeapSampler)
        list.add(immediateFileSampler)
        list.add(immediateThreadSampler)

        return list
    }

    companion object {
        private const val MAX_THREADS_COUNT = 1000
    }
}