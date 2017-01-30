import com.nhaarman.mockito_kotlin.whenever
import database.daos.DAOManager
import database.models.Group
import handlers.GroupHandler
import handlers.UserHandler
import utils.Config
import utils.InternalResourceFile
import java.io.File
import java.util.*

/**
 * Created by alewis on 30/01/2017.
 */

object TestingSetup {

    fun setupSetEnv() {

        Config.setProperty("default_admin_user", "tvf_admin")
        Config.setProperty("default_admin_password", "testing1234")
        Config.setProperty("server_address", "localhost")
        Config.setProperty("schema_name", "tvf_testing")
        Config.setProperty("port", "80")
        Config.setProperty("log_file", "tvf.log")
        Config.setProperty("default_admin_email", "admin_tvf@tvf.net")
        Config.setProperty("properties_file", "tvf_testing.properties")
        Config.setProperty("db_url", "jdbc:mysql://localhost")

        val dbProperties = Properties()
        val dbURL = Config.getProperty("db_url")
        dbProperties.setProperty("user", "tvf_admin")
        dbProperties.setProperty("password", "testing1234")
        dbProperties.setProperty("useSSL", "false")
        dbProperties.setProperty("autoReconnect", "false")
        DAOManager.init(dbURL, dbProperties)
        DAOManager.connect()
        //run the set up schemas if they don't exist
        DAOManager.setup(InternalResourceFile("/sql/test_sql_setup_script.sql"))
        DAOManager.disconnect()
        //I AM ALMOST CERTAIN I ACTUALLY NEED TO DO THIS DISCONNECT AND RE-CONNECT
        //reconnect at the requested specific schema
        DAOManager.init(dbURL + "/${Config.getProperty("schema_name")}", dbProperties)

        GroupHandler.createGroup(Group("admins"))
        GroupHandler.createGroup(Group("members"))
        UserHandler.createRootAdmin()
    }
}
