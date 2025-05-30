/*
 * Copyright 2000-2015 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/*
 * @author max
 */
package com.intellij.psi;

import org.jetbrains.annotations.NotNull;

/// A variant of [JavaElementVisitor] which also visits all children elements in the
/// [pre-order fashion](https://en.wikipedia.org/wiki/Tree_traversal#Pre-order).
///
/// It handles all containing elements without consuming stack space, so it can be used
/// even for very deep trees without getting a [StackOverflowError].
///
/// ### Note
///
/// This visitor works for source-based PSI only.
/// Any elements implementing [PsiCompiledElement] are rejected, and an error is logged.
public abstract class JavaRecursiveElementWalkingVisitor extends JavaElementVisitor implements PsiRecursiveVisitor {
  private final PsiWalkingState myWalkingState = new PsiWalkingState(this) {
    @Override
    public void elementFinished(@NotNull PsiElement element) {
      JavaRecursiveElementWalkingVisitor.this.elementFinished(element);
    }
  };

  @Override
  public void visitElement(@NotNull PsiElement element) {
    myWalkingState.elementStarted(element);
  }

  protected void elementFinished(@NotNull PsiElement element) {
  }

  @Override
  public void visitReferenceExpression(@NotNull PsiReferenceExpression expression) {
    visitExpression(expression);
    myWalkingState.startedWalking(); // do not traverse from scratch
    visitReferenceElement(expression);
  }

  public void stopWalking() {
    myWalkingState.stopWalking();
  }
}
