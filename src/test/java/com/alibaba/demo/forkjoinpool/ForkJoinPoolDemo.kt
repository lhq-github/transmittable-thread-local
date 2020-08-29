package com.alibaba.demo.forkjoinpool

import java.util.concurrent.ForkJoinPool
import java.util.concurrent.RecursiveTask

/**
 * ForkJoinPool use demo.
 *
 * @author Jerry Lee (oldratlee at gmail dot com)
 */
fun main() {
    val pool = ForkJoinPool.commonPool()

    val result = pool.invoke(SumTask(1..1000))

    println("computed result: $result") // result is 500500
}

internal class SumTask(private val numbers: IntRange, private val forkLevel: Int = 0) : RecursiveTask<Int>() {
    override fun compute(): Int =
        if (numbers.count() <= 16) {
            println(String.format("direct compute %9s[%4s] at fork level %2s @ thread ${Thread.currentThread().name}",
                numbers, numbers.count(), forkLevel))

            // compute directly
            numbers.sum()
        } else {
            println(String.format("fork   compute %9s[%4s] at fork level %2s @ thread ${Thread.currentThread().name}",
                numbers, numbers.count(), forkLevel))

            // split task
            val middle = numbers.first + numbers.count() / 2
            val nextForkLevel = forkLevel + 1
            val taskLeft = SumTask(numbers.first until middle, nextForkLevel)
            val taskRight = SumTask(middle..numbers.last, nextForkLevel)

            // fork-join compute
            taskLeft.fork()
            taskRight.fork()
            taskLeft.join() + taskRight.join()
        }
}
