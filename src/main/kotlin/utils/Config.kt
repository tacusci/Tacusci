package utils

import extensions.doesNotExist
import mu.KLoggable
import utils.Config.props.propertiesFileUpdate
import java.io.File
import java.io.IOException
import java.security.InvalidParameterException
import java.text.MessageFormat
import java.util.*

/**
 * Created by alewis on 29/11/2016.
 */

class Config private constructor() {

    companion object props : Properties() {

        fun load() {

            val defaults: HashMap<String, String> = hashMapOf(Pair("server_address", "localhost"),
                    Pair("port", "1025"),
                    Pair("db_url", "jdbc:mysql://localhost"),
                    Pair("sql_setup_script_location", "sql_setup_script.sql"),
                    Pair("schema_name", "tvf"),
                    Pair("default_admin_user", "tvf_admin"),
                    Pair("default_admin_password", "Password1234!"),
                    Pair("default_admin_email", "admin_tvf@tvf.net"))
            //TODO: this could probably be cleaned up more
            this.setProperty("properties_file", "tvf.properties")
            val propertiesFile = File(this.getProperty("properties_file"))
            if (propertiesFile.doesNotExist()) {
                defaults.forEach { property, value -> this.setProperty(property, value) }
                this.store(propertiesFile.outputStream(), "")
            } else {
                try {
                    //logger.info("Loading properties from tvf.properties")
                    this.load(propertiesFile.inputStream())
                    defaults.forEach { property, value ->
                        if (getProperty(property).isEmpty() || getProperty(property).isBlank()) {
                            this.setProperty(property, value)
                        }
                    }
                    this.store(propertiesFile.outputStream(), "")
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }

        override fun getProperty(key: String): String {
            val property = super.getProperty(key) ?: ""
            return property
        }

        override fun getProperty(key: String, defaultValue: String): String {
            return super.getProperty(key, defaultValue)
        }

        fun monitorPropertiesFile() {
            val fileWatcher = FileWatcher(File(this.getProperty("properties_file")))
            fileWatcher.action = {propertiesFileUpdate()}
            fileWatcher.start()
        }

        fun propertiesFileUpdate() {
            println("TODO: Add stuff")
        }
    }
}
