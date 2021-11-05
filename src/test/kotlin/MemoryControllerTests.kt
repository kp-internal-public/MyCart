import data.Admin
import data.Controller
import data.User
import data.impls.InMemory
import data.models.Bill
import data.models.Product
import data.types.StorageType
import org.junit.Before
import org.junit.Test

class MemoryControllerTests {

    private lateinit var adminController: Admin
    private lateinit var userController: User

    @Before
    fun initializeController() {
        adminController = Controller.get(StorageType.IN_MEMORY)
        userController = Controller.get(StorageType.IN_MEMORY)

        adminController.clearCategories()
        adminController.clearProducts()
    }

    @Test
    fun `check if categories work for in-memory databases`() {
        // initially empty
        assert(adminController.getAllUsers().isEmpty())

        adminController.addCategory("tech")
        adminController.addCategory("fashion")

        assert(!adminController.addCategory("tech"))

        assert(adminController.getCategories().size == 2)

        // Let's see if data is persisted with user controller
        assert(userController.getCategories() == adminController.getCategories())
    }

    @Test
    fun `check if products work for in-memory databases`() {
        adminController.addCategory("tech")
        adminController.addProduct(Product("Xiaomi 9 power", "tech", 12000))

        // category is not available so will return false
        assert(!adminController.addProduct(Product("Xiaomi 9 power", "unknown", 12000)))

        assert(adminController.getProducts().size == 1)

        // Let's see if data is persisted with user controller
        assert(userController.getProducts() == adminController.getProducts())
    }

    @Test
    fun `duplicate users are not allowed`() {
        adminController.addUser("KP")

        // Duplicate users are not allowed
        assert(!adminController.addUser("KP"))

        assert(adminController.getAllUsers().size == 1)
    }

    @Test
    fun `user add bill`() {
        adminController.addUser("KP")

        val product = Product("Xiaomi 9 power", "tech", 12000)

        val userController = adminController.getUser("KP").second

        // Cannot add bill with no products
        assert(!userController.addBill(Bill(listOf())))

        // Category & product does not exist hence the bill will not be added.
        assert(!userController.addBill(Bill(listOf(product))))

        adminController.addCategory("tech")
        adminController.addProduct(product)

        // Now it will be added
        assert(userController.addBill(Bill(listOf(product))))
    }
}