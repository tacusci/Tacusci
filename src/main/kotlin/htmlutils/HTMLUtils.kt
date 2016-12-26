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

import org.w3c.tidy.Tidy
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream

/**
 * Created by tauraamui on 08/11/2016.
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

    fun genLink(linkLocation: String, name: String): String {
        val linkString = "<a href=$linkLocation>$name</a>"
        return linkString
    }

    fun genParagraph(content: String): String {
        return "<p>$content</p>"
    }

    fun genList(list: List<Any>): String {
        val stringBuilder = StringBuilder()
        stringBuilder.append("<ul>")
        list.forEach { element ->
            stringBuilder.append("<li>$element</li>")
        }
        stringBuilder.append("</ul><ul>")
        return stringBuilder.toString()
    }
}