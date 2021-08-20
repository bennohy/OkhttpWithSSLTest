package com.evermore.benno.okhttpwithssltest

data class Device(
    var name: String = "",
    var id: String = "",
    var fcmToken: String = "",
    var sinceLastActivity: Long = 0
)