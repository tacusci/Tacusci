package db

import java.io.File
import java.util.*

/**
 * Created by alewis on 07/12/2016.
 */
class DatabaseSetupFile(var file: File) {

    val schemas = HashMap<String, String>()
    val tables = HashMap<String, String>()

    fun pass() {
        if (file.exists()) {
            if (file.isFile) {
                if (file.name.endsWith(".sql")) {
                    selectSQLSchemas()
                    selectSQLTables()
                }
            }
        }
    }

    private fun selectSQLSchemas() {
        file.readLines().forEach { line -> val lowerCaseLine = line.toLowerCase()

            val lineRegex = """([a-zA-Z]+) ([a-zA-Z]+) ([a-zA-Z]+;)""".toRegex()
            val matches = lineRegex.find(line)

            var schemaName = ""

            if (matches != null && matches.groups.size > 1) {
                matches.groupValues.forEachIndexed { i, value ->
                    if (i == 0 && value.toLowerCase() == "create") {
                        if (i == 1 && value.toLowerCase() == "schema") {
                            if (i == 2) schemaName = value.replace(";","")
                            schemas.put(schemaName, line)
                        }
                    }
                }
            }
        }
    }

    private fun selectSQLTables() {}
}