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
        file.readLines().forEach { line ->
            val lineRegex = """([a-zA-Z]+) ([a-zA-Z]+) ([a-zA-Z]+;)""".toRegex()
            val matches = lineRegex.find(line)

            if (matches != null && matches.groups.size > 1) {
                for (index in 0..matches.groupValues.size - 1) {
                    if (index == 1 && matches.groupValues[index].toLowerCase() != "create") return@forEach
                    if (index == 2 && matches.groupValues[index].toLowerCase() != "schema") return@forEach
                    schemas.put(matches.groupValues[1], line)
                    schemas.put(matches.groupValues[2], line)
                    schemas.put(matches.groupValues[3], line)
                }
            }
        }
    }

    private fun selectSQLTables() {
    }
}