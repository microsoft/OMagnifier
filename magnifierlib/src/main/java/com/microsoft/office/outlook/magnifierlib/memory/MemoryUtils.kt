/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License.
 */

@file:JvmName("MemoryUtils")

package com.microsoft.office.outlook.magnifierlib.memory

import android.os.Build
import android.os.Process
import android.system.Os
import java.io.File
import java.io.RandomAccessFile
import java.util.ArrayList
import java.util.regex.Pattern

private const val INDEX_PROC_STATUS = 1
private const val INDEX_PROC_LIMITS = 3

fun readVss(): Long {
    return readFieldFromProcFile("VmSize", INDEX_PROC_STATUS, "/proc/${Process.myPid()}/status")
}

fun readVmRss(): Long {
    return readFieldFromProcFile("VmRSS", INDEX_PROC_STATUS, "/proc/${Process.myPid()}/status")
}

fun readThreadsCount(): Long {
    return readFieldFromProcFile("Threads", INDEX_PROC_STATUS, "/proc/${Process.myPid()}/status")
}

fun readMaxOpenFiles(): Long {
    return readFieldFromProcFile("Max open files", INDEX_PROC_LIMITS, "/proc/${Process.myPid()}/limits")
}

fun readFileDescriptors(): List<String> {
    return ArrayList<String>().also {
        val listFiles: Array<File> = File("proc/${Process.myPid()}/fd").listFiles() ?: arrayOf()
        for (file in listFiles) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                try {
                    it.add(Os.readlink(file.absolutePath))
                } catch (swallowed: Exception) {
                }
            }
        }
    }
}

private fun readFieldFromProcFile(
    fieldName: String,
    fieldIndex: Int,
    fileName: String
): Long {
    val file = RandomAccessFile(fileName, "r")
    file.use {
        var line = file.readLine()
        while (line != null) {
            if (line.startsWith(fieldName)) {
                val arr = Pattern.compile("\\s+").split(line, 0)
                if (arr.size > 1) {
                    return arr[fieldIndex].toLong()
                }
            }
            line = file.readLine()
        }
    }
    return 0
}



