import TestingCore.mockRequest
import handlers.UserHandler
import org.junit.Assert.assertTrue
import org.junit.Test
import utils.Config

/**
 * Created by alewis on 30/01/2017.
 */
class UserHandlerTest {

    @Test
    fun login() {
        TestingCore.setupSetEnv()
        val loginSuccessful = UserHandler.login(mockRequest(), Config.getProperty("default_admin_user"), Config.getProperty("default_admin_password"))
        assertTrue(loginSuccessful)
    }

    @Test
    fun logout() {
        TestingCore.setupSetEnv()
        val logoutSuccessful = UserHandler.logout(mockRequest())
        assertTrue(logoutSuccessful)
    }
}