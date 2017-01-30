import com.nhaarman.mockito_kotlin.whenever
import handlers.UserHandler
import org.junit.Assert.assertTrue
import org.junit.Test
import org.mockito.Mockito.mock
import spark.Request
import spark.Session
import utils.Config
import utils.PasswordStorage

/**
 * Created by alewis on 30/01/2017.
 */
class UserHandlerTest {
    @Test
    fun login() {
        TestingSetup.setupSetEnv()
        val mockRequest = mock(Request::class.java)
        whenever(mockRequest.ip()).thenReturn("0.0.0.0:80")
        whenever(mockRequest.session()).thenReturn(mock(Session::class.java))
        val loginSuccessful = UserHandler.login(mockRequest, Config.getProperty("default_admin_user"), Config.getProperty("default_admin_password"))
        assertTrue(loginSuccessful)
    }
}