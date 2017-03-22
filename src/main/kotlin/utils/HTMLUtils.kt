package utils

import j2html.TagCreator.*
import j2html.tags.Tag

/**
 * Created by alewis on 04/01/2017.
 */


class HTMLTable {

    private var columnNames = listOf("")
    private val rows: MutableList<List<List<Tag>>> = mutableListOf()

    var className = ""

    constructor() {}

    constructor(columnNames: List<String>) {
        this.columnNames = columnNames
    }

    fun setColumnNames(columnNames: List<String>) { this.columnNames = columnNames }

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