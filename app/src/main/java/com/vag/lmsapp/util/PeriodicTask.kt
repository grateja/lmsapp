package com.vag.lmsapp.util

suspend fun Int.periodicAsyncTask(
    steps: Int,
    task: suspend (step: Boolean, iteration: Int) -> Unit
) {
    if(this <= 0 || steps <= 0) return

    val increment = this / steps
    var work = 1

    for (i in 1..this) {
        // Task that runs when work == current

        val current = if(increment > 1)
            Math.floorDiv(i, increment)
        else
            work

        val stepped = work == current
        if (stepped) {
            work++
        }

        task(stepped, i)
    }
}