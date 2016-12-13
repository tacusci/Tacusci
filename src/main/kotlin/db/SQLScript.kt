package db

import mu.KLogging
import java.io.File
import java.sql.Connection
import java.util.*

/**
 * Created by alewis on 13/12/2016.
 */
class SQLScript(var file: File) {

    companion object : KLogging()

    private val statements = mutableListOf("")

    fun parse() {
        statements.clear()
        val fileScanner = Scanner(file.inputStream())
        fileScanner.useDelimiter("(;(\r)?\n)|(--\n)")
        while (fileScanner.hasNext()) {
            var line = fileScanner.next()
            if (line.startsWith("/*") && line.endsWith("*/")) {
                val i = line.indexOf(' ')
                line = line.substring(i + 1, line.length - " */".length)
                if (line.trim().isNotEmpty()) {
                    statements.add(line)
                }
            }
        }
    }

    fun executeStatements(connection: Connection) {
        statements.forEach { statement ->
            val preparedStatement = connection?.prepareStatement("$statement;")
            logger.info("Executing statement: ${statement.replace("\n", "")};")
            preparedStatement?.execute()
            preparedStatement?.closeOnCompletion()
        }
    }
}