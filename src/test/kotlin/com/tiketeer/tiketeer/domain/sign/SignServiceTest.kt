package com.tiketeer.tiketeer.domain.sign

import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.MockitoAnnotations
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.util.ReflectionTestUtils
import java.util.*
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

@SpringBootTest
class SignServiceTest {

    @Autowired
    private lateinit var signService: SignService
    private lateinit var sha256Hmac: Mac

    @BeforeEach
    fun setup() {
        MockitoAnnotations.openMocks(this)
        val secretKey = "test-secret-key"
        sha256Hmac = Mac.getInstance("HmacSHA256")
        val secretKeySpec = SecretKeySpec(secretKey.toByteArray(), "HmacSHA256")
        sha256Hmac.init(secretKeySpec)
        ReflectionTestUtils.setField(signService, "sha256Hmac", sha256Hmac)
    }

    @Test
    fun `정상 signature - 검증 - 성공`() {
        // given
        val target = "test-message"
        val signatureBytes = sha256Hmac.doFinal(target.toByteArray())
        val base64Signature = Base64.getUrlEncoder().withoutPadding().encodeToString(signatureBytes)

        // when
        val isValid = signService.verify(target, base64Signature)

        // then
        assertTrue(isValid)
    }


    @Test
    fun `틀린 signature - 검증 - 실패`() {
        // given
        val target = "test-message"
        val invalidSignature = "invalid-signature"

        // when - then
        assertThrows<IllegalArgumentException> {
            signService.verify(target, invalidSignature)
        }
    }
}