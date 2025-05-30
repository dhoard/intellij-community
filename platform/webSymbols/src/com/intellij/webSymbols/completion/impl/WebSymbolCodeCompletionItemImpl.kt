// Copyright 2000-2022 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license.
package com.intellij.webSymbols.completion.impl

import com.intellij.codeInsight.completion.*
import com.intellij.codeInsight.lookup.AutoCompletionPolicy
import com.intellij.codeInsight.lookup.LookupElement
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.codeInsight.lookup.LookupElementPresentation
import com.intellij.codeInsight.lookup.LookupElementRenderer
import com.intellij.psi.PsiElement
import com.intellij.webSymbols.PsiSourcedWebSymbol
import com.intellij.webSymbols.WebSymbol
import com.intellij.webSymbols.WebSymbolApiStatus
import com.intellij.webSymbols.WebSymbolApiStatus.Companion.isDeprecatedOrObsolete
import com.intellij.webSymbols.completion.WebSymbolCodeCompletionItem
import com.intellij.webSymbols.completion.WebSymbolCodeCompletionItemBuilder
import com.intellij.webSymbols.completion.WebSymbolCodeCompletionItemInsertHandler
import com.intellij.webSymbols.impl.scaleToHeight
import com.intellij.webSymbols.query.WebSymbolDefaultIconProvider
import org.jetbrains.annotations.ApiStatus
import javax.swing.Icon

internal data class WebSymbolCodeCompletionItemImpl(
  override val name: String,
  override val offset: Int = 0,
  override val completeAfterInsert: Boolean = false,
  override val completeAfterChars: Set<Char> = emptySet(),
  override val displayName: String? = null,
  override val symbol: WebSymbol? = null,
  override val priority: WebSymbol.Priority? = null,
  override val proximity: Int? = null,
  override val apiStatus: WebSymbolApiStatus = WebSymbolApiStatus.Stable,
  override val aliases: Set<String> = emptySet(),
  override val icon: Icon? = null,
  val typeTextStatic: String? = null,
  val typeTextProvider: (() -> String?)? = null,
  override val tailText: String? = null,
  override val insertHandler: WebSymbolCodeCompletionItemInsertHandler? = null,
  @get:ApiStatus.Internal
  val stopSequencePatternEvaluation: Boolean = false,
) : WebSymbolCodeCompletionItem {

  override val typeText: String? get() = typeTextProvider?.invoke() ?: typeTextStatic

  override fun addToResult(
    parameters: CompletionParameters,
    result: CompletionResultSet,
    baselinePriorityValue: Double,
  ) {
    val completionPrefixMatcher = result.prefixMatcher
    val completionPrefix = completionPrefixMatcher.prefix
    if (completionPrefix.length < offset ||
        completionPrefix.substring(offset) == name)
      return
    val priorityOffset = baselinePriorityValue - WebSymbol.Priority.NORMAL.value
    val deprecatedOrObsolete = apiStatus.isDeprecatedOrObsolete()
    LookupElementBuilder
      .create(wrapSymbolForDocumentation(symbol, parameters.position)?.createPointer() ?: name, name)
      .withLookupStrings(aliases)
      .withIcon(icon?.scaleToHeight(16))
      .withTypeText(typeTextStatic, true)
      .let {
        if (typeTextProvider != null)
          it.withExpensiveRenderer(object : LookupElementRenderer<LookupElement>() {
            override fun renderElement(element: LookupElement, presentation: LookupElementPresentation) {
              element.renderElement(presentation)
              typeTextProvider()?.let { presentation.typeText = it }
            }
          })
        else it
      }
      .withTailText(tailText, true)
      .withBoldness(!deprecatedOrObsolete && priority == WebSymbol.Priority.HIGHEST)
      .withStrikeoutness(deprecatedOrObsolete)
      .let {
        if (displayName != null)
          it.withPresentableText(displayName)
        else it
      }
      .withInsertHandler { insertionContext, completionItem ->
        val invokeCompletion = completeAfterInsert || completeAfterChars.contains(insertionContext.completionChar)
        insertHandler?.prepare(insertionContext, completionItem, invokeCompletion)?.run()
        if (invokeCompletion) {
          insertionContext.setLaterRunnable {
            CodeCompletionHandlerBase(CompletionType.BASIC)
              .invokeCompletion(parameters.originalFile.project, parameters.editor)
          }
        }
      }.let {
        if (completeAfterChars.isNotEmpty())
          it.withAutoCompletionPolicy(AutoCompletionPolicy.NEVER_AUTOCOMPLETE)
        else it
      }.let {
        val priorityValue = if (deprecatedOrObsolete) WebSymbol.Priority.LOWEST.value
        else (priority ?: WebSymbol.Priority.NORMAL).value + priorityOffset
        PrioritizedLookupElement.withPriority(it, priorityValue)
      }.let {
        PrioritizedLookupElement.withExplicitProximity(it, proximity ?: 0)
      }.let {
        (if (offset > 0) result.withPrefixMatcher(completionPrefixMatcher.cloneWithPrefix(completionPrefix.substring(offset)))
        else result).addElement(it)
      }
  }

  private fun wrapSymbolForDocumentation(symbol: WebSymbol?, location: PsiElement) =
    when (symbol) {
      is PsiSourcedWebSymbol -> symbol.getDocumentationTarget(location)?.let {
        PsiSourcedCodeCompletionWebSymbolWithDocumentation(symbol, it)
      }
      is WebSymbol -> symbol.getDocumentationTarget(location)?.let {
        CodeCompletionWebSymbolWithDocumentation(symbol, it)
      }
      else -> null
    }

  override fun withName(name: String): WebSymbolCodeCompletionItem =
    copy(name = name)

  override fun withPrefix(prefix: String): WebSymbolCodeCompletionItem =
    if (prefix.isEmpty())
      this
    else
      copy(
        name = prefix + name,
        offset = offset - prefix.length,
        displayName = if (displayName != null) prefix + displayName else null,
        aliases = aliases.asSequence().map { prefix + it }.toSet()
      )

  override fun withOffset(offset: Int): WebSymbolCodeCompletionItem =
    copy(offset = offset)

  override fun withCompleteAfterInsert(completeAfterInsert: Boolean): WebSymbolCodeCompletionItem =
    copy(completeAfterInsert = completeAfterInsert)

  override fun withDisplayName(displayName: String?): WebSymbolCodeCompletionItem =
    copy(displayName = displayName)

  override fun withSymbol(symbol: WebSymbol?): WebSymbolCodeCompletionItem =
    copy(symbol = symbol)

  override fun withPriority(priority: WebSymbol.Priority?): WebSymbolCodeCompletionItem =
    copy(priority = priority)

  override fun withProximity(proximity: Int): WebSymbolCodeCompletionItem =
    copy(proximity = proximity)

  override fun withApiStatus(apiStatus: WebSymbolApiStatus): WebSymbolCodeCompletionItem =
    copy(apiStatus = apiStatus)

  override fun withAliasesReplaced(aliases: Set<String>): WebSymbolCodeCompletionItem =
    copy(aliases = aliases)

  override fun withAliasesAdded(aliases: Set<String>): WebSymbolCodeCompletionItem =
    copy(aliases = this.aliases + aliases)

  override fun withAliasAdded(alias: String): WebSymbolCodeCompletionItem =
    copy(aliases = this.aliases + alias)

  override fun withIcon(icon: Icon?): WebSymbolCodeCompletionItem =
    copy(icon = icon)

  override fun withTypeText(typeText: String?): WebSymbolCodeCompletionItem =
    copy(typeTextStatic = typeText)

  override fun withTypeText(typeTextProvider: () -> String?): WebSymbolCodeCompletionItem =
    copy(typeTextProvider = typeTextProvider)

  override fun withTailText(tailText: String?): WebSymbolCodeCompletionItem =
    copy(tailText = tailText)

  override fun withCompleteAfterChar(char: Char): WebSymbolCodeCompletionItem =
    copy(completeAfterChars = if (!completeAfterInsert) completeAfterChars + char else emptySet())

  override fun withCompleteAfterCharsAdded(chars: List<Char>): WebSymbolCodeCompletionItem =
    copy(completeAfterChars = if (!completeAfterInsert) completeAfterChars + chars else emptySet())

  override fun withInsertHandlerReplaced(insertHandler: WebSymbolCodeCompletionItemInsertHandler?): WebSymbolCodeCompletionItem =
    copy(insertHandler = insertHandler)

  override fun withInsertHandlerAdded(
    insertHandler: InsertHandler<LookupElement>,
    priority: WebSymbol.Priority,
  ): WebSymbolCodeCompletionItem =
    withInsertHandlerAdded(WebSymbolCodeCompletionItemInsertHandler.adapt(insertHandler, priority))

  override fun withInsertHandlerAdded(insertHandler: WebSymbolCodeCompletionItemInsertHandler): WebSymbolCodeCompletionItem =
    copy(insertHandler = CompoundInsertHandler.merge(this.insertHandler, insertHandler))

  override fun with(
    name: String,
    offset: Int,
    completeAfterInsert: Boolean,
    completeAfterChars: Set<Char>,
    displayName: String?,
    symbol: WebSymbol?,
    priority: WebSymbol.Priority?,
    proximity: Int?,
    apiStatus: WebSymbolApiStatus,
    icon: Icon?,
    typeText: String?,
    tailText: String?,
  ): WebSymbolCodeCompletionItem =
    copy(name = name, offset = offset, completeAfterInsert = completeAfterInsert,
         completeAfterChars = if (!completeAfterInsert) completeAfterChars else emptySet(),
         displayName = displayName, symbol = symbol, priority = priority, proximity = proximity,
         apiStatus = apiStatus, icon = icon, typeTextStatic = typeText ?: typeTextStatic,
         tailText = tailText)

  class BuilderImpl(
    private var name: String,
    private var offset: Int = 0,
    private var symbol: WebSymbol? = null,
  ) : WebSymbolCodeCompletionItemBuilder {

    private var completeAfterInsert: Boolean = false
    private var completeAfterChars: Set<Char> = emptySet()
    private var displayName: String? = null
    private var priority: WebSymbol.Priority? = symbol?.priority
    private var proximity: Int? = symbol?.proximity
    private var apiStatus: WebSymbolApiStatus = symbol?.apiStatus ?: WebSymbolApiStatus.Stable
    private var aliases: Set<String> = emptySet()
    private var icon: Icon? = symbol?.let {
      it.icon
      ?: it.origin.defaultIcon
      ?: WebSymbolDefaultIconProvider.get(it.namespace, it.kind)
    }
    private var typeTextStatic: String? = null
    private var typeTextProvider: (() -> String?)? = null
    private var tailText: String? = null
    private var insertHandler: WebSymbolCodeCompletionItemInsertHandler? = null
    private var stopSequencePatternEvaluation: Boolean = false

    fun build(): WebSymbolCodeCompletionItem = WebSymbolCodeCompletionItemImpl(
      name, offset, completeAfterInsert, completeAfterChars, displayName, symbol, priority, proximity,
      apiStatus, aliases, icon, typeTextStatic, typeTextProvider, tailText, insertHandler, stopSequencePatternEvaluation)

    override fun displayName(value: String?): WebSymbolCodeCompletionItemBuilder {
      displayName = value
      return this
    }

    override fun offset(value: Int): WebSymbolCodeCompletionItemBuilder {
      offset = value
      return this
    }

    override fun icon(value: Icon?): WebSymbolCodeCompletionItemBuilder {
      icon = value
      return this
    }

    override fun typeText(value: String?): WebSymbolCodeCompletionItemBuilder {
      typeTextStatic = value
      return this
    }

    override fun typeText(provider: () -> String?): WebSymbolCodeCompletionItemBuilder {
      typeTextProvider = provider
      return this
    }

    override fun tailText(value: String?): WebSymbolCodeCompletionItemBuilder {
      tailText = value
      return this
    }

    override fun completeAfterInsert(value: Boolean): WebSymbolCodeCompletionItemBuilder {
      completeAfterInsert = value
      return this
    }

    override fun completeAfterChars(value: Set<Char>): WebSymbolCodeCompletionItemBuilder {
      completeAfterChars = value
      return this
    }

    override fun priority(value: WebSymbol.Priority?): WebSymbolCodeCompletionItemBuilder {
      priority = value
      return this
    }

    override fun proximity(value: Int?): WebSymbolCodeCompletionItemBuilder {
      proximity = value
      return this
    }

    override fun apiStatus(value: WebSymbolApiStatus): WebSymbolCodeCompletionItemBuilder {
      apiStatus = value
      return this
    }

    override fun aliases(value: Set<String>): WebSymbolCodeCompletionItemBuilder {
      aliases = value
      return this
    }

    override fun symbol(value: WebSymbol?): WebSymbolCodeCompletionItemBuilder {
      symbol = value
      return this
    }

    override fun insertHandler(value: WebSymbolCodeCompletionItemInsertHandler?): WebSymbolCodeCompletionItemBuilder {
      insertHandler = value
      return this
    }

  }

}