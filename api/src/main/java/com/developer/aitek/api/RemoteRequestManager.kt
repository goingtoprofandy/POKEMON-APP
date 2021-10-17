package com.developer.aitek.api

import android.annotation.SuppressLint
import android.content.Context
import com.developer.aitek.api.data.*
import com.readystatesoftware.chuck.ChuckInterceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

interface RemoteRequestManager {

    @GET("lists/index")
    suspend fun getLists(
        @Query("page") page: Int
    ): Response<CustomResponsePagination<MutableList<ItemPokemon>>>

    @GET("detail/index")
    suspend fun getDetail(
        @Query("id") id: String,
        @Query("device_id") device_id: String
    ): Response<CustomResponseDetail<DataDetailPokemon, MetaDetailPokemon>>

    @GET("catchIt/index")
    suspend fun catchIt(
        @Query("id") id: String,
        @Query("device_id") device_id: String
    ): Response<CustomResponseMeta<MetaDetailPokemon?>>

    @GET("catchIt/save")
    suspend fun catchItAdd(
        @Query("id") id: String,
        @Query("device_id") device_id: String,
        @Query("name") name: String,
    ): Response<CustomResponseMeta<MetaDetailPokemon?>>

    @GET("catchIt/rename")
    suspend fun catchItRename(
        @Query("id") id: String,
        @Query("device_id") device_id: String
    ): Response<CustomResponseMeta<MetaDetailPokemon?>>

    @GET("release/index")
    suspend fun release(
        @Query("id") id: String,
        @Query("device_id") device_id: String,
    ): Response<CustomResponseMeta<MetaDetailPokemon?>>

    @GET("my/index")
    suspend fun my(
        @Query("page") page: Int,
        @Query("device_id") device_id: String,
    ): Response<CustomResponsePagination<MutableList<ItemMyPokemon>>>

    companion object{
        @SuppressLint("HardwareIds")
        operator fun invoke(
            networkConnectionInterceptor: NetworkConnectionInterceptor,
            context: Context
        ) : RemoteRequestManager {

            val interceptor = HttpLoggingInterceptor()
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY)

            val okHttpClient = OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .addInterceptor(networkConnectionInterceptor)
                .addInterceptor(ChuckInterceptor(context))
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build()

            return Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl("http://103.146.203.236/pokemon_api/index.php/v1/")
                .client(okHttpClient)
                .build()
                .create(RemoteRequestManager::class.java)
        }
    }
}