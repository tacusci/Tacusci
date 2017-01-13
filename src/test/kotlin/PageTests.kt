import com.natpryce.hamkrest.equalTo
import com.oneeyedmen.konsent.*
import com.oneeyedmen.konsent.webdriver.ChromeAcceptanceTest
import com.oneeyedmen.konsent.webdriver.loadsThePageAt
import com.oneeyedmen.konsent.webdriver.thePageTitle
import com.oneeyedmen.okeydoke.pickle.Scenario
import org.junit.runner.RunWith

/**
 * Created by alewis on 12/01/2017.
 */

@RunWith(Konsent::class)
@Preamble(
        "As a user named tvf_admin",
        "I want to know that the index page loads correctly"
)

class PageTests : ChromeAcceptanceTest() {

    val user = actorNamed("tvf_admin")

    @Scenario("1") fun indexPageLoadsCorrectly() {
        Given(user).loadsThePageAt("http://localhost")
        Then(user) {
            shouldSee(thePageTitle, equalTo("Thames Valley Furs - Homepage"))
        }
    }
}