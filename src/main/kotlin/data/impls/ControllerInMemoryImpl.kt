package data.impls

import data.Controller
import data.User
import data.models.Bill
import data.models.Product
import data.types.Category
import data.types.Customer
import data.types.StorageType

private object Global {
    val users = mutableMapOf<String, User>()
    val categories = arrayListOf<Category>()
    val products = arrayListOf<Product>()
}

private object Mocker {
    fun mockGlobal() {
        Global.users["KP"] = InMemory().apply { addBill(Bill(listOf(Product("Xiaomi 9 power", "tech", 12000)), 500)) }
        Global.categories.addAll(listOf("tech", "fashion"))
        Global.products.addAll(listOf(
            Product("Xiaomi 9 power", "tech", 12000),
            Product("Mechanical keyboard", "tech", 2499),
            Product("Shirt", "fashion", 550),
        ))
    }
}

class InMemory : Controller {
    private val cart = arrayListOf<Product>()
    private val bills = arrayListOf<Bill>()

    override fun getCategories(): List<String> = Global.categories
    override fun getProducts(): List<Product> = Global.products
    override fun getCart(): List<Product> = cart
    override fun getBills(): List<Bill> = bills

    override fun addCategory(name: Category) : Boolean {
        if (Global.categories.contains(name)) return false
        return Global.categories.add(name)
    }

    override fun addProduct(product: Product) : Boolean {
        if (Global.products.contains(product)) return false
        if (!Global.categories.contains(product.category)) return false
        return Global.products.add(product)
    }

    override fun removeCategory(name: Category) : Boolean {
        return Global.categories.remove(name)
    }

    override fun removeProduct(product: Product) : Boolean {
        return Global.products.remove(product)
    }

    override fun addBill(bill: Bill) : Boolean {
        if (bill.products.isEmpty()) return false
        if (!Global.products.containsAll(bill.products)) return false
        bills.add(bill)
        return true
    }

    override fun addProductToCart(product: Product) : Boolean {
        return cart.add(product)
    }

    override fun removeProductFromCart(product: Product) : Boolean {
        return cart.remove(product)
    }

    override fun getAllUsers(): List<Customer> {
        return Global.users.map { it.key to it.value }
    }

    override fun addUser(name: String) : Boolean {
        if (Global.users.containsKey(name)) return false
        Global.users[name] = Controller.get(StorageType.IN_MEMORY)
        return true
    }

    override fun getUser(name: String): Customer {
        return name to Global.users[name]!!
    }

    override fun clearCategories() {
        Global.categories.clear()
    }

    override fun clearProducts() {
        Global.products.clear()
    }
}