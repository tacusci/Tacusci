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
        val schemaMatches = """([a-zA-Z]+)\s([a-zA-Z]+)\s([a-zA-Z]+;)""".toRegex().find(fileContent)

        if (schemaMatches != null && schemaMatches.groupValues.size > 1) {
            (0..schemaMatches.groupValues.size-1).forEach { i ->
                if (i == 1 && schemaMatches.groupValues[i].toLowerCase() != "create") return@forEach
                if (i == 2 && schemaMatches.groupValues[i].toLowerCase() != "schema") return@forEach
                if (i == 3) schemas.put(schemaMatches.groupValues[3].replace(";", ""), schemaMatches.groupValues[0]) else return@forEach
            }
        }
    }

    private fun selectSQLTables() {

        //TODO: Finish working out table schema extraction for easy caching and then indexing

        //regex for capturing create table content \(\X*\);

        val fileContent = file.readText()
        //find all instances of pattern like this: CREATE TABLE `tvf`.`groups`
        val tableCreateStructMatches = """([a-zA-Z]+)\s([a-zA-Z]+)\s(\W[a-zA-Z]+\W).(\W[a-zA-Z]+\W)""".toRegex().find(fileContent)

        if (tableCreateStructMatches != null && tableCreateStructMatches.groupValues.size > 1) {
            val tableName = StringBuilder()
            (0..tableCreateStructMatches.groupValues.size-1).forEach { i ->
                if (i == 1 && tableCreateStructMatches.groupValues[i].toLowerCase() != "create") return@forEach
                if (i == 2 && tableCreateStructMatches.groupValues[i].toLowerCase() != "table") return@forEach
                if (i == 3) tableName.append("'${tableCreateStructMatches.groupValues[i]}'")
                if (i == 4) {tableName.append(".'${tableCreateStructMatches.groupValues[i]}'"); tables.put(tableName.toString(), "")}
                //need to force include index 4
            }
        }

        val tableStructContentMatches = """\((?:\P{M}\p{M}*)*\);""".toRegex().find(fileContent)

        if (tableStructContentMatches != null && tableStructContentMatches.groupValues.size > 1) {
            (0..tableStructContentMatches.groupValues.size-1).forEach { i ->
                println(tableStructContentMatches.groupValues[i])
            }
        }
    }
}