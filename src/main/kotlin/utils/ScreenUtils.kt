package utils

import kotlin.system.exitProcess

object ScreenUtils {
    fun parseNumberInput(onReload: () -> Unit) : Int {
        try {
            return readLine()?.toInt() ?: throw NumberFormatException("Incorrect")
        } catch (e: NumberFormatException) {
            showIncorrectOption(onReload)
            exitProcess(1)
        }
    }

    fun parseTextInput(onReload: () -> Unit) : String {
        val data = readLine()
        if (data.isNullOrBlank()) {
            showIncorrectOption(onReload)
            exitProcess(1)
        }
        return data
    }

    fun showIncorrectOption(onReload: () -> Unit) {
        println()
        println("Error: Incorrect option! Retry (y/n)?")
        val input = readLine()
        if (input?.toLowerCase() == "y") onReload.invoke()
    }
}