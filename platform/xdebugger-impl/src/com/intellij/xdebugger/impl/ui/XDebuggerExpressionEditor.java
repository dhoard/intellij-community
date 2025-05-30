// Copyright 2000-2024 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license.
package com.intellij.xdebugger.impl.ui;

import com.intellij.lang.Language;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.CommonShortcuts;
import com.intellij.openapi.actionSystem.DataSink;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.ex.EditorEx;
import com.intellij.openapi.editor.ex.util.EditorUtil;
import com.intellij.openapi.project.DumbAwareAction;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.ui.EditorTextField;
import com.intellij.util.ui.JBUI;
import com.intellij.util.ui.UIUtil;
import com.intellij.xdebugger.XExpression;
import com.intellij.xdebugger.XSourcePosition;
import com.intellij.xdebugger.evaluation.EvaluationMode;
import com.intellij.xdebugger.evaluation.XDebuggerEditorsProvider;
import com.intellij.xdebugger.impl.breakpoints.XExpressionImpl;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.border.CompoundBorder;

public class XDebuggerExpressionEditor extends XDebuggerEditorBase {
  private final JComponent myComponent;
  private final EditorTextField myEditorTextField;
  private XExpression myExpression;

  public XDebuggerExpressionEditor(Project project,
                                   @NotNull XDebuggerEditorsProvider debuggerEditorsProvider,
                                   @Nullable @NonNls String historyId,
                                   @Nullable XSourcePosition sourcePosition,
                                   @NotNull XExpression text,
                                   final boolean multiline,
                                   boolean editorFont,
                                   boolean showEditor) {
    this(project, debuggerEditorsProvider, historyId, sourcePosition, text, multiline, editorFont, showEditor, null);
  }

  protected XDebuggerExpressionEditor(Project project,
                                      @NotNull XDebuggerEditorsProvider debuggerEditorsProvider,
                                      @Nullable @NonNls String historyId,
                                      @Nullable XSourcePosition sourcePosition,
                                      @NotNull XExpression text,
                                      final boolean multiline,
                                      boolean editorFont,
                                      boolean showEditor,
                                      @Nullable PsiElement psiContext) {
    super(project, debuggerEditorsProvider, multiline ? EvaluationMode.CODE_FRAGMENT : EvaluationMode.EXPRESSION, historyId, sourcePosition,
          psiContext);
    myExpression = XExpressionImpl.changeMode(text, getMode());
    myEditorTextField = new EditorTextField(
      createDocument(myExpression), project, debuggerEditorsProvider.getFileType(), false, !multiline) {
      @Override
      protected @NotNull EditorEx createEditor() {
        final EditorEx editor = super.createEditor();
        editor.setHorizontalScrollbarVisible(multiline);
        editor.setVerticalScrollbarVisible(multiline);
        editor.getSettings().setUseSoftWraps(isUseSoftWraps());
        editor.getSettings().setLineCursorWidth(EditorUtil.getDefaultCaretWidth());
        editor.getColorsScheme().setEditorFontName(getFont().getFontName());
        editor.getColorsScheme().setEditorFontSize(getFont().getSize());
        prepareEditor(editor);
        if (multiline) {
          editor.getContentComponent().setBorder(new CompoundBorder(editor.getContentComponent().getBorder(), JBUI.Borders.emptyLeft(2)));
          editor.setContextMenuGroupId("XDebugger.Evaluate.Code.Fragment.Editor.Popup");
        }
        else {
          foldNewLines(editor);
          if (showEditor) {
            setExpandable(editor);
          }
        }
        return editor;
      }

      @Override
      public void uiDataSnapshot(@NotNull DataSink sink) {
        super.uiDataSnapshot(sink);
        sink.lazy(LangDataKeys.CONTEXT_LANGUAGES, () -> new Language[]{myExpression.getLanguage()});
        sink.lazy(CommonDataKeys.PSI_FILE, () -> PsiDocumentManager.getInstance(getProject()).getPsiFile(getDocument()));
      }
    };
    if (editorFont) {
      myEditorTextField.setFontInheritedFromLAF(false);
      myEditorTextField.setFont(EditorUtil.getEditorFont());
    }

    if (multiline) {
      DumbAwareAction.create(e -> goForward()).registerCustomShortcutSet(CommonShortcuts.MOVE_UP, myEditorTextField);
      DumbAwareAction.create(e -> goBackward()).registerCustomShortcutSet(CommonShortcuts.MOVE_DOWN, myEditorTextField);
    }

    myComponent = decorate(myEditorTextField, multiline, showEditor);
    setExpression(myExpression);
  }

  @Override
  public JComponent getComponent() {
    return myComponent;
  }

  @Override
  public JComponent getEditorComponent() {
    return myEditorTextField;
  }

  @Override
  protected void doSetText(XExpression text) {
    myExpression = text;
    myEditorTextField.setNewDocumentAndFileType(getFileType(text), createDocument(text));
  }

  @Override
  public XExpression getExpression() {
    return getEditorsProvider().createExpression(getProject(), myEditorTextField.getDocument(), myExpression.getLanguage(), myExpression.getMode());
  }

  @Override
  public @Nullable JComponent getPreferredFocusedComponent() {
    final Editor editor = myEditorTextField.getEditor();
    return editor != null ? editor.getContentComponent() : null;
  }

  @Override
  public void setEnabled(boolean enable) {
    if (enable == myComponent.isEnabled()) return;
    UIUtil.setEnabled(myComponent, enable, true);
  }

  @Override
  public @Nullable Editor getEditor() {
    return myEditorTextField.getEditor();
  }

  @Override
  public void selectAll() {
    myEditorTextField.selectAll();
  }
}
