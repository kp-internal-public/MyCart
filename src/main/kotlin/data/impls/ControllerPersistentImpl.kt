package data.impls

import data.Admin
import data.Client
import data.User
import data.models.Bill
import data.models.Product
import data.types.Category
import data.types.Customer
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.io.File
import kotlin.reflect.KClass
import kotlinx.serialization.*

class PersistentController {
    companion object {
        fun<T: Client> get(type: KClass<T>, manager: Manager = builtInPersistentManager) : T {
            return (if (type == Admin::class) PersistentAdminController(manager) else PersistentUserController(UserData(), manager)) as T
        }
    }
}

@Serializable
data class UserData(
    val cart: MutableList<Product> = arrayListOf(),
    val bills: MutableList<Bill> = arrayListOf()
)

@Serializable
data class PersistentData(
    val categories: MutableList<Category> = arrayListOf(),
    val products: MutableList<Product> = arrayListOf(),
    val bills: MutableList<Bill> = arrayListOf(),
    val users: MutableMap<String, UserData> = mutableMapOf()
)

private val builtInPersistentManager = PersistentManager()

interface Manager {
    val persistent: PersistentData
    fun persist()
    fun read() : String
}

private class PersistentManager : Manager {
    private var _persistent = PersistentData()
    override val persistent: PersistentData = _persistent

    init {
        loadFromDisk()
    }

    override fun persist() {
        File(FILE_NAME).writeText(Json.encodeToString(persistent))
    }

    private fun loadFromDisk() {
        if (File(FILE_NAME).exists()) {
            val json = File(FILE_NAME).readText()
            _persistent = Json.decodeFromString(json)
        }
    }

    override fun read(): String {
        return File(FILE_NAME).readText()
    }

    private companion object {
        private const val FILE_NAME = "data.json"
    }

}

private class PersistentAdminController(private val persistentManager: Manager) : Admin {
    override fun getCategories(): List<Category> = persistentManager.persistent.categories
    override fun getProducts(): List<Product> = persistentManager.persistent.products

    override fun addCategory(name: Category) : Boolean {
        if (persistentManager.persistent.categories.contains(name)) return false
        persistentManager.persistent.categories.add(name)
        persistentManager.persist()
        return true
    }

    override fun removeCategory(name: Category) : Boolean {
        val result = persistentManager.persistent.categories.remove(name)
        persistentManager.persist()
        return result
    }

    override fun addProduct(product: Product) : Boolean {
        if (persistentManager.persistent.products.contains(product)) return false
        if (!persistentManager.persistent.categories.contains(product.category)) return false
        persistentManager.persistent.products.add(product)
        persistentManager.persist()
        return true
    }

    override fun removeProduct(product: Product) : Boolean {
        val result = persistentManager.persistent.products.remove(product)
        persistentManager.persist()
        return result
    }

    override fun getAllUsers(): List<Customer> = persistentManager.persistent.users.map { it.key to PersistentUserController(it.value, persistentManager) }
    override fun getUser(name: String): Customer {
        val userData = persistentManager.persistent.users[name]!!
        return name to PersistentUserController(userData, persistentManager)
    }

    override fun addUser(name: String) : Boolean = with(persistentManager) {
        if (persistent.users.containsKey(name)) return false
        persistent.users[name] = UserData()
        persist()
        return true
    }
}

private class PersistentUserController(private val userData: UserData, private val persistentManager: Manager) : User {
    override fun getCategories(): List<Category> = persistentManager.persistent.categories
    override fun getProducts(): List<Product> = persistentManager.persistent.products

    override fun getCart(): List<Product> = userData.cart

    override fun getBills(): List<Bill> = userData.bills

    override fun addProductToCart(product: Product) : Boolean {
        val result = userData.cart.add(product)
        persistentManager.persist()
        return result
    }

    override fun removeProductFromCart(product: Product) : Boolean {
        val result = userData.cart.remove(product)
        persistentManager.persist()
        return result
    }

    override fun addBill(bill: Bill) : Boolean {
        if (bill.products.isEmpty()) return false
        if (!getProducts().containsAll(bill.products)) return false
        userData.bills.add(bill)
        persistentManager.persist()
        return true
    }
}