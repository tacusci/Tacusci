/*
 * # DON'T BE A DICK PUBLIC LICENSE
 *
 * > Version 1.1, December 2016
 *
 * > Copyright (C) 2016 Adam Prakash Lewis
 *
 *  Everyone is permitted to copy and distribute verbatim or modified
 *  copies of this license document.
 *
 * > DON'T BE A DICK PUBLIC LICENSE
 * > TERMS AND CONDITIONS FOR COPYING, DISTRIBUTION AND MODIFICATION
 *
 *  1. Do whatever you like with the original work, just don't be a dick.
 *
 *      Being a dick includes - but is not limited to - the following instances:
 *
 * 	 1a. Outright copyright infringement - Don't just copy this and change the name.
 * 	 1b. Selling the unmodified original with no work done what-so-ever, that's REALLY being a dick.
 * 	 1c. Modifying the original work to contain hidden harmful content. That would make you a PROPER dick.
 *
 *  2. If you become rich through modifications, related works/services, or supporting the original work,
 *  share the love. Only a dick would make loads off this work and not buy the original work's
 *  creator(s) a pint.
 *
 *  3. Code is provided with no warranty. Using somebody else's code and bitching when it goes wrong makes
 *  you a DONKEY dick. Fix the problem yourself. A non-dick would submit the fix back.
 */

package utils

import TestingCore
import database.models.User
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Created by alewis on 02/02/2017.
 */
class ValidationTest {

    val configRootAdmin = User(-1, -1, -1, "Root Admin", Config.getProperty("root-username"), Config.getProperty("root-password"), Config.getProperty("root-email"), 0, 1)

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