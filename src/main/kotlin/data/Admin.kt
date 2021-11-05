package data

import data.types.Category
import data.types.Customer
import data.models.Product

interface Admin : Client {
    fun getCategories(): List<Category>
    fun getProducts(): List<Product>

    fun addCategory(name: Category) : Boolean
    fun removeCategory(name: Category) : Boolean

    fun addProduct(product: Product) : Boolean
    fun removeProduct(product: Product) : Boolean

    fun getAllUsers(): List<Customer>
    fun getUser(name: String): Customer
    fun addUser(name: String) : Boolean
}