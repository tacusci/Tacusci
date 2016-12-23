/*
# DON'T BE A DICK PUBLIC LICENSE

> Version 1.1, December 2016

> Copyright (C) 2016 Adam Prakash Lewis
 
 Everyone is permitted to copy and distribute verbatim or modified
 copies of this license document.

> DON'T BE A DICK PUBLIC LICENSE
> TERMS AND CONDITIONS FOR COPYING, DISTRIBUTION AND MODIFICATION

 1. Do whatever you like with the original work, just don't be a dick.

     Being a dick includes - but is not limited to - the following instances:

	 1a. Outright copyright infringement - Don't just copy this and change the name.  
	 1b. Selling the unmodified original with no work done what-so-ever, that's REALLY being a dick.  
	 1c. Modifying the original work to contain hidden harmful content. That would make you a PROPER dick.  

 2. If you become rich through modifications, related works/services, or supporting the original work,
 share the love. Only a dick would make loads off this work and not buy the original work's 
 creator(s) a pint.
 
 3. Code is provided with no warranty. Using somebody else's code and bitching when it goes wrong makes 
 you a DONKEY dick. Fix the problem yourself. A non-dick would submit the fix back.
 */
 
 
 
 package htmlutils

import extensions.delete
import java.util.*

/**
 * Created by alewis on 08/11/2016.
 */

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