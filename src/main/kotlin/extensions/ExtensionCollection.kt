package extensions

/**
 * Created by alewis on 15/11/2016.
 */


fun StringBuilder.delete(string: String) {
    val i = this.indexOf(string)
    if (i != -1) {
        this.delete(i, i + string.length)
    }
}