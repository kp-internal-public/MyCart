package ui

import utils.*
import startScreen
import data.Admin
import data.Controller
import data.models.Product
import data.types.StorageType
import kotlin.system.exitProcess

fun adminScreen() {
    clearScreen()
    println()
    println("[*] Browse from the following options")
    println("-------------------------------------")
    println()
    println("1. Manage Categories")
    println("2. Manage Products")
    println()
    println("3. User Info")
    println()
    println("0. Logout")
    println()
    print("Choose: ")
    when(ScreenUtils.parseNumberInput { startScreen() }) {
        0 -> startScreen()
        1 -> adminCategoryScreen()
        2 -> adminProductScreen()
        3 -> adminSelectUserInfoScreen()
        else -> ScreenUtils.showIncorrectOption { adminScreen() }
    }
    exitProcess(1)
}

fun adminSelectUserInfoScreen() {
    clearScreen()

    val adminController = Controller.get<Admin>(AppSettings.storageType)
    println()
    if (adminController.getAllUsers().isEmpty()) {
        println("No user exist in database, press any key...")
        readLine()
        adminScreen()
        return
    }
    println("[*] Select a user")
    println()
    adminController.getAllUsers().forEachIndexed { index, pair -> println("${index + 1}. ${pair.first}") }
    println()
    print("Choose: ")
    val index = ScreenUtils.parseNumberInput { adminSelectUserInfoScreen() }
    adminUserInfoScreen(adminController.getAllUsers()[index - 1].first)
}

fun adminUserInfoScreen(name: String) {
    clearScreen()

    println()
    println("[*] Select options from below for User: $name")
    println()
    println("1. List all items in the cart")
    println("2. List all purchased item")
    println()
    println("0. Return")
    println()
    print("Choose: ")
    when(ScreenUtils.parseNumberInput { adminUserInfoScreen(name) }) {
        0 -> adminScreen()
        1 -> adminUserInfoCartScreen(name)
        2 -> adminUserInfoBillScreen(name)
        else -> ScreenUtils.showIncorrectOption { adminScreen() }
    }
    exitProcess(1)
}

fun adminUserInfoCartScreen(name: String) {
    clearScreen()

    val adminController = Controller.get<Admin>(AppSettings.storageType)
    val userController = adminController.getUser(name).second
    println()
    println("$name Cart")
    println()
    if (userController.getCart().isEmpty()) {
        println("Error: Cart is empty, press any key...")
        readLine()
        adminUserInfoScreen(name)
        return
    }
    userController.getCart().forEachIndexed { index, product -> println("$index. $product") }
    println()
    println("0. Return")
    println()
    print("Choose: ")
    val input = ScreenUtils.parseNumberInput { adminUserInfoCartScreen(name) }
    if (input == 0) {
        adminUserInfoScreen(name)
        return
    }
    exitProcess(1)
}

fun adminUserInfoBillScreen(name: String) {
    clearScreen()

    val adminController = Controller.get<Admin>(AppSettings.storageType)
    val userController = adminController.getUser(name).second
    println()
    println("$name Bills")
    println()
    if (userController.getBills().isEmpty()) {
        println("Error: Purchases are empty, press any key...")
        readLine()
        adminUserInfoScreen(name)
        return
    }
    userController.getBills().forEachIndexed { index, bill -> println("${index + 1}. $bill\n") }
    println()
    println("0. Return")
    println()
    print("Choose: ")
    val input = ScreenUtils.parseNumberInput { adminUserInfoBillScreen(name) }
    if (input == 0) {
        adminUserInfoScreen(name)
        return
    }
    exitProcess(1)
}

fun adminCategoryScreen() {
    val adminController = Controller.get<Admin>(AppSettings.storageType)
    adminViewCommonScreen(
        type = "Category",
        formatted = adminController.getCategories(),
        add = ::adminAddCategoryScreen,
        remove = { index ->
            val category = adminController.getCategories()[index]
            adminController.removeCategory(category)
            println("Category \"$category\" removed, press any key...")
            readLine()
            adminCategoryScreen()
        }
    )
}

fun adminAddCategoryScreen() {
    clearScreen()

    val adminController = Controller.get<Admin>(AppSettings.storageType)
    println()
    println("[*] Add a new category (x to return): ")
    println()
    print("Name: ")

    val category = ScreenUtils.parseTextInput { adminAddCategoryScreen() }
    if (category == "x") {
        adminCategoryScreen()
        return
    }
    if (adminController.getCategories().contains(category)) {
        println()
        println("Error: Category already exist, press any key...")
        readLine()
        adminCategoryScreen()
        return
    }
    adminController.addCategory(category)
    println()
    println("Category \"${category}\" added, press any key...")
    readLine()
    adminAddCategoryScreen()
}

fun adminProductScreen() {
    val adminController = Controller.get<Admin>(AppSettings.storageType)
    adminViewCommonScreen(
        type = "Product",
        formatted = adminController.getProducts().map { it.toString() },
        add = ::adminAddProductScreen,
        remove = { index ->
            val product = adminController.getProducts()[index]
            adminController.removeProduct(product)
            println("Product \"$product\" removed, press any key...")
            readLine()
            adminProductScreen()
        }
    )
}

fun adminAddProductScreen() {
    clearScreen()

    val adminController = Controller.get<Admin>(AppSettings.storageType)
    if (adminController.getCategories().isEmpty()) {
        println()
        println("Cannot add new product when categories are empty. Kindly add some category")
        println("Press any key...")
        readLine()
        adminProductScreen()
        return
    }
    println()
    println("[*] Add new product (x to return)")
    println()
    print("Name: ")
    val name = ScreenUtils.parseTextInput { adminAddProductScreen() }
    if (name == "x") {
        adminProductScreen()
        return
    }
    println()
    print("Category (${adminController.getCategories().joinToString()}): ")
    val category = ScreenUtils.parseTextInput { adminAddProductScreen() }
    if (!adminController.getCategories().contains(category)) {
        ScreenUtils.showIncorrectOption { adminAddProductScreen() }
        return
    }
    println()
    print("Price (number): ")
    val price = ScreenUtils.parseNumberInput { adminAddProductScreen() }
    val product = Product(name, category, priceInRupees = price.toLong())

    if (adminController.getProducts().contains(product)) {
        println()
        println("Error: Product already exist, press any key...")
        readLine()
        adminAddProductScreen()
        return
    }
    adminController.addProduct(product)
    println()
    println("Product added, press any key...")
    readLine()
    adminProductScreen()
}

fun adminViewCommonScreen(type: String, formatted: List<String>, add: () -> Unit, remove: (index: Int) -> Unit) {
    clearScreen()

    println()
    println("Here are all the $type")
    println("---------------------------")
    println()
    if (formatted.isEmpty()) println("Oops.. There are no items")
    else formatted.forEachIndexed { index, line -> println("${index + 1}. $line") }
    println()
    println("a. Add $type")
    println("0. Return")
    println()
    println("Tip: To Remove a $type, (type no & press Enter)")
    println()
    print("Choose: ")
    val input = ScreenUtils.parseTextInput { adminViewCommonScreen(type, formatted, add, remove) }
    val number = input.toIntOrNull()
    if (number != null) {
        if (number == 0) {
            adminScreen()
            return
        }
        // delete
        if (number !in 1..formatted.size) {
            println()
            println("Incorrect number to remove item, press any key...")
            readLine()
            adminViewCommonScreen(type, formatted, add, remove)
            return
        }
        remove(number - 1)
        adminCategoryScreen()
    }

    if (input == "a") {
        if (type == "Category") adminAddCategoryScreen()
        else if (type == "Product") adminAddProductScreen()
        return
    } else {
        ScreenUtils.showIncorrectOption { adminViewCommonScreen(type, formatted, add, remove) }
    }
}