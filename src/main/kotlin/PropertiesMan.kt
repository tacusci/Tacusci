import extensions.doesNotExist
import java.io.File
import java.util.*

/**
 * Created by tauraamui on 28/11/2016.
 */
object PropertiesMan {

    val properties = Properties()

    fun load() {
        val propertiesFile = File("tvf.properties")
        if (propertiesFile.doesNotExist()) {
            properties.setProperty("server_address", "localhost")
            properties.setProperty("port", "1025")
            properties.setProperty("db_url", "jdbc:mysql://localhost")
            properties.store(propertiesFile.outputStream(), "")
        }
    }
}