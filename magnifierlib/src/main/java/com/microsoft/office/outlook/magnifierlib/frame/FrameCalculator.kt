/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License.
 */

package com.microsoft.office.outlook.magnifierlib.frame

import android.os.Handler
import android.os.HandlerThread
import android.os.Message
import android.view.Choreographer
import java.util.LinkedList

class FrameCalculator(
    private val listener: (frameCount: Int) -> Unit
) : Choreographer.FrameCallback {

    private val frameList = LinkedList<Long>()

    private var handlerThread: HandlerThread = HandlerThread(MAGNIFIER_FRAME_CALLBACK_THREAD_NAME).also { it.start() }

    private var handler: Handler = object : Handler(handlerThread.looper) {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            when (msg.what) {
                HANDLE_MSG -> notifyListener()
            }
        }
    }

    override fun doFrame(frameTimeNanos: Long) {
        frameList.add(frameTimeNanos)
        Choreographer.getInstance().postFrameCallback(this)
    }

    fun start() {
        Choreographer.getInstance().postFrameCallback(this)
        frameList.clear()
        handler.sendEmptyMessageDelayed(HANDLE_MSG, THRESHOLD_IN_MS)
    }

    fun stop() {
        Choreographer.getInstance().removeFrameCallback(this)
        frameList.clear()
        handler.removeCallbacksAndMessages(null)
        handlerThread.quit()
        handlerThread.interrupt()
    }

    private fun notifyListener() {
        listener.invoke(frameList.size)
        frameList.clear()
        handler.sendEmptyMessageDelayed(HANDLE_MSG, THRESHOLD_IN_MS)
    }

    companion object {
        private const val HANDLE_MSG = 0
        private const val THRESHOLD_IN_MS = 1000L
        private const val MAGNIFIER_FRAME_CALLBACK_THREAD_NAME = "magnifier_frame_callback_thread"
    }
}