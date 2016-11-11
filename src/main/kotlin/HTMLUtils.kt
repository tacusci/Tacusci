package htmlutils

import org.w3c.tidy.Tidy
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream

/**
 * Created by tauraamui on 08/11/2016.
 */

abstract class HTMLElement

class CheckBox : HTMLElement() {
    var name = ""
    var id = ""
    var value = ""
    var isChecked = false

    override fun toString(): String {
        var checkedString = ""
        if (isChecked) checkedString = " checked"
        return "<input type='checkbox' name='$name' id='$id' value='$value' $checkedString>"
    }
}

object HTMLUtils {

    fun genRadioButton(name: String, id: String, value: String): String {
        val radioButtonString = "<input type='radio' name='$name' id='$id' value='$value'>"
        return radioButtonString
    }

    fun formatMarkup(html: String): String {
        val tidy = Tidy()
        tidy.xhtml = true
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