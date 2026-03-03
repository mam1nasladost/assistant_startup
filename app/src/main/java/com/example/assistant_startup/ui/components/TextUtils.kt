package com.example.assistant_startup.ui.components

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle

fun String.highlightQuery(query: String, highlightColor: Color = Color.Yellow): AnnotatedString {
    if (query.isBlank()) return AnnotatedString(this)

    return buildAnnotatedString {
        val matches = Regex(Regex.escape(query), RegexOption.IGNORE_CASE).findAll(this@highlightQuery)
        var lastIndex = 0

        for (match in matches) {
            append(this@highlightQuery.substring(lastIndex, match.range.first))
            withStyle(SpanStyle(background = highlightColor)) {
                append(match.value)
            }
            lastIndex = match.range.last + 1
        }
        append(this@highlightQuery.substring(lastIndex, this@highlightQuery.length))
    }
}

fun String.getSnippetAtIndex(startIndex: Int, query: String, contextChars: Int = 30): AnnotatedString {
    val endMatchIndex = startIndex + query.length
    val start = (startIndex - contextChars).coerceAtLeast(0)
    val end = (endMatchIndex + contextChars).coerceAtMost(this.length)

    val prefix = if (start > 0) "..." else ""
    val suffix = if (end < this.length) "..." else ""

    val snippet = this.substring(start, end)
    val adjustedStart = (startIndex - start)

    return buildAnnotatedString {
        append(prefix)
        append(snippet)
        append(suffix)

        addStyle(
            style = SpanStyle(background = Color(0xFFFBC02D), fontWeight = FontWeight.Bold),
            start = prefix.length + adjustedStart,
            end = prefix.length + adjustedStart + query.length
        )
    }
}