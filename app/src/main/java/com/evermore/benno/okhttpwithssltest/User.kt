package com.evermore.benno.okhttpwithssltest


data class User(
    var mail: String = "",
    var appPwd: String = "",
    var name: String = "",
    var displayName: String = "",
    var isAdmin: Boolean = false,
    var cloudDevice: String = "",
    var current: Boolean = false
)
