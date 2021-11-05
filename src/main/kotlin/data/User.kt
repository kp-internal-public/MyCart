package data

import data.models.Bill
import data.types.Category
import data.models.Product

interface User : Client {
    fun getCategories(): List<Category>
    fun getProducts(): List<Product>
    fun getCart(): List<Product>
    fun getBills(): List<Bill>

    fun addProductToCart(product: Product) : Boolean
    fun removeProductFromCart(product: Product) : Boolean

    fun addBill(bill: Bill) : Boolean
}