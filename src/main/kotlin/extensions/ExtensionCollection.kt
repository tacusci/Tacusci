package extensions

import java.io.File
import java.io.InputStream
import java.nio.charset.Charset

/**
 * Created by alewis on 15/11/2016.
 */


fun StringBuilder.delete(string: String) {
    val i = this.indexOf(string)
    if (i != -1) {
        this.delete(i, i + string.length)
    }
}

fun InputStream.readTextAndClose(charset: Charset = Charsets.UTF_8): String {
    return this.bufferedReader(charset).use { it.readText() }
}

fun File.doesNotExist(): Boolean {
    return !this.exists()
}