package com.vag.lmsapp.model

sealed class PrinterItem(val characterLength: Int, val showPrice: Boolean) {
    open fun formattedString(): String = toString()

    class SingleLine(characterLength: Int) : PrinterItem(characterLength, false) {
        override fun toString(): String = "-".repeat(characterLength)
    }

    class Cutter(characterLength: Int): PrinterItem(characterLength, false) {
        override fun toString(): String = "\n"+(".".repeat(characterLength)) + "\n"
    }

    open class Header(val text: String?) : PrinterItem(0, false) {
        override fun toString(): String = text.orEmpty()
        override fun formattedString(): String = "<b>${text.orEmpty()}</b>"
    }

    class HeaderDoubleCenter(text: String?) : Header(text) {
        override fun formattedString(): String = "[C]<font size='big'><b>${text.orEmpty()}</b></font>"
    }

    open class ListItem(
        characterLength: Int,
        showPrice: Boolean,
        val count: Float,
        val title: String,
        val price: Float
    ) : PrinterItem(characterLength, showPrice) {

        override fun toString(): String {
            val countStr = if (count > 0)
                if (count % 1 == 0f) {
                    String.format("(%.0f)", count)
                } else {
                    String.format("(%.1f)", count)
                }
            else ""
            val priceStr = if(showPrice) String.format("P%.1f", price) else ""
            val totalLength = countStr.length + title.length + priceStr.length
            val separatorLength = characterLength - totalLength

            return if (totalLength <= characterLength) {
                "$countStr$title${" ".repeat(separatorLength)}$priceStr"
            } else {
                val remainingLength = characterLength - priceStr.length
                val truncatedTitle = (countStr + title).take(remainingLength - 3)
                "$truncatedTitle...$priceStr"
            }
        }
    }

    class ListItemBold(
        characterLength: Int,
        showPrice: Boolean,
        count: Float,
        title: String,
        price: Float
    ) : ListItem(characterLength, showPrice, count, title, price) {
        override fun formattedString(): String {
            return "<b>${toString()}</b>"
        }
    }

    open class DefinitionTerm(
        characterLength: Int,
        val term: String,
        val definition: String?
    ) : PrinterItem(characterLength, false) {

        override fun toString(): String {
            val spaces = " ".repeat(11 - term.length)
            return "$spaces$term: ${definition.orEmpty()}"
        }
    }

    class DefinitionTermBold(
        characterLength: Int,
        term: String,
        definition: String?
    ) : DefinitionTerm(characterLength, term, definition)

    open class TextCenter(val text: String?) : PrinterItem(0, false) {
        override fun toString(): String = text.orEmpty()
        override fun formattedString(): String = "[C]${text.orEmpty()}"
    }

    class TextCenterTall(text: String?) : TextCenter(text) {
        override fun formattedString(): String = "[C]<font size='tall'>${text.orEmpty()}</font>"
    }
}
