// Copyright 2000-2025 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license.
package com.intellij.java.codeserver.highlighting;

import com.intellij.java.codeserver.highlighting.errors.JavaErrorKinds;
import com.intellij.java.codeserver.highlighting.errors.JavaIncompatibleTypeError;
import com.intellij.psi.*;
import com.intellij.psi.impl.IncompleteModelUtil;
import com.intellij.psi.util.InheritanceUtil;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.psi.util.PsiUtil;
import com.intellij.psi.util.TypeConversionUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

final class ExpressionChecker {
  private final @NotNull JavaErrorVisitor myVisitor;

  ExpressionChecker(@NotNull JavaErrorVisitor visitor) { myVisitor = visitor; }

  void checkQualifiedNew(@NotNull PsiNewExpression expression, @Nullable PsiType type, @Nullable PsiClass aClass) {
    PsiExpression qualifier = expression.getQualifier();
    if (qualifier == null) return;
    if (type instanceof PsiArrayType) {
      myVisitor.report(JavaErrorKinds.NEW_EXPRESSION_QUALIFIED_MALFORMED.create(expression));
      return;
    }
    if (aClass == null) return;
    if (aClass.hasModifierProperty(PsiModifier.STATIC)) {
      myVisitor.report(JavaErrorKinds.NEW_EXPRESSION_QUALIFIED_STATIC_CLASS.create(expression, aClass));
      return;
    }
    if (aClass instanceof PsiAnonymousClass anonymousClass) {
      PsiClass baseClass = PsiUtil.resolveClassInType(anonymousClass.getBaseClassType());
      if (baseClass != null && baseClass.isInterface()) {
        myVisitor.report(JavaErrorKinds.NEW_EXPRESSION_QUALIFIED_ANONYMOUS_IMPLEMENTS_INTERFACE.create(expression, aClass));
        return;
      }
    }
    PsiJavaCodeReferenceElement reference = expression.getClassOrAnonymousClassReference();
    if (reference != null) {
      PsiElement refQualifier = reference.getQualifier();
      if (refQualifier != null) {
        myVisitor.report(JavaErrorKinds.NEW_EXPRESSION_QUALIFIED_QUALIFIED_CLASS_REFERENCE.create(refQualifier));
      }
    }
  }

  void checkCreateInnerClassFromStaticContext(@NotNull PsiNewExpression expression, @NotNull PsiClass aClass) {
    if (aClass instanceof PsiAnonymousClass anonymousClass) {
      aClass = anonymousClass.getBaseClassType().resolve();
      if (aClass == null) return;
    }

    PsiExpression qualifier = expression.getQualifier();
    PsiElement placeToSearchEnclosingFrom;
    if (qualifier != null) {
      placeToSearchEnclosingFrom = PsiUtil.resolveClassInType(qualifier.getType());
    }
    else {
      placeToSearchEnclosingFrom = expression;
    }
    if (placeToSearchEnclosingFrom == null) {
      return;
    }
    checkCreateInnerClassFromStaticContext(expression, placeToSearchEnclosingFrom, aClass);
  }

  void checkCreateInnerClassFromStaticContext(@NotNull PsiElement element,
                                              @NotNull PsiElement placeToSearchEnclosingFrom,
                                              @NotNull PsiClass aClass) {
    if (!PsiUtil.isInnerClass(aClass)) return;
    PsiClass outerClass = aClass.getContainingClass();
    if (outerClass == null) return;

    if (outerClass instanceof PsiSyntheticClass ||
        InheritanceUtil.hasEnclosingInstanceInScope(outerClass, placeToSearchEnclosingFrom, true, false)) {
      return;
    }
    checkIllegalEnclosingUsage(placeToSearchEnclosingFrom, aClass, outerClass, element);
  }

  void checkIllegalEnclosingUsage(@NotNull PsiElement place,
                                  @Nullable PsiClass aClass,
                                  @NotNull PsiClass outerClass,
                                  @NotNull PsiElement elementToHighlight) {
    var context = new JavaErrorKinds.ClassStaticReferenceErrorContext(outerClass, aClass, place);
    if (!PsiTreeUtil.isContextAncestor(outerClass, place, false)) {
      myVisitor.report(JavaErrorKinds.CLASS_NOT_ENCLOSING.create(elementToHighlight, context));
    }
    else if (context.enclosingStaticElement() != null) {
      myVisitor.report(JavaErrorKinds.CLASS_CANNOT_BE_REFERENCED_FROM_STATIC_CONTEXT.create(elementToHighlight, context));
    }
  }

  void checkAssignability(@Nullable PsiType lType,
                          @Nullable PsiType rType,
                          @Nullable PsiExpression expression,
                          @NotNull PsiElement elementToHighlight) {
    if (lType == rType) return;
    if (expression == null) {
      if (rType == null || lType == null || TypeConversionUtil.isAssignable(lType, rType)) return;
    }
    else if (TypeConversionUtil.areTypesAssignmentCompatible(lType, expression) || PsiTreeUtil.hasErrorElements(expression)) {
      return;
    }
    if (rType == null) {
      rType = expression.getType();
    }
    if (lType == null || lType == PsiTypes.nullType()) {
      return;
    }
    if (expression != null && IncompleteModelUtil.isIncompleteModel(expression) &&
        IncompleteModelUtil.isPotentiallyConvertible(lType, expression)) {
      return;
    }
    myVisitor.report(JavaErrorKinds.TYPE_INCOMPATIBLE.create(elementToHighlight, new JavaIncompatibleTypeError(lType, rType)));
  }
}
