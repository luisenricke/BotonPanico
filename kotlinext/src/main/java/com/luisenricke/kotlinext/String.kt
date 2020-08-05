package com.luisenricke.kotlinext

fun String.removeWhiteSpaces(): String {
    return this.replace("\\s".toRegex(), "")
}
