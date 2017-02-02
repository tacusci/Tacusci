package utils

import database.models.User
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Created by alewis on 02/02/2017.
 */
class ValidationTest {

    val configRootAdmin = User("Root Admin", Config.getProperty("default_admin_user"), Config.getProperty("default_admin_password"), Config.getProperty("default_admin_email"), 0, 1)

    @Test
    fun matchPasswordPattern() {
        TestingCore.setupConfig()
        val goodPassword = "GoodPasswordHere1234!"
        val badPassword = "testing"
        assertTrue(Validation.matchPasswordPattern(goodPassword))
        assertFalse(Validation.matchPasswordPattern(badPassword))
    }

    @Test
    fun matchEmailPattern() {
        TestingCore.setupConfig()
        val validEmail = "iamhere@place.com"
        val invalidEmail = "nononononono@efiejfieefe.gnngng"
        assertTrue(Validation.matchEmailPattern(validEmail))
        assertFalse(Validation.matchEmailPattern(invalidEmail))
    }

    @Test
    fun matchUsernamePattern() {
        TestingCore.setupConfig()
        val validUsernames = listOf("tvf_admin", "IamAUsername", "WohOhNo")
        val invalidUsernames = listOf("<>", "<script>", "%&(S@!")
        validUsernames.forEach { assertTrue(Validation.matchUsernamePattern(it)) }
        invalidUsernames.forEach { assertFalse(Validation.matchUsernamePattern(it)) }
    }

    @Test
    fun matchFullNamePattern() {
        TestingCore.setupConfig()
        val validFullName = "Full Name"
        val invalidFullName = "<script>Do naughty shizz</script>"
        assertTrue(Validation.matchFullNamePattern(validFullName))
        assertFalse(Validation.matchFullNamePattern(invalidFullName))
    }

    @Test
    fun isAdminAccountFullNameValid() {
        TestingCore.setupConfig()
        assertTrue(Validation.matchFullNamePattern(configRootAdmin.fullName))
    }

    @Test
    fun isAdminAccountUsernameValid() {TestingCore.setupConfig()
        assertTrue(Validation.matchUsernamePattern(configRootAdmin.username))
    }

    @Test
    fun isAdminAccountPasswordValid() {
        TestingCore.setupConfig()
        assertTrue(Validation.matchPasswordPattern(configRootAdmin.password))
    }

    @Test
    fun isAdminAccountEmailValid() {
        TestingCore.setupConfig()
        assertTrue(Validation.matchEmailPattern(configRootAdmin.email))
    }
}