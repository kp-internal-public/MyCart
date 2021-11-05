import data.types.StorageType
import ui.adminScreen
import ui.createUserScreen
import utils.ScreenUtils
import utils.clearScreen
import kotlin.system.exitProcess

object AppSettings {
    var storageType : StorageType = StorageType.PERSISTENT
}

fun main(args: Array<String>) {
    startScreen()
}

fun startScreen() {
    clearScreen()
    println("Welcome to MyCart application")
    println("-----------------------------")
    println()
    println("[*] Select client type to continue")
    println()
    println("1. User (able to view, buy products)")
    println("2. Admin (controller)")
    println()
    println("0. Exit")
    println()
    print("Choose: ")

    when(ScreenUtils.parseNumberInput { startScreen() }) {
        0 -> exitProcess(0)
        1 -> createUserScreen()
        2 -> adminScreen()
        else -> ScreenUtils.showIncorrectOption { startScreen() }
    }
    exitProcess(1)
}
