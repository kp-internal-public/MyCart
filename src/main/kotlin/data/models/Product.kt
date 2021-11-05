package data.models

import data.types.Category
import kotlinx.serialization.Serializable

@Serializable
data class Product(val name: String, val category: Category, val priceInRupees: Long) {
    override fun toString(): String = "$name ($category) - $priceInRupees rupees"
}