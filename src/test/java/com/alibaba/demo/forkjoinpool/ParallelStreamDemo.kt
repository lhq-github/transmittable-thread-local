package com.alibaba.demo.forkjoinpool

import java.util.concurrent.ConcurrentSkipListSet

/**
 * Parallel Stream use demo.
 *
 * @author Jerry Lee (oldratlee at gmail dot com)
 */
fun main() {
    println("availableProcessors: ${Runtime.getRuntime().availableProcessors()}")

    val threadNames: MutableSet<String> = ConcurrentSkipListSet()

    (0..100).toList().stream().parallel().mapToInt {
        threadNames.add(Thread.currentThread().name)
        Thread.sleep(10)
        println("map $it @ thread ${Thread.currentThread().name}")

        it
    }.sum().let {
        println("sum result: $it")
    }

    println(threadNames.joinToString(
        separator = "\n\t",
        prefix = "run threads(${threadNames.size}):\n\t"
    ))
}
