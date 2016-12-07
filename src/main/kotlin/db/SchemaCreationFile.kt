package db

import java.io.File

/**
 * Created by alewis on 07/12/2016.
 */
class SchemaCreationFile(var file: File) {

    val schemas = mutableListOf<String>()
    val tables = mutableListOf<String>()

    fun pass() {
        if (file.exists()) {
            if (file.isFile) {
                if (file.name.endsWith(".sql")) {
                    passAsSQL()
                }
            }
        }
    }

    private fun passAsSQL() {
        file.readLines().forEach { line ->
            val lowerCaseLine = line.toLowerCase()
            if (lowerCaseLine.contains("create")) {
                if (lowerCaseLine.contains("schema")) {
                    val schemaNameRegex = """([a-zA-Z]+;)""".toRegex()
                    if (schemaNameRegex.containsMatchIn(lowerCaseLine)) {
                        schemaNameRegex.find(lowerCaseLine)?.groupValues?.forEach { schemas.add(it.replace(";","")) }
                    }
                }
            }
        }
        schemas.forEach(::println)
    }
}