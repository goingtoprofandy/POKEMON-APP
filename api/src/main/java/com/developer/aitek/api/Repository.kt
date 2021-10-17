package com.developer.aitek.api

import com.developer.aitek.api.data.*

class Repository(
    private val remoteRequestManager: RemoteRequestManager
): SafeApiRequest() {

    suspend fun lists(page: Int = 0): CustomResponsePagination<MutableList<ItemPokemon>> {
        return apiRequest { remoteRequestManager.getLists(page) }
    }

    suspend fun detail(id: String, deviceID: String): CustomResponseDetail<DataDetailPokemon,
            MetaDetailPokemon> {
        return apiRequest { remoteRequestManager.getDetail(id, deviceID) }
    }

    suspend fun catchIt(id: String, deviceID: String): CustomResponseMeta<MetaDetailPokemon?> {
        return apiRequest { remoteRequestManager.catchIt(id, deviceID) }
    }

    suspend fun save(id: String, deviceID: String, name: String): CustomResponseMeta<MetaDetailPokemon?> {
        return apiRequest { remoteRequestManager.catchItAdd(id, deviceID, name) }
    }

    suspend fun rename(id: String, deviceID: String): CustomResponseMeta<MetaDetailPokemon?> {
        return apiRequest { remoteRequestManager.catchItRename(id, deviceID) }
    }

    suspend fun release(id: String, deviceID: String): CustomResponseMeta<MetaDetailPokemon?> {
        return apiRequest { remoteRequestManager.release(id, deviceID) }
    }

    suspend fun my(page: Int = 0, deviceID: String): CustomResponsePagination<MutableList<ItemMyPokemon>> {
        return apiRequest { remoteRequestManager.my(page, deviceID) }
    }

}