package db

import java.io.File
import java.util.*

/**
 * Created by alewis on 13/12/2016.
 */
class SQLScript(var file: File) {

    val statements = mutableListOf("")

    fun parse() {
        statements.clear()
        val fileScanner = Scanner(file.inputStream())
        fileScanner.useDelimiter("(;(\r)?\n)|(--\n)")
        while (fileScanner.hasNext()) { println(fileScanner.next()) }
    }
}