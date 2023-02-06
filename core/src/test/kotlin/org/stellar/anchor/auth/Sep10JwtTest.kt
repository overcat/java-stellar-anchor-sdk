package org.stellar.anchor.auth

import java.time.Instant
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.stellar.anchor.TestConstants.Companion.TEST_ACCOUNT
import org.stellar.anchor.TestConstants.Companion.TEST_CLIENT_DOMAIN

class Sep10JwtTest {
  private val issuedAt = Instant.now().epochSecond
  private val expiresAt = issuedAt + 500

  @Test
  fun `test token creation`() {
    val token = Sep10Jwt.of("iss", TEST_ACCOUNT, issuedAt, expiresAt, "", TEST_CLIENT_DOMAIN)
    assertEquals(TEST_ACCOUNT, token.account)
    assertEquals(TEST_CLIENT_DOMAIN, token.getClientDomain())
    assertEquals(issuedAt, token.getIat())
    assertEquals(expiresAt, token.getExp())
    assertEquals(TEST_CLIENT_DOMAIN, token.getClientDomain())
  }

  @Test
  fun `test the mapping of JWT fields`() {
    val accountMemo = "135689"
    val token =
      Sep10Jwt.of("iss", "$TEST_ACCOUNT:$accountMemo", issuedAt, expiresAt, "", TEST_CLIENT_DOMAIN)
    assertEquals(TEST_ACCOUNT, token.account)
    assertEquals(accountMemo, token.accountMemo)
    assertEquals(TEST_CLIENT_DOMAIN, token.getClientDomain())
    assertEquals(issuedAt, token.getIat())
    assertEquals(expiresAt, token.getExp())
    assertEquals(TEST_CLIENT_DOMAIN, token.getClientDomain())
  }

  @Test
  fun `test the mux account mapping`() {
    val muxedAccount = "MA3X53JGZ5SLT733GNKH3CVV7RKCL4DXWCIZG2Y24HA24L6XNEHSQAAAAAAETFQC2JGGC"
    val token = Sep10Jwt.of("iss", muxedAccount, issuedAt, expiresAt, "", TEST_CLIENT_DOMAIN)
    assertEquals("GA3X53JGZ5SLT733GNKH3CVV7RKCL4DXWCIZG2Y24HA24L6XNEHSQXT4", token.account)
    assertEquals(muxedAccount, token.muxedAccount)
    assertEquals(1234567890, token.muxedAccountId)
    assertNull(token.accountMemo)
    assertEquals(TEST_CLIENT_DOMAIN, token.getClientDomain())
    assertEquals(issuedAt, token.getIat())
    assertEquals(expiresAt, token.getExp())
    assertEquals(TEST_CLIENT_DOMAIN, token.getClientDomain())
  }
}