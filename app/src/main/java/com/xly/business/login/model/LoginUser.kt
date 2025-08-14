package com.xly.business.login.model


data class LoginUser(
    var phoneNumber: String = "",
    var verificationCode: String = ""
)