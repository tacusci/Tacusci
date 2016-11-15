package htmlutils

import extensions.delete
import java.util.*

/**
 * Created by alewis on 08/11/2016.
 */

class HTMLTable {

    private val model = StringBuilder()
    private var columnNames = listOf("")
    private val rows = mutableListOf(listOf(""))

    constructor() {}

    constructor(columnNames: List<String>) {
        this.columnNames = columnNames
    }

    fun addRow(rowContent: List<String>) {
        rows.add(rowContent)
    }

    fun create(): String {
        val model = StringBuilder()

        model.append("<table>")

        model.append("<tr>")
        for (columnName in columnNames) { model.append("<td>$columnName</td>") }
        model.append("</tr>")

        rows.forEach { rowContent ->
            model.append("<tr>")
            for (content in rowContent) {
                model.append("<td>$content</td>")
            }
            model.append("</tr>")
        }
        return model.toString()
    }

    private fun removeTableFooter() {
        if (model.contains("</table>")) { model.delete("</table>") }
    }
}