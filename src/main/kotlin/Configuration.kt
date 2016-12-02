import extensions.doesNotExist
import mu.KLoggable
import mu.KLogger
import mu.KLogging
import java.io.File
import java.io.IOException
import java.security.InvalidParameterException
import java.text.MessageFormat
import java.util.*

/**
 * Created by alewis on 29/11/2016.
 */

class Configuration private constructor() {

    companion object props : Properties(), KLoggable {

        override val logger = logger()

        fun load() {
            val propertiesFile = File("tvf.properties")
            if (propertiesFile.doesNotExist()) {
                this.setProperty("server_address", "localhost")
                this.setProperty("port", "1025")
                this.setProperty("db_url", "jdbc:mysql://localhost")
                this.setProperty("sql_setup_script_location", "sql_setup_script.sql")
                this.setProperty("schema_name", "tvf")
                this.store(propertiesFile.outputStream(), "")
            } else {
                try {
                    logger.info("Loading properties from tvf.properties")
                    this.load(propertiesFile.inputStream())
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }

        override fun getProperty(key: String): String {
            val property = super.getProperty(key) ?: throw InvalidParameterException(MessageFormat.format("Missing value for key {0}!", key))
            return property
        }

        override fun getProperty(key: String, defaultValue: String): String {
            return super.getProperty(key, defaultValue)
        }

    }
}