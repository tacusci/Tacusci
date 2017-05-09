/*
 * # DON'T BE A DICK PUBLIC LICENSE
 *
 * > Version 1.1, December 2016-2017
 *
 * > Copyright (C) 2016-2017 Adam Prakash Lewis
 *
 *  Everyone is permitted to copy and distribute verbatim or modified
 *  copies of this license document.
 *
 * > DON'T BE A DICK PUBLIC LICENSE
 * > TERMS AND CONDITIONS FOR COPYING, DISTRIBUTION AND MODIFICATION
 *
 *  1. Do whatever you like with the original work, just don't be a dick.
 *
 *      Being a dick includes - but is not limited to - the following instances:
 *
 * 	 1a. Outright copyright infringement - Don't just copy this and change the name.
 * 	 1b. Selling the unmodified original with no work done what-so-ever, that's REALLY being a dick.
 * 	 1c. Modifying the original work to contain hidden harmful content. That would make you a PROPER dick.
 *
 *  2. If you become rich through modifications, related works/services, or supporting the original work,
 *  share the love. Only a dick would make loads off this work and not buy the original work's
 *  creator(s) a pint.
 *
 *  3. Code is provided with no warranty. Using somebody else's code and bitching when it goes wrong makes
 *  you a DONKEY dick. Fix the problem yourself. A non-dick would submit the fix back.
 */

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
        //create the table string stuct with the column names mapped to -> trs
        val table = table().withClass(className).attr("align", "center").with(thead().with(tr().with(columnNames.map(::th))))

        //for each row, make a column entry for each non-empty element
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