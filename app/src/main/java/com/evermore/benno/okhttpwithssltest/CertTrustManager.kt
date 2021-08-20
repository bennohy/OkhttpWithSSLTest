package com.evermore.benno.okhttpwithssltest

import java.security.cert.X509Certificate
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

class CertTrustManager : X509TrustManager, TrustManager {
    override fun checkClientTrusted(chain: Array<out X509Certificate>, authType: String) {
    }

    override fun checkServerTrusted(chain: Array<out X509Certificate>, authType: String) {
    }

    override fun getAcceptedIssuers(): Array<X509Certificate> = arrayOf<X509Certificate>()
}