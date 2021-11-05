import data.Admin
import data.impls.PersistentController
import data.impls.PersistentData
import data.models.Product
import kotlinx.serialization.json.Json
import org.junit.Before
import org.junit.Test
import kotlinx.serialization.*

class PersistentControllerTests {

    private lateinit var adminController: Admin
    private lateinit var persistentManager: MemoryPersistentManager

    @Before
    fun initialize() {
        // Don't mock use fakes
        persistentManager = MemoryPersistentManager()
        adminController = PersistentController.get(Admin::class, manager = persistentManager)
    }

    @Test
    fun `check if data is persisted`() {
        adminController.addCategory("tech")

        assert(persistentManager.read().isNotEmpty())
    }

    @Test
    fun `check if categories are persisted correctly`() {
        adminController.addCategory("tech")
        adminController.addCategory("fashion")

        val persistentData = PersistentData(categories = arrayListOf("tech", "fashion"))
        assert(Json.encodeToString(persistentData) == readFromStorage())
    }

    @Test
    fun `see if user data is persisted`() {
        val product = Product("product", "tech", 1200)

        adminController.addUser("KP")
        adminController.addCategory("tech")
        adminController.addProduct(product)

        val userController = adminController.getUser("KP").second

        userController.addProductToCart(product)

        val json = """{"categories":["tech"],"products":[{"name":"product","category":"tech","priceInRupees":1200}],"users":{"KP":{"cart":[{"name":"product","category":"tech","priceInRupees":1200}]}}}"""

        assert(readFromStorage() == json)
    }

    private fun readFromStorage() : String {
        return persistentManager.read()
    }
}