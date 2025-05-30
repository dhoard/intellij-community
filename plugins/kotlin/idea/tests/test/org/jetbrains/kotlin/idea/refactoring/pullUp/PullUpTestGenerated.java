// Copyright 2000-2025 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license.

package org.jetbrains.kotlin.idea.refactoring.pullUp;

import com.intellij.testFramework.TestDataPath;
import org.jetbrains.kotlin.idea.base.plugin.KotlinPluginMode;
import org.jetbrains.kotlin.idea.base.test.TestRoot;
import org.jetbrains.kotlin.idea.test.JUnit3RunnerWithInners;
import org.jetbrains.kotlin.idea.test.KotlinTestUtils;
import org.jetbrains.kotlin.test.TestMetadata;
import org.junit.runner.RunWith;

/**
 * This class is generated by {@link org.jetbrains.kotlin.testGenerator.generator.TestGenerator}.
 * DO NOT MODIFY MANUALLY.
 */
@SuppressWarnings("all")
@TestRoot("idea/tests")
@TestDataPath("$CONTENT_ROOT")
@RunWith(JUnit3RunnerWithInners.class)
public abstract class PullUpTestGenerated extends AbstractPullUpTest {
    @RunWith(JUnit3RunnerWithInners.class)
    @TestMetadata("testData/refactoring/pullUp/k2k")
    public static class K2K extends AbstractPullUpTest {
        @java.lang.Override
        @org.jetbrains.annotations.NotNull
        public final KotlinPluginMode getPluginMode() {
            return KotlinPluginMode.K1;
        }

        private void runTest(String testDataFilePath) throws Exception {
            KotlinTestUtils.runTest(this::doKotlinTest, this, testDataFilePath);
        }

        @TestMetadata("abstractFromInterfaceToInterface.kt")
        public void testAbstractFromInterfaceToInterface() throws Exception {
            runTest("testData/refactoring/pullUp/k2k/abstractFromInterfaceToInterface.kt");
        }

        @TestMetadata("accidentalOverrides.kt")
        public void testAccidentalOverrides() throws Exception {
            runTest("testData/refactoring/pullUp/k2k/accidentalOverrides.kt");
        }

        @TestMetadata("clashWithSuper.kt")
        public void testClashWithSuper() throws Exception {
            runTest("testData/refactoring/pullUp/k2k/clashWithSuper.kt");
        }

        @TestMetadata("constructorParametersToInterface.kt")
        public void testConstructorParametersToInterface() throws Exception {
            runTest("testData/refactoring/pullUp/k2k/constructorParametersToInterface.kt");
        }

        @TestMetadata("constructorParametersToSuperClass.kt")
        public void testConstructorParametersToSuperClass() throws Exception {
            runTest("testData/refactoring/pullUp/k2k/constructorParametersToSuperClass.kt");
        }

        @TestMetadata("constructorParametersToSuperClassAndMakeAbstract.kt")
        public void testConstructorParametersToSuperClassAndMakeAbstract() throws Exception {
            runTest("testData/refactoring/pullUp/k2k/constructorParametersToSuperClassAndMakeAbstract.kt");
        }

        @TestMetadata("constructorParametersWithDefaultValue1.kt")
        public void testConstructorParametersWithDefaultValue1() throws Exception {
            runTest("testData/refactoring/pullUp/k2k/constructorParametersWithDefaultValue1.kt");
        }

        @TestMetadata("constructorParametersWithDefaultValue2.kt")
        public void testConstructorParametersWithDefaultValue2() throws Exception {
            runTest("testData/refactoring/pullUp/k2k/constructorParametersWithDefaultValue2.kt");
        }

        @TestMetadata("constructorParametersWithNamedArgs.kt")
        public void testConstructorParametersWithNamedArgs() throws Exception {
            runTest("testData/refactoring/pullUp/k2k/constructorParametersWithNamedArgs.kt");
        }

        @TestMetadata("constructorParametersWithNamedArgsAndDefault.kt")
        public void testConstructorParametersWithNamedArgsAndDefault() throws Exception {
            runTest("testData/refactoring/pullUp/k2k/constructorParametersWithNamedArgsAndDefault.kt");
        }

        @TestMetadata("constructorParametersWithVararg.kt")
        public void testConstructorParametersWithVararg() throws Exception {
            runTest("testData/refactoring/pullUp/k2k/constructorParametersWithVararg.kt");
        }

        @TestMetadata("defaultValuesInOverride.kt")
        public void testDefaultValuesInOverride() throws Exception {
            runTest("testData/refactoring/pullUp/k2k/defaultValuesInOverride.kt");
        }

        @TestMetadata("dropFinal.kt")
        public void testDropFinal() throws Exception {
            runTest("testData/refactoring/pullUp/k2k/dropFinal.kt");
        }

        @TestMetadata("dropModifierWhenMovingSideOverride.kt")
        public void testDropModifierWhenMovingSideOverride() throws Exception {
            runTest("testData/refactoring/pullUp/k2k/dropModifierWhenMovingSideOverride.kt");
        }

        @TestMetadata("dropModifierWhenMovingSideOverrideWithAbstract.kt")
        public void testDropModifierWhenMovingSideOverrideWithAbstract() throws Exception {
            runTest("testData/refactoring/pullUp/k2k/dropModifierWhenMovingSideOverrideWithAbstract.kt");
        }

        @TestMetadata("dropModifierWhenMovingSideOverrideWithSuperEntry.kt")
        public void testDropModifierWhenMovingSideOverrideWithSuperEntry() throws Exception {
            runTest("testData/refactoring/pullUp/k2k/dropModifierWhenMovingSideOverrideWithSuperEntry.kt");
        }

        @TestMetadata("dropModifierWhenMovingSideOverrideWithSuperEntryAndAbstract.kt")
        public void testDropModifierWhenMovingSideOverrideWithSuperEntryAndAbstract() throws Exception {
            runTest("testData/refactoring/pullUp/k2k/dropModifierWhenMovingSideOverrideWithSuperEntryAndAbstract.kt");
        }

        @TestMetadata("fromClassToClass.kt")
        public void testFromClassToClass() throws Exception {
            runTest("testData/refactoring/pullUp/k2k/fromClassToClass.kt");
        }

        @TestMetadata("fromClassToClassMakeAbstract.kt")
        public void testFromClassToClassMakeAbstract() throws Exception {
            runTest("testData/refactoring/pullUp/k2k/fromClassToClassMakeAbstract.kt");
        }

        @TestMetadata("fromClassToClassMakeAbstractWithCommentAndAnotherIndent.kt")
        public void testFromClassToClassMakeAbstractWithCommentAndAnotherIndent() throws Exception {
            runTest("testData/refactoring/pullUp/k2k/fromClassToClassMakeAbstractWithCommentAndAnotherIndent.kt");
        }

        @TestMetadata("fromClassToClassMakeAbstractWithCommentAndAnotherIndent2.kt")
        public void testFromClassToClassMakeAbstractWithCommentAndAnotherIndent2() throws Exception {
            runTest("testData/refactoring/pullUp/k2k/fromClassToClassMakeAbstractWithCommentAndAnotherIndent2.kt");
        }

        @TestMetadata("fromClassToClassWithGenerics.kt")
        public void testFromClassToClassWithGenerics() throws Exception {
            runTest("testData/refactoring/pullUp/k2k/fromClassToClassWithGenerics.kt");
        }

        @TestMetadata("fromClassToInterface.kt")
        public void testFromClassToInterface() throws Exception {
            runTest("testData/refactoring/pullUp/k2k/fromClassToInterface.kt");
        }

        @TestMetadata("fromClassToInterfaceMakeAbstract.kt")
        public void testFromClassToInterfaceMakeAbstract() throws Exception {
            runTest("testData/refactoring/pullUp/k2k/fromClassToInterfaceMakeAbstract.kt");
        }

        @TestMetadata("implicitCompanionUsages.kt")
        public void testImplicitCompanionUsages() throws Exception {
            runTest("testData/refactoring/pullUp/k2k/implicitCompanionUsages.kt");
        }

        @TestMetadata("inaccessibleMemberUsed.kt")
        public void testInaccessibleMemberUsed() throws Exception {
            runTest("testData/refactoring/pullUp/k2k/inaccessibleMemberUsed.kt");
        }

        @TestMetadata("initializerInConstructor.kt")
        public void testInitializerInConstructor() throws Exception {
            runTest("testData/refactoring/pullUp/k2k/initializerInConstructor.kt");
        }

        @TestMetadata("initializerInMultipleConstructorsEq.kt")
        public void testInitializerInMultipleConstructorsEq() throws Exception {
            runTest("testData/refactoring/pullUp/k2k/initializerInMultipleConstructorsEq.kt");
        }

        @TestMetadata("initializerInMultipleConstructorsNonEq.kt")
        public void testInitializerInMultipleConstructorsNonEq() throws Exception {
            runTest("testData/refactoring/pullUp/k2k/initializerInMultipleConstructorsNonEq.kt");
        }

        @TestMetadata("innerClassToInterface.kt")
        public void testInnerClassToInterface() throws Exception {
            runTest("testData/refactoring/pullUp/k2k/innerClassToInterface.kt");
        }

        @TestMetadata("moveAllSuperInterfaces.kt")
        public void testMoveAllSuperInterfaces() throws Exception {
            runTest("testData/refactoring/pullUp/k2k/moveAllSuperInterfaces.kt");
        }

        @TestMetadata("moveAllSuperInterfacesWithGenerics.kt")
        public void testMoveAllSuperInterfacesWithGenerics() throws Exception {
            runTest("testData/refactoring/pullUp/k2k/moveAllSuperInterfacesWithGenerics.kt");
        }

        @TestMetadata("moveSuperInterfaceToItSelf.kt")
        public void testMoveSuperInterfaceToItSelf() throws Exception {
            runTest("testData/refactoring/pullUp/k2k/moveSuperInterfaceToItSelf.kt");
        }

        @TestMetadata("moveSuperInterfaces.kt")
        public void testMoveSuperInterfaces() throws Exception {
            runTest("testData/refactoring/pullUp/k2k/moveSuperInterfaces.kt");
        }

        @TestMetadata("moveSuperInterfacesToEmptySpecifierList.kt")
        public void testMoveSuperInterfacesToEmptySpecifierList() throws Exception {
            runTest("testData/refactoring/pullUp/k2k/moveSuperInterfacesToEmptySpecifierList.kt");
        }

        @TestMetadata("multipleInitializersInConstructorsEq.kt")
        public void testMultipleInitializersInConstructorsEq() throws Exception {
            runTest("testData/refactoring/pullUp/k2k/multipleInitializersInConstructorsEq.kt");
        }

        @TestMetadata("noCaret.kt")
        public void testNoCaret() throws Exception {
            runTest("testData/refactoring/pullUp/k2k/noCaret.kt");
        }

        @TestMetadata("noClashWithAbstractSuper.kt")
        public void testNoClashWithAbstractSuper() throws Exception {
            runTest("testData/refactoring/pullUp/k2k/noClashWithAbstractSuper.kt");
        }

        @TestMetadata("noInitializationInInterface.kt")
        public void testNoInitializationInInterface() throws Exception {
            runTest("testData/refactoring/pullUp/k2k/noInitializationInInterface.kt");
        }

        @TestMetadata("noSuperClass.kt")
        public void testNoSuperClass() throws Exception {
            runTest("testData/refactoring/pullUp/k2k/noSuperClass.kt");
        }

        @TestMetadata("noVisibilityCheckBetweenMovedMembers.kt")
        public void testNoVisibilityCheckBetweenMovedMembers() throws Exception {
            runTest("testData/refactoring/pullUp/k2k/noVisibilityCheckBetweenMovedMembers.kt");
        }

        @TestMetadata("outsideOfClass.kt")
        public void testOutsideOfClass() throws Exception {
            runTest("testData/refactoring/pullUp/k2k/outsideOfClass.kt");
        }

        @TestMetadata("parameterNameConflict.kt")
        public void testParameterNameConflict() throws Exception {
            runTest("testData/refactoring/pullUp/k2k/parameterNameConflict.kt");
        }

        @TestMetadata("parametersInPrimaryInitializer.kt")
        public void testParametersInPrimaryInitializer() throws Exception {
            runTest("testData/refactoring/pullUp/k2k/parametersInPrimaryInitializer.kt");
        }

        @TestMetadata("privateMemberWithUsagesToClass.kt")
        public void testPrivateMemberWithUsagesToClass() throws Exception {
            runTest("testData/refactoring/pullUp/k2k/privateMemberWithUsagesToClass.kt");
        }

        @TestMetadata("privateMemberWithUsagesToInterface.kt")
        public void testPrivateMemberWithUsagesToInterface() throws Exception {
            runTest("testData/refactoring/pullUp/k2k/privateMemberWithUsagesToInterface.kt");
        }

        @TestMetadata("propertyDependenceSatisfied.kt")
        public void testPropertyDependenceSatisfied() throws Exception {
            runTest("testData/refactoring/pullUp/k2k/propertyDependenceSatisfied.kt");
        }

        @TestMetadata("propertyDependenceUnsatisfied.kt")
        public void testPropertyDependenceUnsatisfied() throws Exception {
            runTest("testData/refactoring/pullUp/k2k/propertyDependenceUnsatisfied.kt");
        }

        @TestMetadata("propertyWithoutLightMethod.kt")
        public void testPropertyWithoutLightMethod() throws Exception {
            runTest("testData/refactoring/pullUp/k2k/propertyWithoutLightMethod.kt");
        }

        @TestMetadata("publicToInterface.kt")
        public void testPublicToInterface() throws Exception {
            runTest("testData/refactoring/pullUp/k2k/publicToInterface.kt");
        }

        @TestMetadata("reformatModifierList.kt")
        public void testReformatModifierList() throws Exception {
            runTest("testData/refactoring/pullUp/k2k/reformatModifierList.kt");
        }

        @TestMetadata("removeVisibilityOnOverride.kt")
        public void testRemoveVisibilityOnOverride() throws Exception {
            runTest("testData/refactoring/pullUp/k2k/removeVisibilityOnOverride.kt");
        }

        @TestMetadata("skipFakeOverrides.kt")
        public void testSkipFakeOverrides() throws Exception {
            runTest("testData/refactoring/pullUp/k2k/skipFakeOverrides.kt");
        }

        @TestMetadata("spaceAfterModifier.kt")
        public void testSpaceAfterModifier() throws Exception {
            runTest("testData/refactoring/pullUp/k2k/spaceAfterModifier.kt");
        }

        @TestMetadata("superToThis.kt")
        public void testSuperToThis() throws Exception {
            runTest("testData/refactoring/pullUp/k2k/superToThis.kt");
        }

        @TestMetadata("toIndirectSuperClass.kt")
        public void testToIndirectSuperClass() throws Exception {
            runTest("testData/refactoring/pullUp/k2k/toIndirectSuperClass.kt");
        }

        @TestMetadata("usedPrivateToClass.kt")
        public void testUsedPrivateToClass() throws Exception {
            runTest("testData/refactoring/pullUp/k2k/usedPrivateToClass.kt");
        }
    }

    @RunWith(JUnit3RunnerWithInners.class)
    @TestMetadata("testData/refactoring/pullUp/k2j")
    public static class K2J extends AbstractPullUpTest {
        @java.lang.Override
        @org.jetbrains.annotations.NotNull
        public final KotlinPluginMode getPluginMode() {
            return KotlinPluginMode.K1;
        }

        private void runTest(String testDataFilePath) throws Exception {
            KotlinTestUtils.runTest(this::doKotlinTest, this, testDataFilePath);
        }

        @TestMetadata("constructorParameterToClass.kt")
        public void testConstructorParameterToClass() throws Exception {
            runTest("testData/refactoring/pullUp/k2j/constructorParameterToClass.kt");
        }

        @TestMetadata("defaultValuesInOverride.kt")
        public void testDefaultValuesInOverride() throws Exception {
            runTest("testData/refactoring/pullUp/k2j/defaultValuesInOverride.kt");
        }

        @TestMetadata("fromClassToClass.kt")
        public void testFromClassToClass() throws Exception {
            runTest("testData/refactoring/pullUp/k2j/fromClassToClass.kt");
        }

        @TestMetadata("fromClassToClassAndMakeAbstract.kt")
        public void testFromClassToClassAndMakeAbstract() throws Exception {
            runTest("testData/refactoring/pullUp/k2j/fromClassToClassAndMakeAbstract.kt");
        }

        @TestMetadata("fromClassToClassWithGenerics.kt")
        public void testFromClassToClassWithGenerics() throws Exception {
            runTest("testData/refactoring/pullUp/k2j/fromClassToClassWithGenerics.kt");
        }

        @TestMetadata("fromClassToInterface.kt")
        public void testFromClassToInterface() throws Exception {
            runTest("testData/refactoring/pullUp/k2j/fromClassToInterface.kt");
        }

        @TestMetadata("fromClassToNestedClass.kt")
        public void testFromClassToNestedClass() throws Exception {
            runTest("testData/refactoring/pullUp/k2j/fromClassToNestedClass.kt");
        }

        @TestMetadata("moveSuperInterfacesToClass.kt")
        public void testMoveSuperInterfacesToClass() throws Exception {
            runTest("testData/refactoring/pullUp/k2j/moveSuperInterfacesToClass.kt");
        }

        @TestMetadata("moveSuperInterfacesToInterface.kt")
        public void testMoveSuperInterfacesToInterface() throws Exception {
            runTest("testData/refactoring/pullUp/k2j/moveSuperInterfacesToInterface.kt");
        }

        @TestMetadata("moveSuperInterfacesWithGenerics.kt")
        public void testMoveSuperInterfacesWithGenerics() throws Exception {
            runTest("testData/refactoring/pullUp/k2j/moveSuperInterfacesWithGenerics.kt");
        }

        @TestMetadata("publicToInterface.kt")
        public void testPublicToInterface() throws Exception {
            runTest("testData/refactoring/pullUp/k2j/publicToInterface.kt");
        }

        @TestMetadata("usedPrivateToClass.kt")
        public void testUsedPrivateToClass() throws Exception {
            runTest("testData/refactoring/pullUp/k2j/usedPrivateToClass.kt");
        }
    }

    @RunWith(JUnit3RunnerWithInners.class)
    @TestMetadata("testData/refactoring/pullUp/j2k")
    public static class J2K extends AbstractPullUpTest {
        @java.lang.Override
        @org.jetbrains.annotations.NotNull
        public final KotlinPluginMode getPluginMode() {
            return KotlinPluginMode.K1;
        }

        private void runTest(String testDataFilePath) throws Exception {
            KotlinTestUtils.runTest(this::doJavaTest, this, testDataFilePath);
        }

        @TestMetadata("fromClassToClass.java")
        public void testFromClassToClass() throws Exception {
            runTest("testData/refactoring/pullUp/j2k/fromClassToClass.java");
        }

        @TestMetadata("fromClassToClassAndMakeAbstract.java")
        public void testFromClassToClassAndMakeAbstract() throws Exception {
            runTest("testData/refactoring/pullUp/j2k/fromClassToClassAndMakeAbstract.java");
        }

        @TestMetadata("fromClassToClassWithGenerics.java")
        public void testFromClassToClassWithGenerics() throws Exception {
            runTest("testData/refactoring/pullUp/j2k/fromClassToClassWithGenerics.java");
        }

        @TestMetadata("fromClassToInterface.java")
        public void testFromClassToInterface() throws Exception {
            runTest("testData/refactoring/pullUp/j2k/fromClassToInterface.java");
        }

        @TestMetadata("fromClassToInterfaceWithConflicts.java")
        public void testFromClassToInterfaceWithConflicts() throws Exception {
            runTest("testData/refactoring/pullUp/j2k/fromClassToInterfaceWithConflicts.java");
        }

        @TestMetadata("fromClassToNestedClass.java")
        public void testFromClassToNestedClass() throws Exception {
            runTest("testData/refactoring/pullUp/j2k/fromClassToNestedClass.java");
        }

        @TestMetadata("moveSuperInterfacesToClass.java")
        public void testMoveSuperInterfacesToClass() throws Exception {
            runTest("testData/refactoring/pullUp/j2k/moveSuperInterfacesToClass.java");
        }

        @TestMetadata("moveSuperInterfacesToInterface.java")
        public void testMoveSuperInterfacesToInterface() throws Exception {
            runTest("testData/refactoring/pullUp/j2k/moveSuperInterfacesToInterface.java");
        }

        @TestMetadata("moveSuperInterfacesWithGenerics.java")
        public void testMoveSuperInterfacesWithGenerics() throws Exception {
            runTest("testData/refactoring/pullUp/j2k/moveSuperInterfacesWithGenerics.java");
        }
    }
}
