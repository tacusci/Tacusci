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

        val fileContent = file.readText()
        //find all instances of a pattern like this: CREATE schema tvf;
        val matches = """([a-zA-Z]+)\s([a-zA-Z]+)\s([a-zA-Z]+;)""".toRegex().find(fileContent)

        if (matches != null && matches.groupValues.size > 1) {
            (0..matches.groupValues.size-1).forEach { i ->
                if (i == 1 && matches.groupValues[i].toLowerCase() != "create") return@forEach
                if (i == 2 && matches.groupValues[i].toLowerCase() != "schema") return@forEach
                if (i == 3) schemas.put(matches.groupValues[3], matches.groupValues[0]) else return@forEach
            }
        }
    }

    private fun selectSQLTables() {

        //TODO: Finish working out table schema extraction for easy caching and then indexing

        //regex for capturing create table content \(\X*\);

        val fileContent = file.readText()
        //find all instances of pattern like this: CREATE TABLE `tvf`.`groups`
        val matches = """([a-zA-Z]+)\s([a-zA-Z]+)\s(\W[a-zA-Z]+\W).(\W[a-zA-Z]+\W)""".toRegex().find(fileContent)

        var tableName = ""

        if (matches != null && matches.groupValues.size > 1) {
            (0..matches.groupValues.size-1).forEach { i ->
                println(i)
                if (i == 1 && matches.groupValues[i].toLowerCase() != "create") return@forEach
                if (i == 2 && matches.groupValues[i].toLowerCase() != "table") return@forEach
                if (i == 3) tables.put(matches.groupValues[3], matches.groupValues[0]) else return@forEach
                if (i == 4) println(matches.groupValues[4])
                //need to force include index 4
            }
        }
    }
}