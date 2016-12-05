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

            val settingAndValue = HashMap<String, String>()
            settingAndValue.put("server_address", "localhost")
            settingAndValue.put("port", "1025")
            settingAndValue.put("db_url", "jdbc:mysql://localhost")
            settingAndValue.put("sql_setup_script_location", "sql_setup_script.sql")
            settingAndValue.put("schema_name", "tvf")

            val propertiesFile = File("tvf.properties")

            if (propertiesFile.doesNotExist()) {
                logger.info("Creating tvf.properties file with default settings")
                this.putAll(settingAndValue)
                this.store(propertiesFile.outputStream(), "")
                this.load(propertiesFile.inputStream())
            } else {
                try {
                    logger.info("Loading properties from tvf.properties")
                    this.load(propertiesFile.inputStream())
                    settingAndValue.forEach { setting, value ->
                        if (!this.containsKey(setting)) {
                            this.put(setting, value)
                        }
                    }
                    this.store(propertiesFile.outputStream(), "")
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