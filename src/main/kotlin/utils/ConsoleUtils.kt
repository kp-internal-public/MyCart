package utils

fun clearScreen() {
    try {
        if (System.getProperty("sun.desktop") == "windows") {
            ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor()
        } else {
            ProcessBuilder("clear").inheritIO().start().waitFor()
        }
    } catch (e : Exception) { /* no-op */ }
}