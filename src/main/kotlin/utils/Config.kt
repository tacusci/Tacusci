/*
 * # DON'T BE A DICK PUBLIC LICENSE
 *
 * > Version 1.1, December 2016
 *
 * > Copyright (C) 2016-2017 Adam Prakash Lewis
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
import java.nio.charset.Charset
import java.util.*
import java.util.regex.Pattern

/**
 * Created by alewis on 29/11/2016.
 */

open class Config {

    companion object props : LinkedProperties() {

        var fileWatcher = FileWatcher(File(""))
        var propertiesFile = File("tacusci.properties")

        private fun getDefaultPropertiesList(): List<Pair<String, String>> {
            return listOf(Pair("server-address", "localhost"),
                    Pair("port", "1025"),
                    Pair("using-ssl-on-proxy", "false"),
                    Pair("schema-name", "tacusci"),
                    Pair("db-url", "jdbc:mysql://localhost"),
                    Pair("root-username", "admin_tacusci"),
                    Pair("root-password", "Password1234!"),
                    Pair("root-email", ""),
                    Pair("color-theme", "dark"),
                    Pair("max-threads", ""),
                    Pair("min-threads", ""),
                    Pair("thread-idle-timeout", ""),
                    Pair("session-idle-timeout", ""),
                    Pair("log-file", "tacusci.log"),
                    Pair("smtp-server-host", ""),
                    Pair("smtp-server-port", ""),
                    Pair("smtp-account-username", ""),
                    Pair("smtp-account-password", ""),
                    Pair("smtp-use-ttls", "false"),
                    Pair("page-title", "Tacusci Website"),
                    Pair("static-asset-folder", ""),
                    Pair("response-pages-folder", ""),
                    Pair("page-title-divider", "|"),
                    Pair("robots-file", ""),
                    Pair("reset-password-authhash-timeout-seconds", ""),
                    Pair("reset-password-from-address", ""),
                    Pair("reset-password-email-subject", ""),
                    Pair("reset-password-email-content-file", ""))
        }

        fun load() {
            val defaults: List<Pair<String, String>> = getDefaultPropertiesList()
            //TODO: this could probably be cleaned up more
            if (propertiesFile.doesNotExist()) {
                defaults.forEach { pair -> this.setProperty(pair.first, pair.second) }
                storeAll()
            } else {
                try {
                    this.load(propertiesFile.inputStream())
                    defaults.forEach { pair ->
                        if (getProperty(pair.first).isEmpty() || getProperty(pair.first).isBlank()) {
                            this.setProperty(pair.first, pair.second)
                        }
                    }
                    this.store(propertiesFile.outputStream(), "")
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }

            propertiesFile.inputStream().close()
            setupLoggers(Config.getProperty("log-file"))

            encryptStoredPassword()
        }

        override fun getProperty(key: String): String {
            val property = super.getProperty(key) ?: ""
            return property
        }

        override fun getProperty(key: String, defaultValue: String): String {
            return super.getProperty(key, defaultValue)
        }

        fun encryptStoredPassword() {
            val password = getProperty("root-password")
            val pattern = Pattern.compile("CRYPT\\((\\S*)\\)")
            val matcher = pattern.matcher(password)
            if (!matcher.find()) {
                //if existing password in the config has not been encrypted, then do so
                setProperty("root-password", "CRYPT(${Base64.getEncoder().encodeToString(PasswordStorage.createHash(password).toByteArray(Charset.forName("UTF-8")))})")
                storeAll()
            }
        }

        fun decryptStoredPassword(): String {
            val password = getProperty("root-password")
            val pattern = Pattern.compile("CRYPT\\((\\S*)\\)")
            val matcher = pattern.matcher(password)
            if (matcher.find()) return String(Base64.getDecoder().decode(matcher.group(1)), Charset.forName("UTF-8"))
            return ""
        }

        //Note: can apparently wrap this with a more detailed FileOutputStream
        fun storeAll() { store(propertiesFile.outputStream(), "") }

        fun getDefaultProperty(key: String): String {
            return getDefaultProperties().getProperty(key)
        }

        fun getDefaultProperty(key: String, defaultValue: String): String {
            return getDefaultProperties().getProperty(key, defaultValue)
        }

        private fun getDefaultProperties(): Properties {
            val defaultProperties = Properties()
            val defaultPropertyKeysAndValues = getDefaultPropertiesList()
            defaultPropertyKeysAndValues.forEach { pair -> defaultProperties.setProperty(pair.first, pair.second) }
            return defaultProperties
        }

        fun loadFromPropertiesFile(propertiesFile: File) {
            val defaults: List<Pair<String, String>> = getDefaultPropertiesList()
            File(this.getProperty("properties-file"))
            if (propertiesFile.doesNotExist()) {
                defaults.forEach { pair -> this.setProperty(pair.first, pair.second) }
                this.store(propertiesFile.outputStream(), "")
            } else {
                try {
                    this.load(propertiesFile.inputStream())
                    defaults.forEach { pair ->
                        if (getProperty(pair.first).isEmpty() || getProperty(pair.first).isBlank()) {
                            this.setProperty(pair.first, pair.second)
                        }
                    }
                    this.store(propertiesFile.outputStream(), "")
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }

        fun monitorPropertiesFile(application: Application) {
            fileWatcher = FileWatcher(File(this.getProperty("properties-file")))
            fileWatcher.action = {propertiesFileUpdate(application)}
            fileWatcher.start()
        }

        fun stopMonitoringPropertiesFile() {
            fileWatcher.stop()
        }

        fun propertiesFileUpdate(application: Application) {
            if (File(this.getProperty("properties-file")).exists()) {
                this.load(File(this.getProperty("properties-file")).inputStream())
                application.restartServer()
            }
        }

        fun setupLoggers(logFilePath: String) {

            val pattern = "%d{yyyy-MM-dd HH:mm:ss} %-5p %c{1}:%L - %m%n"
            //get explicitly set debug log level
            val threshold = if (CliOptions.getFlag("debug") && !CliOptions.getFlag("disable-debug-output")) Level.DEBUG else Level.INFO

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

        //NOTE: this is technically deprecated, but should keep this as it works well to pass lists from config
        // in a list format, eg. ["some_folder", "somewhere_over_the_rainbow", "/full random path with spaces/hi there"]
        /*
        fun getStaticAssetFoldersList(): MutableList<String> {
            var configString = getProperty("static_asset_folders")
            val staticAssetFoldersList = mutableListOf<String>()
            if (configString.startsWith("[") && configString.endsWith("]")) {
                configString = configString.removePrefix("[").removeSuffix("]")
                configString.split(",").forEach {
                    staticAssetFoldersList.add(it.replaceFirst(" ", "").removeSuffix(" "))
                }
            }
            return staticAssetFoldersList
        }
        */
    }
}
