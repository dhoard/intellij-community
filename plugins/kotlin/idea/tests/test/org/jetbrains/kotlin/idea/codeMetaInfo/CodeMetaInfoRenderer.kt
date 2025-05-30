// Copyright 2000-2022 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package org.jetbrains.kotlin.idea.codeMetaInfo

import com.intellij.util.containers.Stack
import org.jetbrains.kotlin.codeMetaInfo.model.CodeMetaInfo
import java.io.File

object CodeMetaInfoRenderer {
    fun renderTagsToText(codeMetaInfos: List<CodeMetaInfo>, originalText: String): StringBuilder {
        return StringBuilder().apply {
            renderTagsToText(this, codeMetaInfos, originalText)
        }
    }

    fun renderTagsToText(builder: StringBuilder, codeMetaInfos: List<CodeMetaInfo>, originalText: String) {
        if (codeMetaInfos.isEmpty()) {
            builder.append(originalText)
            return
        }
        val sortedMetaInfos = getSortedCodeMetaInfos(codeMetaInfos).groupBy { it.start }
        val opened = Stack<CodeMetaInfo>()

        for ((i, c) in originalText.withIndex()) {
            processMetaInfosStartedAtOffset(i, sortedMetaInfos, opened, builder)
            builder.append(c)
        }
        val lastSymbolIsNewLine = builder.last() == '\n'
        if (lastSymbolIsNewLine) {
            builder.deleteCharAt(builder.length - 1)
        }
        processMetaInfosStartedAtOffset(originalText.length, sortedMetaInfos, opened, builder)
        if (lastSymbolIsNewLine) {
            builder.appendLine()
        }
    }

    private fun processMetaInfosStartedAtOffset(
        offset: Int,
        sortedMetaInfos: Map<Int, List<CodeMetaInfo>>,
        opened: Stack<CodeMetaInfo>,
        builder: StringBuilder
    ) {
        checkOpenedAndCloseStringIfNeeded(opened, offset, builder)
        val matchedCodeMetaInfos = sortedMetaInfos[offset] ?: emptyList()
        if (matchedCodeMetaInfos.isNotEmpty()) {
            openStartTag(builder)
            val iterator = matchedCodeMetaInfos.listIterator()
            var current: CodeMetaInfo? = iterator.next()

            while (current != null) {
                val next: CodeMetaInfo? = if (iterator.hasNext()) iterator.next() else null
                opened.push(current)
                builder.append(current.asString())
                when {
                    next == null ->
                        closeStartTag(builder)
                    next.end == current.end ->
                        builder.append(", ")
                    else ->
                        closeStartAndOpenNewTag(builder)
                }
                current = next
            }
        }
        // Here we need to handle meta infos which has start == end and close them immediately
        checkOpenedAndCloseStringIfNeeded(opened, offset, builder)
    }

    private val metaInfoComparator =
        compareBy<CodeMetaInfo> { it.start }
            .thenByDescending { it.end }
            .thenBy { it.tag }
            .thenBy { it.asString() }

    private fun getSortedCodeMetaInfos(metaInfos: Collection<CodeMetaInfo>): List<CodeMetaInfo> {
        return metaInfos.sortedWith(metaInfoComparator)
    }

    private fun closeString(result: StringBuilder) = result.append("<!>")
    private fun openStartTag(result: StringBuilder) = result.append("<!")
    private fun closeStartTag(result: StringBuilder) = result.append("!>")
    private fun closeStartAndOpenNewTag(result: StringBuilder) = result.append("!><!")

    private fun checkOpenedAndCloseStringIfNeeded(opened: Stack<CodeMetaInfo>, end: Int, result: StringBuilder) {
        var prev: CodeMetaInfo? = null
        while (!opened.isEmpty() && end == opened.peek().end) {
            if (prev == null || prev.start != opened.peek().start)
                closeString(result)
            prev = opened.pop()
        }
    }
}

fun clearFileFromDiagnosticMarkup(file: File) {
    val text = file.readText()
    val cleanText = clearTextFromDiagnosticMarkup(text)
    file.writeText(cleanText)
}

fun clearTextFromDiagnosticMarkup(text: String): String = text.replace(CodeMetaInfoParser.openingOrClosingRegex, "")