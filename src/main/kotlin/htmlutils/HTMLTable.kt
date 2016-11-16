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

    var className = ""

    constructor() {}

    constructor(columnNames: List<String>) {
        this.columnNames = columnNames
    }

    fun addRow(rowContent: List<String>) {
        rows.add(rowContent)
    }

    fun create(): String {
        val model = StringBuilder()

        model.append("<table class=\"$className\">")
        model.append("<thead>")
        model.append("<tr>")
        for (columnName in columnNames) { model.append("<th>$columnName</th>") }
        model.append("</tr>")
        model.append("</thead>")

        model.append("<tbody>")
        rows.forEach { rowContent ->
            model.append("<tr>")
            for (content in rowContent) {
                model.append("<td>$content</td>")
            }
            model.append("</tr>")
        }
        model.append("</tbody>")
        model.append("</table>")
        return model.toString()
    }

    private fun removeTableFooter() {
        if (model.contains("</table>")) { model.delete("</table>") }
    }
}