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
            val lineRegex = """([a-zA-Z]+)\s([a-zA-Z]+)\s([a-zA-Z]+;)""".toRegex()
            val matches = lineRegex.find(line)

            if (matches != null && matches.groups.size > 1) {
                for (index in 0..matches.groupValues.size - 1) {
                    if (index == 1 && matches.groupValues[index].toLowerCase() != "create") return@forEach
                    if (index == 2 && matches.groupValues[index].toLowerCase() != "schema") return@forEach
                    if (index == 3) schemas.put(matches.groupValues[3], line) else return@forEach
                }
            }
        }
    }

    private fun selectSQLTables() {
        //regex for capturing create table content \(\X*\);
        file.readLines().forEach { line ->
            val lineRegex = """([a-zA-Z]+)\s([a-zA-Z]+)\s(\W[a-zA-Z]+\W).(\W[a-zA-Z]+\W)""".toRegex()
            val matches = lineRegex.find(line)

            if (matches != null && matches.groups.size > 1) {
                for (index in 0..matches.groupValues.size -1) {
                    if (index == 1 && matches.groupValues[index].toLowerCase() != "create") return@forEach
                    if (index == 2 && matches.groupValues[index].toLowerCase() != "table") return@forEach
                    if (index == 3) tables.put(matches.groupValues[3], "") else return@forEach
                }
            }

            var firstRegParenthesesIndex = 0



            println(line)
        }
    }
}