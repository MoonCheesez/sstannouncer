package sst.com.anouncements.feed.data

import org.apache.commons.text.StringEscapeUtils
import java.text.SimpleDateFormat
import java.util.*

data class Entry(
    val id: String,
    val publishedDate: Date,
    val updatedDate: Date,
    val authorName: String,
    val url: String,
    val title: String,
    val content: String
) {
    val relativePublishedDate: String by lazy { relativeDate(publishedDate) }

    val contentWithoutHTML: String by lazy {
        // Remove HTML tags
        val content = this.content.replace("<[^>]*>".toRegex(), "")
        // Unescape characters, e.g. converting &lt; to <
        StringEscapeUtils.unescapeHtml4(content)
    }

    private fun relativeDate(date: Date): String {
        // TODO: Make the date more relative, like "today", "3 days ago"
        val dateFormat = SimpleDateFormat("d MMM", Locale.getDefault())
        return dateFormat.format(date)
    }

    override fun equals(other: Any?) =
        other is Entry &&
        id == other.id &&
        updatedDate.compareTo(other.updatedDate) == 0

    override fun hashCode() = (id + updatedDate).hashCode()
}