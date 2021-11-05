import data.impls.Manager
import data.impls.PersistentData
import kotlinx.serialization.*
import kotlinx.serialization.json.Json

class MemoryPersistentManager : Manager {
    private var _persistent = PersistentData()
    override val persistent: PersistentData = _persistent

    private var data: String = ""

    override fun persist() {
        data = Json.encodeToString(persistent)
    }

    override fun read(): String {
        return data
    }
}