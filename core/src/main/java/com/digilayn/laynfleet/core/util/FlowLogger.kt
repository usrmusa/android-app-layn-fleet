package com.digilayn.laynfleet.core.util

import android.util.Log

object FlowLogger {
    private const val TAG = "digilayn-flow-logs"

    fun d(source: String, message: String) {
        Log.d(TAG, "[$source] $message")
    }

    fun e(source: String, message: String, throwable: Throwable? = null) {
        Log.e(TAG, "[$source] $message", throwable)
    }

    fun i(source: String, message: String) {
        Log.i(TAG, "[$source] $message")
    }
}
