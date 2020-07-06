package com.luisenricke.kotlinext

@Suppress("unused")
fun String.formatPhone(): String {
    val filter = this.filter { it.isDigit() }

    val result = filter.filterIndexed { index, c ->
        index >= filter.length - 10 && index <= filter.length
    }

    return "${result.slice(0..2)}-${result.slice(3..5)}-${result.slice(6..9)}"
}
