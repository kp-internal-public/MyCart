package data

import data.impls.InMemory
import data.impls.PersistentController
import data.types.StorageType

interface Controller : User, Admin {

    companion object {
        inline fun <reified T : Client> get(type: StorageType): T {
            return if (type == StorageType.IN_MEMORY)
                InMemory() as T
            else PersistentController.get(T::class)
        }
    }
}