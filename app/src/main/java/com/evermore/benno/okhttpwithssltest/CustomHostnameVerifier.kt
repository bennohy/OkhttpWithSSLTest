package com.evermore.benno.okhttpwithssltest

import javax.net.ssl.HostnameVerifier
import javax.net.ssl.SSLSession

class CustomHostnameVerifier : HostnameVerifier {
    override fun verify(hostname: String, session: SSLSession): Boolean = true
}