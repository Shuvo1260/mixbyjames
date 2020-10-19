package org.binaryitplanet.mixbyjames.Network

import org.binaryitplanet.mixbyjames.Utils.Config
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitClient {

    companion object {
        private var retrofit: Retrofit? = null

        fun getInstance(): Retrofit {
            return if (retrofit != null)
                retrofit!!
            else
                Retrofit
                    .Builder()
                    .baseUrl(Config.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
        }
    }
}