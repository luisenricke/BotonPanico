package com.luisenricke.androidext.room

import java.util.concurrent.Executors


private val IO_EXECUTOR = Executors.newSingleThreadExecutor()

/**
 * Utility method to run blocks on a dedicated background thread, used for io/database work.
 */
@Suppress("unused")
fun ioThread(f : () -> Unit) {
    IO_EXECUTOR.execute(f)
}
