package htmlutils

import org.w3c.dom.bootstrap.DOMImplementationRegistry
import org.w3c.dom.ls.DOMImplementationLS
import org.w3c.tidy.Tidy
import org.xml.sax.InputSource
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.StringReader
import javax.xml.parsers.DocumentBuilderFactory

/**
 * Created by tauraamui on 08/11/2016.
 */

object HTMLUtils {
    fun genRadioButton(name: String, id: String, value: String): String {
        val radioButtonString = "<input type='radio' name='$name' id='$id' value='$value'>"
        return radioButtonString
    }

    fun genCheckBox(name: String, id: String, value: String, checked: Boolean): String {
        var checkedString = ""
        if (checked) checkedString = "checked"
        val checkBoxString = "<input type='checkbox' name='$name' id='$id' value='$value' $checkedString>"
        return checkBoxString
    }

    fun format(html: String): String {
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