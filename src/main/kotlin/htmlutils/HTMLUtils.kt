package htmlutils

import org.w3c.tidy.Tidy
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream

/**
 * Created by tauraamui on 08/11/2016.
 */

object HTMLUtils {

    fun genRadioButton(name: String, value: String): String {
        val radioButtonString = "<input type=\"radio\" name=\"$name\" id=\"$name\" value=\"$value\">"
        return radioButtonString
    }

    fun genCheckBox(name: String, value: String, checked: Boolean): String {
        var checkedString = ""
        if (checked) checkedString = " checked"
        val checkboxString = "<input type=\"checkbox\" name=\"$name\" id=\"$name\" value=\"$value\"$checkedString>"
        return checkboxString
    }

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

    fun generateList(list: List<Any>): StringBuilder {
        val stringBuilder = StringBuilder()
        stringBuilder.append("<ul>")
        list.forEach { element ->
            stringBuilder.append("<li>$element</li>")
        }
        stringBuilder.append("</ul><ul>")
        return stringBuilder
    }
}