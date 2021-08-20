package com.evermore.benno.okhttpwithssltest

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Base64
import android.util.Log
import com.evermore.benno.okhttpwithssltest.databinding.ActivityMainBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.ResponseBody.Companion.toResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    val baseUrl = "https://192.168.9.4/"
    val testApi = "https://192.168.9.4/system/info"
    val credentials = "benno:97497929"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val connectionSpec = ConnectionSpec.Builder(ConnectionSpec.COMPATIBLE_TLS)
            .tlsVersions(TlsVersion.TLS_1_2)
            .cipherSuites(
                CipherSuite.TLS_ECDHE_ECDSA_WITH_AES_128_GCM_SHA256,
                CipherSuite.TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256,
                CipherSuite.TLS_DHE_RSA_WITH_AES_128_GCM_SHA256
            )
            .build()

        val okHttpClient = OkHttpClient.Builder()
            .connectionSpecs(Arrays.asList(ConnectionSpec.MODERN_TLS, ConnectionSpec.COMPATIBLE_TLS))
            .sslSocketFactory(SSLTrustFactory.getSocketFactory(), CertTrustManager())
            .hostnameVerifier(CustomHostnameVerifier())
            .followRedirects(true)
            .cache(null)
            .addInterceptor { chain ->
                val request = chain.request().newBuilder()
                    .addHeader("Authorization", "Basic " + Base64.encodeToString(credentials.toByteArray(), Base64.NO_WRAP))
                    .build()
                chain.proceed(request)
            }
            .addInterceptor { chain ->
                val request = chain.request()
                log("response request $request")
                val response = chain.proceed(request)
                Log.d("SSLTEST", "response.code > ${response.code}")
                log("response.message > ${response.message}")
                log("response.isSuccessful > ${response.isSuccessful}")
                if (response.isSuccessful) {
                    response.body?.let {
                        val bodyString = it.string()
                        log("bodyString $bodyString")
                        return@addInterceptor if (bodyString.isEmpty()) {
                            val responseBody = JSONObject().put("message", response.message)
                            log("responseBody $responseBody")
                            response.newBuilder()
                                .body(responseBody.toString().toResponseBody("application/json".toMediaTypeOrNull()))
                                .build()
                        } else {
                            response.newBuilder()
                                .body(bodyString.toResponseBody("application/json".toMediaTypeOrNull()))
                                .build()
                        }
                    } ?: let {
                        val responseBody = JSONObject().put("message", response.message)
                        log("responseBody $responseBody")
                        return@addInterceptor response.newBuilder()
                            .body(responseBody.toString().toResponseBody("application/json".toMediaTypeOrNull()))
                            .build()
                    }
                } else {
                    response
                }
            }
            .build()

        val retrofitClient = Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()

        val apiService = retrofitClient.create(RetrofitApiService::class.java)

        binding.send.setOnClickListener {
            GlobalScope.launch(IO) {
                val request = Request.Builder().url(testApi).build()
                val response = okHttpClient.newCall(request).execute()
                Log.d("SSLTEST", "response > ${response.body?.string()}")
                val retrofitResponse = apiService.getUserList().execute()
                retrofitResponse.body()?.let {
                    log("retrofit response > $it")
                }
                val responseDevice = apiService.getDeviceList().execute()
                responseDevice.body()?.let {
                    log("retrofit responseDevice > ${it.devices}")
                }

                apiService.getDevice("android:9218e5fc9c22515d").enqueue(object : Callback<List<Device>> {
                    override fun onResponse(call: Call<List<Device>>, response: Response<List<Device>>) {
                        log("retrofit getDevice onResponse")
                        response.body()?.let {
                            log("$it")
                        }
                    }

                    override fun onFailure(call: Call<List<Device>>, t: Throwable) {
                        log("retrofit getDevice onFailure>$t")
                    }
                })
            }
        }
    }

    private fun log(text: String) {
        Log.d("SSLTEST", text)
    }
}