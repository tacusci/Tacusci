package db

import java.io.File

/**
 * Created by alewis on 07/12/2016.
 */
class SchemaCreationFile(var file: File) {

    val schemas = listOf<String>()
    val tables = listOf<String>()

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

    }
}