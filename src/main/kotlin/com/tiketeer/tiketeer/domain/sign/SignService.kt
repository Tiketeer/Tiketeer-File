package com.tiketeer.tiketeer.domain.sign

import jakarta.annotation.PostConstruct
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.security.MessageDigest
import java.util.*
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

@Service
class SignService {

    @Value("\${jwt.secret-key}")
    private lateinit var secretKey: String

    private lateinit var sha256Hmac: Mac

    @PostConstruct
    @Throws(Exception::class)
    fun init() {
        sha256Hmac = Mac.getInstance("HmacSHA256")
        val secretKeySpec = SecretKeySpec(secretKey.toByteArray(), "HmacSHA256")
        sha256Hmac.init(secretKeySpec)
    }

    @Throws(Exception::class)
    fun verify(target: String, signature: String): Boolean {
        val newSignature = sha256Hmac.doFinal(target.toByteArray())
        val providedSignature = Base64.getUrlDecoder().decode(signature)

        return MessageDigest.isEqual(newSignature, providedSignature)
    }
}