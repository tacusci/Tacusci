package utils

import j2html.TagCreator.*
import j2html.tags.ContainerTag
import j2html.tags.Tag
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
    private val rows: MutableList<List<List<Tag>>> = mutableListOf()

    var className = ""

    constructor() {}

    constructor(columnNames: List<String>) {
        this.columnNames = columnNames
    }

    fun addRow(rowContent: List<List<Tag>>) {
        rows.add(rowContent)
    }

    fun render(): Tag {
        val model = StringBuilder()

        val table = table().withClass(className).with(thead()).with(tr())
        for (columnName in columnNames) { table.with(th(columnName)) }
        table.with(tr()).with(thead())
        table.with(tbody())

        rows.forEach rowsLoop@ { row ->
            if (row.isEmpty()) { return@rowsLoop }
            table.with(tr())
            row.forEach { rowContent ->
                rowContent.forEach { rowColumnElement ->
                    table.with(td().with(rowColumnElement))
                }
            }
            table.with(tr())
        }
        table.with(tbody())
        return table
    }
}