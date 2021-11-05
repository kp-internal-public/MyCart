package data.models

import kotlinx.serialization.Serializable

@Serializable
data class Bill(val products: List<Product>, val discount: Int = 0) {
    override fun toString(): String =
        "Purchased products (${products.size}): ${products.joinToString { "${it.name} (${it.category})" }}\nFinal amount (rupees): ${products.sumOf { it.priceInRupees }}, Discount (rupees): $discount, Total (rupees): ${(products.sumOf { it.priceInRupees } - discount)}\n--------------------------"
}