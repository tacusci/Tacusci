import handlers.UserHandler
import org.junit.Assert.assertTrue
import org.junit.Test
import utils.Config
import TestingCore.mockRequest

/**
 * Created by alewis on 30/01/2017.
 */
class UserHandlerTest {

    init {
        TestingCore.setupSetEnv()
    }

    @Test
    fun login() {
        val loginSuccessful = UserHandler.login(mockRequest(), Config.getProperty("default_admin_user"), Config.getProperty("default_admin_password"))
        assertTrue(loginSuccessful)
    }

    @Test
    fun logout() {
        val logoutSuccessful = UserHandler.logout(mockRequest())
        assertTrue(logoutSuccessful)
    }
}