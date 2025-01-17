// Copyright 2000-2024 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license.
package org.jetbrains.idea.svn.actions;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.vcs.VcsException;
import com.intellij.openapi.vcs.changes.VcsDirtyScopeManager;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.idea.svn.SvnBundle;
import org.jetbrains.idea.svn.SvnVcs;
import org.jetbrains.idea.svn.ignore.FileGroupInfo;
import org.jetbrains.idea.svn.ignore.IgnoreGroupHelperAction;
import org.jetbrains.idea.svn.ignore.SvnPropertyService;

import static org.jetbrains.idea.svn.SvnBundle.messagePointer;

public class RemoveFromIgnoreListAction extends BasicAction {
  private final boolean myUseCommonExtension;

  public RemoveFromIgnoreListAction(boolean useCommonExtension) {
    myUseCommonExtension = useCommonExtension;
  }

  @Override
  protected @NotNull String getActionName() {
    return SvnBundle.message("action.name.undo.ignore.files");
  }

  @Override
  public void update(@NotNull AnActionEvent e) {
    Presentation presentation = e.getPresentation();
    IgnoreGroupHelperAction helper = IgnoreGroupHelperAction.createFor(e);
    if (helper == null || !helper.allAreIgnored()) {
      presentation.setEnabledAndVisible(false);
      return;
    }

    FileGroupInfo fileGroupInfo = helper.getFileGroupInfo();
    if (myUseCommonExtension) {
      presentation.setEnabledAndVisible(helper.areIgnoreExtensionOk());
      presentation.setText(fileGroupInfo.getExtensionMask(), false);
      presentation.setDescription(messagePointer("action.Subversion.UndoIgnore.description"));
    }
    else {
      presentation.setEnabledAndVisible(helper.areIgnoreFilesOk());
      if (fileGroupInfo.oneFileSelected()) {
        presentation.setText(fileGroupInfo.getFileName(), false);
      }
      else {
        presentation.setText(messagePointer("action.Subversion.UndoIgnore.text"));
      }
      presentation.setDescription(messagePointer("action.Subversion.UndoIgnore.description"));
    }
  }

  @Override
  protected VirtualFile @Nullable [] getSelectedFiles(@NotNull AnActionEvent e) {
    return IgnoreGroupHelperAction.getSelectedFiles(e);
  }

  @Override
  protected boolean isEnabled(@NotNull SvnVcs vcs, @NotNull VirtualFile file) {
    return IgnoreGroupHelperAction.isIgnored(vcs, file);
  }

  @Override
  protected void doVcsRefresh(@NotNull SvnVcs vcs, @NotNull VirtualFile file) {
    VcsDirtyScopeManager vcsDirtyScopeManager = VcsDirtyScopeManager.getInstance(vcs.getProject());
    if (file.getParent() != null) {
      vcsDirtyScopeManager.fileDirty(file.getParent());
    }
  }

  @Override
  protected void perform(@NotNull SvnVcs vcs, @NotNull VirtualFile file, @NotNull DataContext context) {
  }

  @Override
  protected void batchPerform(@NotNull SvnVcs vcs, VirtualFile @NotNull [] files, @NotNull DataContext context) throws VcsException {
    FileGroupInfo groupInfo = new FileGroupInfo();
    for (VirtualFile file : files) {
      groupInfo.onFileEnabled(file);
    }
    SvnPropertyService.doRemoveFromIgnoreProperty(vcs, myUseCommonExtension, files, groupInfo);
  }

  @Override
  protected boolean isBatchAction() {
    return true;
  }
}
