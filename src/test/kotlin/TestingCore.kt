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

import app.core.handlers.GroupHandler
import app.core.handlers.UserHandler
import com.nhaarman.mockito_kotlin.whenever
import database.daos.DAOManager
import database.models.Group
import org.mockito.Mockito
import spark.Request
import spark.Session
import utils.Config
import utils.InternalResourceFile
import java.util.*

/**
 * Created by alewis on 30/01/2017.
 */

object TestingCore {

    fun setupConfig() {
        Config.setProperty("server-address", "localhost")
        Config.setProperty("port", "1025")
        Config.setProperty("using-ssl-on-proxy", "false")
        Config.setProperty("schema-name", "tacusci")
        Config.setProperty("db-url", "jdbc:mysql://localhost")
        Config.setProperty("root-username", "admin_tacusci")
        Config.setProperty("root-password", "Password1234!")
        Config.setProperty("root-email", "admin@testing.com")
        Config.setProperty("color-theme", "dark")
        Config.setProperty("max-threads", "")
        Config.setProperty("min-threads", "")
        Config.setProperty("max-db-connections", "8")
        Config.setProperty("thread-idle-timeout", "")
        Config.setProperty("session-idle-timeout", "")
        Config.setProperty("log-file", "tacusci.log")
        Config.setProperty("smtp-server-host", "")
        Config.setProperty("smtp-server-port", "")
        Config.setProperty("smtp-account-username", "")
        Config.setProperty("smtp-account-password", "")
        Config.setProperty("smtp-use-ttls", "false")
        Config.setProperty("page-title", "Tacusci Website")
        Config.setProperty("static-asset-folder", "")
        Config.setProperty("response-pages-folder", "")
        Config.setProperty("page-title-divider", "|")
        Config.setProperty("robots-file", "")
        Config.setProperty("reset-password-authhash-timeout-seconds", "")
        Config.setProperty("reset-password-from-address", "")
        Config.setProperty("reset-password-email-subject", "")
        Config.setProperty("reset-password-email-content-file", "")
    }

    fun setupSetEnv() {

        setupConfig()
        val dbProperties = Properties()
        val dbURL = Config.getProperty("db-url")
        dbProperties.setProperty("user", "root")
        dbProperties.setProperty("password", "")
        dbProperties.setProperty("useSSL", "false")
        dbProperties.setProperty("autoReconnect", "false")
        DAOManager.init(dbURL, dbProperties)
        DAOManager.connect()
        //run the set up schemas if they don't exist
        DAOManager.setup(InternalResourceFile("/sql/test_sql_setup_script.sql"))
        DAOManager.disconnect()
        //I AM ALMOST CERTAIN I ACTUALLY NEED TO DO THIS DISCONNECT AND RE-CONNECT
        //reconnect at the requested specific schema
        DAOManager.init(dbURL + "/${Config.getProperty("schema-name")}", dbProperties)

        GroupHandler.createGroup(Group(name = "admins"))
        GroupHandler.createGroup(Group(name = "members"))
        UserHandler.createRootAdmin()
    }

    fun mockRequest(): Request {
        val mockRequest = Mockito.mock(Request::class.java)
        whenever(mockRequest.ip()).thenReturn("0.0.0.0:80")
        whenever(mockRequest.session()).thenReturn(Mockito.mock(Session::class.java))
        return mockRequest
    }
}
