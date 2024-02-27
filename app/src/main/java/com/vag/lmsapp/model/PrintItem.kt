package com.vag.lmsapp.model

data class PrintItem(
    val count: Int?,
    val title: String?,
    val price: Float?,
    val characterLength: Int = 32,
    val definition: String? = null,
    val header: String? = null
) {
    constructor(header: String?, characterLength: Int = 32) : this(null, null, null, header = header, characterLength = characterLength)
    constructor(title: String?, definition: String?, characterLength: Int = 32): this(null, title, null, characterLength, definition)
    companion object {
        fun singleLine(characterLength: Int) : PrintItem {
            return PrintItem("-".repeat(characterLength))
        }
    }

    override fun toString(): String {
        val countStr = count?.let { "(*$it)" } ?: ""
        val titleStr = title?.let { "$title" } ?: ""
        val priceStr = price?.let { String.format("P%.2f", it) } ?: ""
        val totalLength = countStr.length + titleStr.length + priceStr.length
        val separatorLength = characterLength - totalLength

        return when {
            header != null -> header
            definition != null -> "$titleStr :$definition"
            totalLength <= characterLength -> "$countStr$titleStr${" ".repeat(separatorLength)}$priceStr"
            else -> {
                val remainingLength = characterLength - priceStr.length
                val truncatedTitle = (countStr + titleStr).take(remainingLength)
                "$truncatedTitle$priceStr"
            }
        }
    }

    fun formattedString() : String {
        val countStr = count?.let { "(*$it)" } ?: ""
        val titleStr = title?.let { "$title " } ?: ""
        val priceStr = price?.let { " P$it" } ?: ""
        val totalLength = countStr.length + titleStr.length + priceStr.length
//        val separatorLength = characterLength - totalLength

        return when {
            header != null -> "[L]<b>${header}</b>\n"
            definition != null -> "[L]$titleStr:${definition}\n"
            price != null -> "[L]$countStr$titleStr[R]${priceStr}\n"
//            totalLength <= characterLength -> "$countStr$titleStr${" ".repeat(separatorLength)}$priceStr"
            else -> {
                ""
//                val remainingLength = characterLength - priceStr.length
//                val truncatedTitle = (countStr + titleStr).take(remainingLength)
//                "$truncatedTitle$priceStr"
            }
        }
    }

//    override fun toString(): String {
//        val countStr = count?.let { "(*$it)" } ?: ""
//        val titleStr = title ?: ""
//        val priceStr = price?.let { "P$it" } ?: ""
//        val separatorLength = 32 - (countStr.length + titleStr.length + priceStr.length)
//        val separator = " ".repeat(separatorLength.coerceAtLeast(0))
//        return "$countStr$titleStr$separator$priceStr"
//    }
//    override fun toString(): String {
//        val count = if(count == null) "" else "(*$count)"
//        val title = title ?: ""
//        val price = if(price == null) "" else "P$price"
//        val separator = ""
//        return "$count$title$separator$price"
//    }
}
