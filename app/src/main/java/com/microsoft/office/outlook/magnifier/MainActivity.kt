/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License.
 */

package com.microsoft.office.outlook.magnifier

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import com.microsoft.office.outlook.magnifierlib.Magnifier
import com.microsoft.office.outlook.magnifierlib.frame.FPSMonitorConfig
import com.microsoft.office.outlook.magnifierlib.memory.FileDescriptorInfo
import com.microsoft.office.outlook.magnifierlib.memory.HeapMemoryInfo
import com.microsoft.office.outlook.magnifierlib.memory.MemoryMonitor
import com.microsoft.office.outlook.magnifierlib.memory.MemoryMonitorConfig
import com.microsoft.office.outlook.magnifierlib.memory.ThreadInfo

class MainActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val buttonStartFPS = findViewById<Button>(R.id.button_fps_start)
        val buttonStopFPS = findViewById<Button>(R.id.button_fps_stop)
        val buttonSleep = findViewById<Button>(R.id.button_fps_sleep)
        val buttonStartMemory = findViewById<Button>(R.id.button_start_memory)
        val buttonStopMemory = findViewById<Button>(R.id.button_stop_memory)
        val buttonIncreaseMemory = findViewById<Button>(R.id.button_increase_memory)
        val buttonDumpMemory = findViewById<Button>(R.id.button_dump_memory_immediately)

        buttonStartFPS.setOnClickListener {
            Magnifier.startMonitorFPS(
                FPSMonitorConfig.Builder(this.application).lowPercentage(40 / 60f)  // show red tips, (2.0f / 3.0f) by default
                    .mediumPercentage(50 / 60f) // show yellow tips, (5.0f / 6.0f) by default
                    .refreshRate(60f) // defaultDisplay.refreshRate by default
                    .build()
            )
            Log.i(TAG, "after startMonitorFPS isEnabledFPSMonitor: ${Magnifier.isEnabledFPSMonitor()}")
        }

        buttonStopFPS.setOnClickListener {
            Magnifier.stopMonitorFPS()
            Log.i(TAG, "after stopMonitorFPS isEnabledFPSMonitor: ${Magnifier.isEnabledFPSMonitor()}")
        }

        buttonSleep.setOnClickListener {
            Thread.sleep(100)
        }

        buttonStartMemory.setOnClickListener {
            Log.i(TAG, "before startMonitorMemory isEnableMemoryMonitor: ${Magnifier.isEnabledMemoryMonitor()}")
            Magnifier.startMonitorMemory(
                MemoryMonitorConfig.Builder().enableExceedLimitSample(
                    0.8f, // the benchmark for Exceed_Limit type sampler, if we reach out 80% the max, collect the metrics, 0.8f by default
                    10000 // the threshold for Exceed_Limit type sampler, 10s by default
                ).enableTimingSample(60 * 1000) // threshold for the timing checker, 1 min by default
                    .onSampleListener(object : MemoryMonitor.OnSampleListener {
                        override fun onSampleHeap(
                            heapMemoryInfo: HeapMemoryInfo,
                            sampleType: MemoryMonitor.SampleType
                        ) {
                            Log.d(TAG, "heapMemoryInfo:$heapMemoryInfo,sampleType:$sampleType")
                        }

                        override fun onSampleFile(
                            fileDescriptorInfo: FileDescriptorInfo,
                            sampleType: MemoryMonitor.SampleType
                        ) {
                            Log.d(TAG, "fileDescriptorInfo:${fileDescriptorInfo.fdMaxCount},sampleType:$sampleType")
                        }

                        override fun onSampleThread(
                            threadInfo: ThreadInfo,
                            sampleType: MemoryMonitor.SampleType
                        ) {
                            Log.d(TAG, "threadInfo:${threadInfo.threadsCount},sampleType:$sampleType")
                        }
                    }).build()
            )
            Log.i(TAG, "after startMonitorMemory isEnableMemoryMonitor: ${Magnifier.isEnabledMemoryMonitor()}")
        }

        buttonStopMemory.setOnClickListener {
            Log.i(TAG, "before stopMonitorMemory isEnableMemoryMonitor: ${Magnifier.isEnabledMemoryMonitor()}")
            Magnifier.stopMonitorMemory()
            Log.i(TAG, "after stopMonitorMemory isEnableMemoryMonitor: ${Magnifier.isEnabledMemoryMonitor()}")
        }

        buttonIncreaseMemory.setOnClickListener {
            val list = ArrayList<Any>()
            Thread {
                val used = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()
                val totalFree = Runtime.getRuntime().maxMemory() - used
                val arr = ByteArray((totalFree * 0.85).toInt()){
                    1
                }
                list.add(arr)
            }.start()
        }

        buttonDumpMemory.setOnClickListener {
            Magnifier.dumpMemoryImmediately(object : MemoryMonitor.OnSampleListener {
                override fun onSampleHeap(
                    heapMemoryInfo: HeapMemoryInfo,
                    sampleType: MemoryMonitor.SampleType
                ) {
                    Log.d(TAG, "heapMemoryInfo:$heapMemoryInfo,sampleType:$sampleType")
                }

                override fun onSampleFile(
                    fileDescriptorInfo: FileDescriptorInfo,
                    sampleType: MemoryMonitor.SampleType
                ) {
                    Log.d(TAG, "fileDescriptorInfo:${fileDescriptorInfo.fdMaxCount},sampleType:$sampleType")
                }

                override fun onSampleThread(
                    threadInfo: ThreadInfo,
                    sampleType: MemoryMonitor.SampleType
                ) {
                    Log.d(TAG, "threadInfo:${threadInfo.threadsCount},sampleType:$sampleType")
                }
            })
        }
    }

    companion object {
        private const val TAG = "MainActivity"
    }
}