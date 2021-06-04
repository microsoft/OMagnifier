/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License.
 */

package com.microsoft.office.outlook.magnifierlib.memory

class TimingSamplePolicy(private val threshold: Long) : ISamplePolicy {
    override fun needSample(): Boolean {
        return true
    }

    override fun needPostDelayed(): Boolean {
        return true
    }

    override fun postDelayedThreshold(): Long {
        return threshold
    }
}

class ImmediateSamplePolicy() : ISamplePolicy {
    override fun needSample(): Boolean {
        return true
    }

    override fun needPostDelayed(): Boolean {
        return false
    }

    override fun postDelayedThreshold(): Long {
        return Long.MAX_VALUE
    }
}

class ExceedLimitSamplePolicy(
    private val threshold: Long,
    private val needSampleFunc: () -> Boolean
) : ISamplePolicy {
    override fun needSample(): Boolean {
        return needSampleFunc.invoke()
    }

    override fun needPostDelayed(): Boolean {
        return true
    }

    override fun postDelayedThreshold(): Long {
        return threshold
    }
}