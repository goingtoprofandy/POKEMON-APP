package com.developer.aitek.pokemon

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.developer.aitek.api.ApiException
import com.developer.aitek.api.ConnectionException
import com.developer.aitek.api.Repository
import com.developer.aitek.api.data.*

class ViewModelMain(
    private val repository: Repository
): ViewModel() {

    val dataRes = MutableLiveData<CustomResponsePagination<MutableList<ItemPokemon>>>()
    val dataMyRes = MutableLiveData<CustomResponsePagination<MutableList<ItemMyPokemon>>>()
    val dataDetailRes = MutableLiveData<CustomResponseDetail<DataDetailPokemon,
            MetaDetailPokemon>>()
    val isLoading = MutableLiveData<Boolean>().apply { value = false }

    fun loadData(page:Int = 0, onError: (String) -> Unit) {
        isLoading.postValue(true)
        Coroutines.main {
            try {
                val response = repository.lists(page)
                dataRes.postValue(response)
            } catch (e: ApiException) {
                onError(e.message.toString())
            } catch (e: ConnectionException) {
                onError(e.message.toString())
            }
            isLoading.postValue(false)
        }
    }

    fun detailData(id:String, deviceID: String, onError: (String) -> Unit, onSuccess: (String) -> Unit) {
        isLoading.postValue(true)
        Coroutines.main {
            try {
                val response = repository.detail(id, deviceID)
                dataDetailRes.postValue(response)
                onSuccess(response.message)
            } catch (e: ApiException) {
                onError(e.message.toString())
            } catch (e: ConnectionException) {
                onError(e.message.toString())
            }
            isLoading.postValue(false)
        }
    }

    fun catchData(id:String, deviceID: String, onError: (String) -> Unit, onSuccess: (String) -> Unit) {
        isLoading.postValue(true)
        Coroutines.main {
            try {
                val response = repository.catchIt(id, deviceID)
                if (response.meta != null) onSuccess(response.message)
                else onError(response.message)
            } catch (e: ApiException) {
                onError(e.message.toString())
            } catch (e: ConnectionException) {
                onError(e.message.toString())
            }
            isLoading.postValue(false)
        }
    }

    fun saveData(id:String, deviceID: String, name: String,
                 onError: (String) -> Unit, onSuccess: (String) -> Unit) {
        isLoading.postValue(true)
        Coroutines.main {
            try {
                val response = repository.save(id, deviceID, name)
                if (response.meta != null) onSuccess(response.message)
                else onError(response.message)
            } catch (e: ApiException) {
                onError(e.message.toString())
            } catch (e: ConnectionException) {
                onError(e.message.toString())
            }
            isLoading.postValue(false)
        }
    }

    fun renameData(id:String, deviceID: String,
                 onError: (String) -> Unit, onSuccess: (String) -> Unit) {
        isLoading.postValue(true)
        Coroutines.main {
            try {
                val response = repository.rename(id, deviceID)
                onSuccess(response.message)
            } catch (e: ApiException) {
                onError(e.message.toString())
            } catch (e: ConnectionException) {
                onError(e.message.toString())
            }
            isLoading.postValue(false)
        }
    }

    fun releaseData(id:String, deviceID: String,
                   onError: (String) -> Unit, onSuccess: (String) -> Unit) {
        isLoading.postValue(true)
        Coroutines.main {
            try {
                val response = repository.release(id, deviceID)
                if (response.meta != null) onSuccess(response.message)
                else onError(response.message)
            } catch (e: ApiException) {
                onError(e.message.toString())
            } catch (e: ConnectionException) {
                onError(e.message.toString())
            }
            isLoading.postValue(false)
        }
    }

    fun myLists(page:Int = 0, deviceID: String,
                    onError: (String) -> Unit) {
        isLoading.postValue(true)
        Coroutines.main {
            try {
                val response = repository.my(page, deviceID)
                dataMyRes.postValue(response)
            } catch (e: ApiException) {
                onError(e.message.toString())
            } catch (e: ConnectionException) {
                onError(e.message.toString())
            }
            isLoading.postValue(false)
        }
    }

}