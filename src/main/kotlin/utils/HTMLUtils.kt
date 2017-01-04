package utils

import org.w3c.tidy.Tidy
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream

/**
 * Created by alewis on 04/01/2017.
 */

object HTMLUtils {

    fun formatMarkup(html: String): String {
        val tidy = Tidy()
        //tidy.xhtml = true
        tidy.indentContent = true
        tidy.tidyMark = false
        tidy.quiet = true
        tidy.showWarnings = false

        // HTML to DOM
        val htmlDOM = tidy.parseDOM(ByteArrayInputStream(html.toByteArray()), null)

        // Pretty Print
        val out = ByteArrayOutputStream()
        tidy.pprint(htmlDOM, out)
        return out.toString()
    }
}

class HTMLTable {

    private var columnNames = listOf("")
    private val rows: MutableList<List<String>> = mutableListOf()

    var className = ""

    constructor() {}

    constructor(columnNames: List<String>) {
        this.columnNames = columnNames
    }

    fun addRow(rowContent: List<String>) {
        rows.add(rowContent)
    }

    fun render(): String {
        val model = StringBuilder()

        model.append("<table class=\"$className\">")
        model.append("<thead>")
        model.append("<tr>")
        for (columnName in columnNames) { model.append("<th>$columnName</th>") }
        model.append("</tr>")
        model.append("</thead>")

        model.append("<tbody>")
        rows.forEach { rowContent ->
            if (rowContent.isNotEmpty()) {
                model.append("<tr>")
                for (content in rowContent) {
                    model.append("<td>$content</td>")
                }
                model.append("</tr>")
            }
        }
        model.append("</tbody>")
        model.append("</table>")
        return model.toString()
    }
}