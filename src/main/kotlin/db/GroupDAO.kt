package db

import db.models.Group
import mu.KLogging
import java.sql.Connection
import java.sql.SQLException

/**
 * Created by alewis on 20/12/2016.
 */
class GroupDAO(connection: Connection, tableName: String) : GenericDAO(connection, tableName) {

    companion object : KLogging()

    fun insertGroup(group: Group) {
        try {

            val createGroupStatementString = "INSERT INTO $tableName (groupname) VALUES (?)"
            val preparedStatement = connection?.prepareStatement(createGroupStatementString)
            preparedStatement?.setString(1, group.name)

            val results: Boolean? = preparedStatement?.execute()
            var count = 0
            if (results!!) {
                logger.info("Insert group ResultSet data displayed here")
            } else {
                count = preparedStatement?.updateCount!!
                if (count >= 0) {
                    logger.info("DDL or update data displayed here.")
                } else {
                    logger.info("No more results to process")
                }
            }
            connection?.commit()
            preparedStatement?.close()

        } catch (e: SQLException) { logger.error(e.message) }
    }
}