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

import app.Application
import extensions.doesNotExist
import org.apache.log4j.*
import java.io.File
import java.io.IOException
import java.util.*

/**
 * Created by alewis on 29/11/2016.
 */

open class Config {

    companion object props : Properties() {

        var fileWatcher = FileWatcher(File(""))
        var propertiesFile = File("")

        private fun getDefaultPropertiesHashMap(): HashMap<String, String> {
            return hashMapOf(Pair("server_address", "localhost"),
                    Pair("port", "1025"),
                    Pair("db_url", "jdbc:mysql://localhost"),
                    Pair("schema_name", "tacusci"),
                    Pair("default_admin_user", "tacusci_admin"),
                    Pair("default_admin_password", "Password1234!"),
                    Pair("default_admin_email", ""),
                    Pair("log_file", "tacusci.log"),
                    Pair("using_ssl_on_proxy", "false"),
                    Pair("robots_file", ""),
                    Pair("reset_password_authhash_timeout_seconds", ""),
                    Pair("smtp_server_host", ""),
                    Pair("smtp_server_port", ""),
                    Pair("smtp_account_username", ""),
                    Pair("smtp_account_password", ""),
                    Pair("smtp_use_ttls", "false"),
                    Pair("reset_password_from_address", ""),
                    Pair("reset_password_email_subject", ""),
                    Pair("page_title", "Tacusci Website"),
                    Pair("page_title_divider", "|"),
                    Pair("reset_password_email_content_file", ""))
        }

        fun load() {
            val defaults: HashMap<String, String> = getDefaultPropertiesHashMap()
            //TODO: this could probably be cleaned up more
            this.setProperty("properties_file", "tacusci.properties")
            propertiesFile = File(this.getProperty("properties_file"))
            if (propertiesFile.doesNotExist()) {
                defaults.forEach { property, value -> this.setProperty(property, value) }
                storeAll()
            } else {
                try {
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
            propertiesFile.inputStream().close()
            setupLoggers(Config.getProperty("log_file"))
        }

        override fun getProperty(key: String): String {
            val property = super.getProperty(key) ?: ""
            return property
        }

        override fun getProperty(key: String, defaultValue: String): String {
            return super.getProperty(key, defaultValue)
        }

        fun storeAll() { store(propertiesFile.outputStream(), "") }

        fun getDefaultProperty(key: String): String {
            return getDefaultProperties().getProperty(key)
        }

        fun getDefaultProperty(key: String, defaultValue: String): String {
            return getDefaultProperties().getProperty(key, defaultValue)
        }

        private fun getDefaultProperties(): Properties {
            val defaultProperties = Properties()
            val defaultPropertyKeysAndValues = getDefaultPropertiesHashMap()
            defaultPropertyKeysAndValues.forEach { property, value -> defaultProperties.setProperty(property, value) }
            return defaultProperties
        }

        fun loadFromPropertiesFile(propertiesFile: File) {
            val defaults: HashMap<String, String> = getDefaultPropertiesHashMap()
            File(this.getProperty("properties_file"))
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

        fun monitorPropertiesFile(application: Application) {
            fileWatcher = FileWatcher(File(this.getProperty("properties_file")))
            fileWatcher.action = {propertiesFileUpdate(application)}
            fileWatcher.start()
        }

        fun stopMonitoringPropertiesFile() {
            fileWatcher.stop()
        }

        fun propertiesFileUpdate(application: Application) {
            if (File(this.getProperty("properties_file")).exists()) {
                this.load(File(this.getProperty("properties_file")).inputStream())
                application.restartServer()
            }
        }

        fun setupLoggers(logFilePath: String) {

            val pattern = "%d{yyyy-MM-dd HH:mm:ss} %-5p %c{1}:%L - %m%n"
            val threshold = if (CliOptions.getFlag("debug")) Level.DEBUG else Level.INFO

            val consoleAppender = ConsoleAppender()
            consoleAppender.name = "ConsoleAppender"
            consoleAppender.target = "System.err"
            consoleAppender.layout = PatternLayout(pattern)
            consoleAppender.threshold = threshold
            consoleAppender.activateOptions()
            Logger.getRootLogger().addAppender(consoleAppender)

            val fileAppender = FileAppender()
            fileAppender.name = "RollingFileAppender"
            fileAppender.file = logFilePath
            fileAppender.layout = PatternLayout(pattern)
            fileAppender.threshold = threshold
            fileAppender.append = true
            fileAppender.activateOptions()
            Logger.getRootLogger().addAppender(fileAppender)
        }
    }
}
