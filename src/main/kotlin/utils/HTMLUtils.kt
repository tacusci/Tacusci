package utils

import j2html.TagCreator.*
import j2html.tags.ContainerTag
import j2html.tags.Tag
import org.w3c.tidy.Tidy
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.util.stream.Collectors

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
        val table = table().withClass(className).attr("align", "center").with(thead().with(tr().with(columnNames.map(::th))))

        rows.filter { it.isNotEmpty() }.forEach { row ->
            val tableRow = tr()
            row.filter { it.isNotEmpty() }.forEach { column ->
                val tableColumn = td()
                column.forEach { element -> tableColumn.children.add(element) }
                tableRow.with(tableColumn)
            }
            table.with(tableRow)
        }
        return table
    }

    fun rowCount(): Int { return rows.size }
    fun columnCount(): Int { return columnNames.size }
}