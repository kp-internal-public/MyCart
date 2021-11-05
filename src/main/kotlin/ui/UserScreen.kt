package ui

import data.Admin
import data.Controller
import data.models.Bill
import data.models.Product
import data.types.Category
import data.types.Customer
import utils.*
import startScreen
import kotlin.system.exitProcess

fun createUserScreen() {
    clearScreen()
    val adminController = Controller.get<Admin>(AppSettings.storageType)
    // create a user
    println()
    print("Enter a user name: ")
    val userName = ScreenUtils.parseTextInput { createUserScreen() }

    val customer = if (adminController.getAllUsers().any { it.first == userName }) {
        adminController.getAllUsers().find { it.first == userName }!!
    } else {
        adminController.addUser(userName)
        adminController.getUser(userName)
    }

    userScreen(customer)
}

fun userScreen(customer: Customer) {
    clearScreen()
    println()
    println("[*] Browse from the following options")
    println("-------------------------------------")
    println()
    println("1. View categories/products")
    println("2. View cart (${customer.second.getCart().size})")
    println("3. Recent purchases (${customer.second.getBills().size})")
    println()
    println("0. Logout")
    println()
    print("Choose: ")

    when(ScreenUtils.parseNumberInput { userScreen(customer) }) {
        0 -> startScreen()
        1 -> userSelectCategoryScreen(customer)
        2 -> userCartScreen(customer)
        3 -> userLastOrderScreen(customer)
        else -> ScreenUtils.showIncorrectOption { userScreen(customer) }
    }
    exitProcess(1)
}

fun userSelectCategoryScreen(customer: Customer) {
    clearScreen()
    val userController = customer.second
    println()

    if (userController.getCategories().isEmpty()) {
        println("Categories are empty, press any key...")
        readLine()
        userScreen(customer)
        return
    }

    println("[*] Select a category")
    println()
    userController.getCategories().forEachIndexed { index, category -> println("${index + 1}. $category") }
    println()
    println("0. Return")
    println()
    print("Choose: ")
    val input = ScreenUtils.parseNumberInput { userSelectCategoryScreen(customer) }
    if (input == 0) {
        userScreen(customer)
        return
    }
    if (input !in 1..userController.getCategories().size) {
        ScreenUtils.showIncorrectOption { userSelectCategoryScreen(customer) }
    }
    userSelectProductScreen(userController.getCategories()[input - 1], customer)
}

fun userSelectProductScreen(category: Category, customer: Customer) {
    clearScreen()

    val userController = customer.second

    println()

    val products = userController.getProducts().filter { it.category == category }
    if (products.isEmpty()) {
        println("Products are empty, press any key...")
        readLine()
        userScreen(customer)
        return
    }

    println("[*] Browse products for category \"$category\" to purchase")
    println()
    products.forEachIndexed { index, product -> println("${index + 1}. $product") }
    println()
    println("a. View cart (${userController.getCart().size})")
    println("0. Return")
    println()
    print("Add to cart (enter no.): ")
    val input = ScreenUtils.parseTextInput { userSelectProductScreen(category, customer) }
    if (input == "a") {
        userCartScreen(customer)
        return
    }
    val number = input.toIntOrNull() ?: run {
        ScreenUtils.showIncorrectOption { userSelectCategoryScreen(customer) }
        return
    }
    if (number == 0) {
        userSelectCategoryScreen(customer)
        return
    }
    if (number !in 1..userController.getCategories().size) {
        ScreenUtils.showIncorrectOption { userSelectCategoryScreen(customer) }
    }
    val product = products[number - 1]
    userController.addProductToCart(product)
    println()
    println("Product \"${product.name}\" added to cart, press any key...")
    readLine()
    userSelectProductScreen(category, customer)
}

fun userCartScreen(customer: Customer) {
    clearScreen()

    val userController = customer.second
    println()
    println("[*] ${customer.first}'s Cart")
    println()
    if (userController.getCart().isEmpty()) {
        println("No items added to cart, press any key...")
        readLine()
        userScreen(customer)
        return
    }
    userController.getCart().forEachIndexed { index, product -> println("${index + 1}. $product") }
    println()
    println("0. Return")
    println()
    println("Tip: Enter as many numbers comma separated for selecting multiple items (eg: 1,2,3)")
    println()
    print("Choose (multiple): ")
    val input = ScreenUtils.parseTextInput { userCartScreen(customer) }
    if (input == "0") {
        userScreen(customer)
        return
    }
    val selectedItems = input.split(",").mapNotNull { it.toIntOrNull() }
        .filter { it in 1..userController.getCart().size }
        .map { userController.getCart()[it - 1] }
    if (selectedItems.isEmpty()) {
        ScreenUtils.showIncorrectOption { userCartScreen(customer) }
        return
    }
    println()
    println("[*] What do you want to do with ${selectedItems.size} items?")
    println()
    println("1. Purchase")
    println("2. Remove")
    println()
    println("0. Return")
    println()
    print("Choose: ")
    when(ScreenUtils.parseNumberInput { userCartScreen(customer) }) {
        0 -> userCartScreen(customer)
        1 -> userBuyScreen(selectedItems, customer)
        2 -> {
            selectedItems.forEach { userController.removeProductFromCart(it) }
            println()
            println("Selected items are removed, press any key...")
            readLine()
            userCartScreen(customer)
        }
        else -> ScreenUtils.showIncorrectOption { userCartScreen(customer) }
    }
}

fun userBuyScreen(items: List<Product>, customer: Customer) {
    clearScreen()

    val userController = customer.second
    val totalPrice = items.sumOf { it.priceInRupees }
    val shouldGiveDiscount = totalPrice > 10000
    val discount = if (shouldGiveDiscount) 500 else 0
    println()
    println("[*] Billing")
    println()
    items.forEachIndexed { index, product -> println("${index + 1}. $product") }
    println()
    println("------------------------------------")
    println("Discount (rupees): $discount")
    println("Total (rupees): ${totalPrice - discount}")
    println("------------------------------------")
    println()
    println("1. Proceed to buy")
    println("0. Return")
    println()
    print("Choose: ")
    when(ScreenUtils.parseNumberInput { userBuyScreen(items, customer) }) {
        0 -> userScreen(customer)
        1 -> {
            val bill = Bill(items, discount)
            userController.addBill(bill)
            items.forEach { userController.removeProductFromCart(it) }
            println()
            println("Purchase complete, Thank you! Press any key...")
            readLine()
            userScreen(customer)
        }
        else -> ScreenUtils.showIncorrectOption { userBuyScreen(items, customer) }
    }
}

fun userLastOrderScreen(customer: Customer) {
    clearScreen()

    val userController = customer.second
    println()
    println("[*] Your recent orders")
    println()
    if (userController.getBills().isEmpty()) {
        println("No recent purchases found, press any key...")
    } else {
        userController.getBills().forEachIndexed { index, bill -> println("${(index + 1)}. $bill\n\n") }
        println()
    }
    println("Press any key to return...")
    readLine()
    userScreen(customer)
}