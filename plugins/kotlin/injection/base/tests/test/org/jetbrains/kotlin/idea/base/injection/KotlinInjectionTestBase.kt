// Copyright 2000-2023 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license.

package org.jetbrains.kotlin.idea.base.injection

import com.intellij.codeInsight.completion.CompletionType
import com.intellij.injected.editor.EditorWindow
import com.intellij.lang.html.HTMLLanguage
import com.intellij.lang.xml.XMLLanguage
import com.intellij.openapi.fileTypes.PlainTextLanguage
import org.intellij.lang.annotations.Language
import org.intellij.lang.regexp.RegExpLanguage
import org.intellij.plugins.intelliLang.Configuration
import org.intellij.plugins.intelliLang.inject.InjectLanguageAction
import org.intellij.plugins.intelliLang.inject.config.BaseInjection
import org.intellij.plugins.intelliLang.inject.config.InjectionPlace
import org.jetbrains.kotlin.idea.KotlinLanguage
import org.junit.internal.runners.JUnit38ClassRunner
import org.junit.runner.RunWith

@RunWith(JUnit38ClassRunner::class)
abstract class KotlinInjectionTestBase : AbstractInjectionTest() {

    fun testInjectionLanguageOnString() {
        doInjectLanguageOrReferenceTest(
            before = """
            fun bar(){ val code = "fun foo() { <caret> }" }
        """.trimIndent(),
            after = """
            import org.intellij.lang.annotations.Language

            fun bar(){ @Language("kotlin") val code = "fun foo() {  }" }
        """.trimIndent()
        )
    }

    fun testInjectionLanguageOnStringSplitDeclaration() {
        doInjectLanguageOrReferenceTest(
            before = """
            var code: String? = null
            fun bar(){ code = "fun foo() { <caret> }" }
        """.trimIndent(),
            after = """
            import org.intellij.lang.annotations.Language

            @Language("kotlin")
            var code: String? = null
            fun bar(){ code = "fun foo() {  }" }
        """.trimIndent()
        )
    }

    fun testInjectionLanguageOnLibraryDeclaration() {
        doInjectLanguageOrReferenceTest(
            before = """
            val s = buildString { 
                append("<caret>val x = 1")
            }
        """.trimIndent(),
            after = """
            val s = buildString { 
                // language="kotlin"
                append("val x = 1")
            }
        """.trimIndent()
        )
    }

    fun testInjectionOnJavaPredefinedMethodWithAnnotation() = doInjectionPresentTest(
        """
        val test1 = java.util.regex.Pattern.compile("<caret>pattern")
        """,
        RegExpLanguage.INSTANCE.id,
        unInjectShouldBePresent = false
    )

    fun testInjectionOnJavaCustomInjectionWithAnnotation() {
        val customInjection = BaseInjection("java")
        customInjection.injectedLanguageId = HTMLLanguage.INSTANCE.id
        val elementPattern = customInjection.compiler.createElementPattern(
            """psiParameter().ofMethod(2, psiMethod().withName("replace").withParameters("int", "int", "java.lang.String").definedInClass("java.lang.StringBuilder"))""",
            "HTML temp rule"
        )
        customInjection.setInjectionPlaces(InjectionPlace(elementPattern, true))

        try {
            Configuration.getInstance().replaceInjections(listOf(customInjection), listOf(), true)

            doInjectionPresentTest(
                """
                val stringBuilder = StringBuilder().replace(0, 0, "<caret><html></html>")
                """,
                HTMLLanguage.INSTANCE.id,
                unInjectShouldBePresent = false
            )
        } finally {
            Configuration.getInstance().replaceInjections(listOf(), listOf(customInjection), true)
        }
    }

    fun testInjectionWithCommentOnProperty() = doInjectionPresentTest(
        """
        //language=file-reference
        val test = "<caret>simple"
        """
    )

    fun testInjectionWithUsageOnReceiverWithRuntime() = doInjectionPresentTest(
        """
        val test = "<caret>some"
        fun foo() = test.toRegex()
        """,
        languageId = RegExpLanguage.INSTANCE.id, unInjectShouldBePresent = false
    )

    fun testInjectionWithUsageInParameterWithRuntime() = doInjectionPresentTest(
        """
        val test = "<caret>some"
        fun foo() = Regex(test)
        """,
        languageId = RegExpLanguage.INSTANCE.id, unInjectShouldBePresent = false
    )

    fun testNoInjectionThoughSeveralAssignmentsWithRuntime() = assertNoInjection(
        """
        val first = "<caret>some"
        val test = first
        fun foo() = Regex(test)
        """
    )

    fun testInjectionWithMultipleCommentsOnFun() = doInjectionPresentTest(
        """
        // Some comment
        // Other comment
        //language=file-reference
        fun test() = "<caret>simple"
        """
    )

    fun testInjectionWithAnnotationOnPropertyWithAnnotation() = doInjectionPresentTest(
        """
        @org.intellij.lang.annotations.Language("file-reference")
        val test = "<caret>simple"
        """
    )

    fun testInjectWithCommentOnProperty() = doFileReferenceInjectTest(
        """
        val test = "<caret>simple"
        """,
        """
        import org.intellij.lang.annotations.Language
        
        @Language("file-reference")
        val test = "simple"
        """
    )

    fun testInjectWithCommentOnCommentedProperty() = doFileReferenceInjectTest(
        """
        // Hello
        val test = "<caret>simple"
        """,
        """
        import org.intellij.lang.annotations.Language
        
        // Hello
        @Language("file-reference")
        val test = "simple"
        """
    )

    fun testInjectWithCommentOnPropertyWithKDoc() = doFileReferenceInjectTest(
        """
        /**
         * Hi
         */
        val test = "<caret>simple"
        """,
        """
        import org.intellij.lang.annotations.Language
        
        /**
         * Hi
         */
        @Language("file-reference")
        val test = "<caret>simple"
        """
    )

    fun testInjectWithCommentOnExpression() = doFileReferenceInjectTest(
        """
        fun test() {
            "<caret>"
        }
        """,
        """
        fun test() {
            // language="file-reference"
            "<caret>"
        }
        """
    )

    fun testInjectWithCommentOnDeepExpression() = doFileReferenceInjectTest(
        """
        fun test() {
            "" + "<caret>"
        }
        """,
        """
        fun test() {
            "" + "<caret>"
        }
        """
    )

    fun testInjectOnPropertyWithAnnotation() = doFileReferenceInjectTest(
        """
        val test = "<caret>simple"
        """,
        """
        import org.intellij.lang.annotations.Language
        
        @Language("file-reference")
        val test = "simple"
        """
    )

    fun testInjectWithOnExpressionWithAnnotation() = doFileReferenceInjectTest(
        """
        fun test() {
            "<caret>"
        }
        """,
        """
        fun test() {
            // language="file-reference"
            "<caret>"
        }
        """
    )

    // TODO: add test for non-default language annotation

    fun testRemoveInjectionWithAnnotation() = doRemoveInjectionTest(
        """
        import org.intellij.lang.annotations.Language
        
        @Language("file-reference")
        val test = "<caret>simple"
        """,
        """
        import org.intellij.lang.annotations.Language
        
        val test = "simple"
        """
    )

    fun testRemoveInjectionWithComment() = doRemoveInjectionTest(
        """
        //language=file-reference
        val test = "<caret>simple"
        """,
        """
        val test = "simple"
        """
    )

    fun testRemoveInjectionWithCommentNotFirst() = doRemoveInjectionTest(
        """
        // Some comment. To do a language injection, add a line comment language=some instruction.
        //   language=file-reference
        val test = "<caret>simple"
        """,
        """
        // Some comment. To do a language injection, add a line comment language=some instruction.
        val test = "simple"
        """
    )

    fun testRemoveInjectionWithCommentAfterKDoc() = doRemoveInjectionTest(
        """
        /**Property*/
        // language=file-reference
        val test = "<caret>simple"
        """,
        """
        /**Property*/
        val test = "simple"
        """
    )

    fun testRemoveInjectionWithCommentInExpression() = doRemoveInjectionTest(
        """
        fun test() {
            // This is my favorite part
            // language=RegExp
            "<caret>something"
        }
        """,
        """
        fun test() {
            // This is my favorite part
            "something"
        }
        """
    )

    fun testInjectionWithUsageInFunctionWithMarkedParameterWithAnnotation() = doInjectionPresentTest(
        """
        import org.intellij.lang.annotations.Language

        val v = "<caret>some"

        fun foo(@Language("HTML") s: String) {}

        fun other() { foo(v) }
        """,
        languageId = HTMLLanguage.INSTANCE.id, unInjectShouldBePresent = false
    )

    fun testInjectionOfCustomParameterWithAnnotation() = doInjectionPresentTest(
        """
        import org.intellij.lang.annotations.Language

        fun foo(@Language("HTML") s: String) {}

        fun other() { foo("<caret>some") }
        """,
        languageId = HTMLLanguage.INSTANCE.id, unInjectShouldBePresent = false
    )

    fun testInjectionOfCustomParameterInConstructorWithAnnotation() = doInjectionPresentTest(
        """
        import org.intellij.lang.annotations.Language

        class Test(@Language("HTML") val s: String)

        fun other() { Test("<caret>some") }
        """,
        languageId = HTMLLanguage.INSTANCE.id, unInjectShouldBePresent = false
    )

    fun testInjectionOfCustomParameterDefaultCallWithAnnotation() = doInjectionPresentTest(
        """
        import org.intellij.lang.annotations.Language

        fun foo(@Language("HTML") s: String) {}

        fun other() { foo(s = "<caret>some") }
        """,
        languageId = HTMLLanguage.INSTANCE.id, unInjectShouldBePresent = false
    )

    fun testInjectionOfCustomParameterInJavaConstructorWithAnnotationWithAnnotation() = doInjectionPresentTest(
        """
        fun bar() { Test("<caret>some") }
        """,
        javaText =
        """
        import org.intellij.lang.annotations.Language;

        public class Test {
            public Test(@Language("HTML") String str) {}
        }
        """,
        languageId = HTMLLanguage.INSTANCE.id, unInjectShouldBePresent = false
    )

    fun testInjectionOffCustomParameterInEnumConstructorWithAnnotation() = doInjectionPresentTest(
        """
        import org.intellij.lang.annotations.Language

        enum class Test(@Language("HTML") val s: String){
          ONE("<caret>some")
        }
        """,
        languageId = HTMLLanguage.INSTANCE.id, unInjectShouldBePresent = false
    )

    fun testInjectionOfCustomParameterJavaWithAnnotation() = doInjectionPresentTest(
        """
        fun bar() { Test.foo("<caret>some") }
        """,
        javaText =
        """
        import org.intellij.lang.annotations.Language;

        public class Test {
            public static void foo(@Language("HTML") String str) {}
        }
        """,
        languageId = HTMLLanguage.INSTANCE.id, unInjectShouldBePresent = false
    )

    fun testInjectionOnInterpolationWithAnnotation() = doInjectionPresentTest(
        """
        val b = 2

        @org.intellij.lang.annotations.Language("HTML")
        val test = "<caret>simple${'$'}{b}.kt"
        """,
        unInjectShouldBePresent = false,
        shreds = listOf(
            ShredInfo(range(0, 6), hostRange = range(1, 7)),
            ShredInfo(range(6, 21), hostRange = range(11, 14), prefix = "missingValue")
        ),
        injectedText = "simplemissingValue.kt"
    )

    fun testInjectionOnInterpolatedStringWithComment() = doInjectionPresentTest(
        """
        val some = 42
        // language=HTML
        val test = "<ht<caret>ml>${'$'}some</html>"
        """,
        languageId = HTMLLanguage.INSTANCE.id, unInjectShouldBePresent = false,
        shreds = listOf(
            ShredInfo(range(0, 6), hostRange = range(1, 7)),
            ShredInfo(range(6, 17), hostRange = range(12, 19), prefix = "some")
        ),
        injectedText = "<html>some</html>"
    )

    fun testEditorShortShreadsInInterpolatedInjection() = doInjectionPresentTest(
        """
        val s = 42
        // language=TEXT
        val test = "${'$'}s <caret>text ${'$'}s${'$'}{s}${'$'}s text ${'$'}s"
        """,
        languageId = PlainTextLanguage.INSTANCE.id, unInjectShouldBePresent = false,
        shreds = listOf(
            ShredInfo(range(0, 0), hostRange = range(1, 1)),
            ShredInfo(range(0, 7), hostRange = range(3, 9), prefix = "s"),
            ShredInfo(range(7, 8), hostRange = range(11, 11), prefix = "s"),
            ShredInfo(range(8, 20), hostRange = range(15, 15), prefix = "missingValue"),
            ShredInfo(range(20, 27), hostRange = range(17, 23), prefix = "s"),
            ShredInfo(range(27, 28), hostRange = range(25, 25), prefix = "s")
        ),
        injectedText = "s text smissingValues text s"
    )

    fun testEditorShortShreadsInInterpolatedInjectionWithEscapes() = doInjectionPresentTest(
        """
        const val s = "text1"
        // language=TEXT
        val test = "${'$'}s <caret>text"
        """,
        languageId = PlainTextLanguage.INSTANCE.id, unInjectShouldBePresent = false,
        shreds = listOf(
            ShredInfo(range(0, 0), hostRange = range(1, 1)),
            ShredInfo(range(0, 10), hostRange = range(3, 8), prefix = "text1")
        ),
        injectedText = "text1 text"
    )

    fun testEditorLongShreadsInInterpolatedInjection() = doInjectionPresentTest(
        """
        val s = 42
        // language=TEXT
        val test = "${'$'}{s} <caret>text ${'$'}{s}${'$'}s${'$'}{s} text ${'$'}{s}"
        """,
        languageId = PlainTextLanguage.INSTANCE.id, unInjectShouldBePresent = false,
        shreds = listOf(
            ShredInfo(range(0, 0), hostRange = range(1, 1)),
            ShredInfo(range(0, 18), hostRange = range(5, 11), prefix = "missingValue"),
            ShredInfo(range(18, 30), hostRange = range(15, 15), prefix = "missingValue"),
            ShredInfo(range(30, 31), hostRange = range(17, 17), prefix = "s"),
            ShredInfo(range(31, 49), hostRange = range(21, 27), prefix = "missingValue"),
            ShredInfo(range(49, 61), hostRange = range(31, 31), prefix = "missingValue")
        ),
        injectedText = "missingValue text missingValuesmissingValue text missingValue"
    )

    fun testEditorShreadsWithEscapingInjection() = doInjectionPresentTest(
        """
        // language=TEXT
        val test = "\rte<caret>xt\ttext\n\t"
        """,
        languageId = PlainTextLanguage.INSTANCE.id, unInjectShouldBePresent = false,
        shreds = listOf(
            ShredInfo(range(0, 12), hostRange = range(1, 17))
        )
    )

    fun testEditorShreadsInInterpolatedWithEscapingInjection() = doInjectionPresentTest(
        """
        val s = 1
        // language=TEXT
        val test = "\r${'$'}s te<caret>xt${'$'}s\ttext\n\t"
        """,
        languageId = PlainTextLanguage.INSTANCE.id, unInjectShouldBePresent = false,
        shreds = listOf(
            ShredInfo(range(0, 1), hostRange = range(1, 3)),
            ShredInfo(range(1, 7), hostRange = range(5, 10), prefix = "s"),
            ShredInfo(range(7, 15), hostRange = range(12, 22), prefix = "s")
        )
    )

    fun testSuffixPrefixWithAnnotation() = doInjectionPresentTest(
        """
        @org.intellij.lang.annotations.Language("TEXT", prefix = "abc", suffix = "ghi")
        val test = "<caret>def"
        """,
        languageId = PlainTextLanguage.INSTANCE.id, unInjectShouldBePresent = false,
        shreds = listOf(ShredInfo(range(0, 9), hostRange = range(1, 4), prefix = "abc", suffix = "ghi"))
    )

    fun testSuffixPrefixWithCallWithAnnotation() = doInjectionPresentTest(
        """
        fun highlight(@org.intellij.lang.annotations.Language("TEXT", prefix = "fun __f(it: dynamic) = ", suffix = ";") code: String) {}

        fun test() {
            highlight("<caret>it > 0")
        }
    """,
        languageId = PlainTextLanguage.INSTANCE.id, unInjectShouldBePresent = false,
        shreds = listOf(ShredInfo(range(0, 30), hostRange = range(1, 7), prefix = "fun __f(it: dynamic) = ", suffix = ";"))
    )

    fun testSuffixPrefixInComment() = doInjectionPresentTest(
        """
        // language="TEXT" prefix="abc" suffix=ghi
        val test = "<caret>def"
        """,
        languageId = PlainTextLanguage.INSTANCE.id, unInjectShouldBePresent = false,
        shreds = listOf(ShredInfo(range(0, 9), hostRange = range(1, 4), prefix = "abc", suffix = "ghi"))
    )

    fun testSuffixAfterInterpolationInMultiline() = doInjectionPresentTest(
        """
    //language=TEXT suffix="check"
    val some = 1
    val text = ""${'"'}
      <caret>${'$'}{some}
      ""${'"'} // Position is important! Shouldn't be start of the line.
    """,
        languageId = PlainTextLanguage.INSTANCE.id, unInjectShouldBePresent = false,
        shreds = listOf(
            ShredInfo(range(0, 3), hostRange = range(3, 6), prefix = "", suffix = ""),
            ShredInfo(range(3, 23), hostRange = range(13, 16), prefix = "missingValue", suffix = "check")
        )
    )

    fun testMutation() = doInjectionPresentTest(
        """
        @org.intellij.lang.annotations.Language("HTML")
        var html = ""

        fun test() {
          html = "<ht<caret>ml></html>"
        }
        """,
        languageId = HTMLLanguage.INSTANCE.id, unInjectShouldBePresent = false,
        injectedText = "<html></html>"
    )

    fun testMutationNested() = doInjectionPresentTest(
        """
        class A { val b: B = B() }
        class B {
            @org.intellij.lang.annotations.Language("HTML")
            var html = ""
        }
        fun test() {
            val a = A()
            a.b.html = "<ht<caret>ml></html>"
        }
        """,
        languageId = HTMLLanguage.INSTANCE.id, unInjectShouldBePresent = false,
        injectedText = "<html></html>"
    )

    fun testMutationAbstract() = doInjectionPresentTest(
        """
        abstract class A {
            @org.intellij.lang.annotations.Language("HTML")
            var html: String
        }
        fun A.test() {
          html = "<ht<caret>ml></html>"
        }
        """,
        languageId = HTMLLanguage.INSTANCE.id, unInjectShouldBePresent = false,
        injectedText = "<html></html>"
    )

    fun testPlusEqMutation() = doInjectionPresentTest(
        """
        @org.intellij.lang.annotations.Language("TEXT")
        var text = "hello"

        fun test() {
          text += "wor<caret>ld"
        }
        """,
        languageId = PlainTextLanguage.INSTANCE.id, unInjectShouldBePresent = false,
        injectedText = "world"
    )

    fun testJavaAnnotationsPattern() {
        myFixture.addClass(
            """
            @interface Matches { String value(); }
            """
        )

        doAnnotationInjectionTest(
            injectedLanguage = RegExpLanguage.INSTANCE.id,
            pattern = """psiMethod().withName("value").withParameters().definedInClass("Matches")""",
            kotlinCode = """
                                    @Matches("[A-Z]<caret>[a-z]+")
                                    val name = "John"
                                """
        )
    }

    fun testKotlinAnnotationsPattern() {
        doAnnotationInjectionTest(
            patternLanguage = "kotlin",
            injectedLanguage = RegExpLanguage.INSTANCE.id,
            pattern = """kotlinParameter().ofFunction(0, kotlinFunction().withName("Matches").definedInClass("Matches"))""",
            kotlinCode = """
                    annotation class Matches(val pattern: String)

                    @Matches("[A-Z]<caret>[a-z]+")
                    val name = "John"
                                """
        )
    }

    fun testKotlinNestedAnnotationsPattern() {
        doAnnotationInjectionTest(
            patternLanguage = "kotlin",
            injectedLanguage = RegExpLanguage.INSTANCE.id,
            pattern = """kotlinParameter().ofFunction(0, kotlinFunction().withName("Matches").definedInClass("Matches"))""",
            kotlinCode = """
                    annotation class Matches(val pattern: String)
                    annotation class ManyMatches(val patterns: Array<Matches>)

                    @ManyMatches(patterns = [Matches("[A-Z]<caret>[a-z]+")])
                    val name = "John"
                                """
        )
    }

    fun testKotlinAnnotationsPatternNamed() {
        doAnnotationInjectionTest(
            patternLanguage = "kotlin",
            injectedLanguage = RegExpLanguage.INSTANCE.id,
            pattern = """kotlinParameter().ofFunction(0, kotlinFunction().withName("Matches").definedInClass("Matches"))""",
            kotlinCode = """
                    annotation class Matches(val pattern: String)

                    @Matches(pattern = "[A-Z]<caret>[a-z]+")
                    val name = "John"
                                """
        )
    }

    fun testKotlinAnnotationsPatternNamedArrayLiteral() {
        doAnnotationInjectionTest(
            patternLanguage = "kotlin",
            injectedLanguage = RegExpLanguage.INSTANCE.id,
            pattern = """kotlinParameter().ofFunction(0, kotlinFunction().withName("Matches").definedInClass("Matches"))""",
            kotlinCode = """
                    annotation class Matches(val pattern: Array<String>)

                    @Matches(pattern = ["[A-Z]<caret>[a-z]+"])
                    val name = "John"
                                """
        )
    }

    fun testKotlinAnnotationsPatternNamedArray() {
        doAnnotationInjectionTest(
            patternLanguage = "kotlin",
            injectedLanguage = RegExpLanguage.INSTANCE.id,
            pattern = """kotlinParameter().ofFunction(0, kotlinFunction().withName("Matches").definedInClass("Matches"))""",
            kotlinCode = """
                    annotation class Matches(val pattern: Array<String>)

                    @Matches(pattern = arrayOf("[A-Z]<caret>[a-z]+"))
                    val name = "John"
                                """
        )
    }

    fun testInjectionInJavaAnnotation() {

        myFixture.addClass(
            """
            @interface InHtml {
                String value();
            }
            """
        )

        doAnnotationInjectionTest(
            injectedLanguage = HTMLLanguage.INSTANCE.id,
            pattern = """psiMethod().withName("value").withParameters().definedInClass("InHtml")""",
            kotlinCode = """
                                @InHtml("<htm<caret>l></html>")
                                fun foo() {
                                }
                                """,
            additionalAsserts = { assertSameElements(myFixture.complete(CompletionType.BASIC).flatMap { it.allLookupStrings }, "html") }
        )

    }

    fun testInjectionInJavaAnnotationWithNamedParam() {
        myFixture.addClass(
            """
                        package myinjection;

                        @interface InHtml {
                        String html();
                        }
                        """
        )
        doAnnotationInjectionTest(
            injectedLanguage = HTMLLanguage.INSTANCE.id,
            pattern = """psiMethod().withName("html").withParameters().definedInClass("myinjection.InHtml")""",
            kotlinCode = """
                                        import myinjection.InHtml

                                        @InHtml(html = "<htm<caret>l></html>")
                                        fun foo() {
                                        }
                                        """
        )
    }

    fun testInjectionInJavaAnnotationWithNamedParamArray() {
        myFixture.addClass(
            """
                        package myinjection;

                        @interface InHtml {
                        String[] htmls();
                        }
                        """
        )
        doAnnotationInjectionTest(
            injectedLanguage = HTMLLanguage.INSTANCE.id,
            pattern = """psiMethod().withName("htmls").withParameters().definedInClass("myinjection.InHtml")""",
            kotlinCode = """
                                        import myinjection.InHtml

                                        @InHtml(htmls = arrayOf("<br/>", "<htm<caret>l></html>"))
                                        fun foo() {
                                        }
                                        """
        )
    }

    fun testInjectionInJavaAnnotationWithNamedParamLiterals() {
        myFixture.addClass(
            """
                        package myinjection;

                        @interface InHtml {
                        String[] htmls();
                        }
                        """
        )
        doAnnotationInjectionTest(
            injectedLanguage = HTMLLanguage.INSTANCE.id,
            pattern = """psiMethod().withName("htmls").withParameters().definedInClass("myinjection.InHtml")""",
            kotlinCode = """
                                        import myinjection.InHtml

                                        @InHtml(htmls = ["<br/>", "<htm<caret>l></html>"])
                                        fun foo() {
                                        }
                                        """
        )
    }

    fun testInjectionInJavaNestedAnnotation() {
        myFixture.addClass(
            """
                        package myinjection;

                        public @interface InHtml {
                        String html();
                        }
                        """
        )
        myFixture.addClass(
            """
                        package myinjection;

                        public @interface InHtmls {
                        InHtml[] htmls();
                        }
                        """
        )
        doAnnotationInjectionTest(
            injectedLanguage = HTMLLanguage.INSTANCE.id,
            pattern = """psiMethod().withName("html").withParameters().definedInClass("myinjection.InHtml")""",
            kotlinCode = """
                                        import myinjection.InHtml
                                        import myinjection.InHtmls

                                        @InHtmls(htmls = [InHtml(html = "<htm<caret>l></html>")])
                                        fun foo() {
                                        }
                                        """
        )
    }

    fun testInjectionInAliasedJavaAnnotation() {
        myFixture.addClass(
            """
                            @interface InHtml {
                            String html();
                            }
                            """
        )
        doAnnotationInjectionTest(
            injectedLanguage = HTMLLanguage.INSTANCE.id,
            pattern = """psiMethod().withName("html").withParameters().definedInClass("InHtml")""",
            kotlinCode = """
                                            import InHtml as InHtmlAliased

                                            @InHtmlAliased(html = "<htm<caret>l></html>")
                                            fun foo() {
                                            }
                                            """
        )
    }

    fun testInjectionOnReturnResultWithAnnotation() = doInjectionPresentTest(
        """
        import org.intellij.lang.annotations.Language

        @Language("HTML")
        fun htmlProvider(): String {
            return "<ht<caret>ml></html>"
        }
        """,
        languageId = HTMLLanguage.INSTANCE.id, unInjectShouldBePresent = false
    )

    fun testInjectionOnReturnResultWithElvisWithAnnotation() = doInjectionPresentTest(
        """
        import org.intellij.lang.annotations.Language

        @Language("HTML")
        fun htmlProvider(arg: String?): String {
            return arg ?: "<ht<caret>ml></html>"
        }
        """,
        languageId = HTMLLanguage.INSTANCE.id, unInjectShouldBePresent = false
    )

    fun testInjectionOnReturnResultWithIfWithAnnotation() = doInjectionPresentTest(
        """
        import org.intellij.lang.annotations.Language

        @Language("HTML")
        fun htmlProvider(arg: String?): String {
            return if (arg == null) "<div>" else "<ht<caret>ml></html>"
        }
        """,
        languageId = HTMLLanguage.INSTANCE.id, unInjectShouldBePresent = false
    )

    fun testInjectionExpressionBodyWithAnnotation() = doInjectionPresentTest(
        """
        import org.intellij.lang.annotations.Language

        @Language("HTML")
        fun htmlProvider() = "<ht<caret>ml></html>"
        """,
        languageId = HTMLLanguage.INSTANCE.id, unInjectShouldBePresent = false
    )

    fun testInjectionExpressionBodyElvisWithAnnotation() = doInjectionPresentTest(
        """
        import org.intellij.lang.annotations.Language

        @Language("HTML")
        fun htmlProvider(arg: String?) = arg ?: "<ht<caret>ml></html>"
        """,
        languageId = HTMLLanguage.INSTANCE.id, unInjectShouldBePresent = false
    )

    fun testInjectionWithUsageInFunctionWithMarkedSecondNamedParameterWithAnnotation() = doInjectionPresentTest(
        """
        import org.intellij.lang.annotations.Language

        fun foo(@Language("XML") a: String = "", @Language("HTML") b: String = "") {}

        fun other() { foo(b = "some<caret>") }
        """,
        languageId = HTMLLanguage.INSTANCE.id, unInjectShouldBePresent = false
    )

    fun testInjectionWithUsageInFunctionWithMarkedFirstNamedParameterWithAnnotation() = doInjectionPresentTest(
        """
        import org.intellij.lang.annotations.Language

        fun foo(@Language("XML") a: String = "", @Language("HTML") b: String = "") {}

        fun other() { foo(a = "some<caret>") }
        """,
        languageId = XMLLanguage.INSTANCE.id, unInjectShouldBePresent = false
    )

    fun testKotlinReplaceWithBuildInsInjection() {
        myFixture.configureByText(
            "a.kt",
            """
            @kotlin.Deprecated("it's outdated", kotlin.ReplaceWith("do<caret>Fun(42)"))
            fun bar() {}
            """.trimIndent()
        )

        assertFalse(
            "Injection action is available. There's probably no injection at caret place",
            InjectLanguageAction().isAvailable(project, myFixture.editor, myFixture.file)
        )

        val injectedFile = (editor as? EditorWindow)?.injectedFile
        assertEquals("Wrong injection language", KotlinLanguage.INSTANCE, injectedFile?.language)
    }

    protected fun doAnnotationInjectionTest(
        patternLanguage: String = "java",
        injectedLanguage: String,
        pattern: String,
        @Language("kotlin") kotlinCode: String,
        additionalAsserts: () -> Unit = {}
    ) {
        val customInjection = BaseInjection(patternLanguage)
        customInjection.injectedLanguageId = injectedLanguage
        val elementPattern = customInjection.compiler.createElementPattern(
            pattern,
            "temp rule"
        )
        customInjection.setInjectionPlaces(InjectionPlace(elementPattern, true))
        try {
            Configuration.getInstance().replaceInjections(listOf(customInjection), listOf(), true)

            doInjectionPresentTest(
                kotlinCode, null,
                injectedLanguage,
                unInjectShouldBePresent = false
            )
            additionalAsserts()
        } finally {
            Configuration.getInstance().replaceInjections(listOf(), listOf(customInjection), true)
        }
    }
}

