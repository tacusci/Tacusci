import handlers.UserHandler
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.on
import utils.Config
import utils.PasswordStorage
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Created by alewis on 30/01/2017.
 */


object UserHandlerTest : Spek({

    describe("Logging in as default admin") {
        val fakeRequest = FakeRequest()
        on("login") {
            val loginSuccessful = UserHandler.login(fakeRequest, Config.getProperty("default_admin_user"), Config.getProperty("default_admin_password"))
            it("Should have signed in successfully") {
                assertTrue(loginSuccessful, "Logging as default admin was successful")
            }

            it ("Should contain default admin username in session as attribute 'username'") {
                assertEquals(Config.getProperty("default_admin_user"), fakeRequest.session().attribute("username"))
            }
        }
    }
})