package com.evermore.benno.okhttpwithssltest

import java.security.SecureRandom
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLSocketFactory
import javax.net.ssl.TrustManager

class SSLTrustFactory {
    companion object {
        fun getSocketFactory(): SSLSocketFactory {
            val socket = SSLContext.getInstance("TLS")
            socket.init(null, arrayOf<TrustManager>(CertTrustManager()), SecureRandom())
            return socket.socketFactory
        }
    }
}