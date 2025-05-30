// Copyright 2000-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.intellij.workspaceModel.ide.impl.legacyBridge

import com.intellij.configurationStore.serialize
import com.intellij.model.SideEffectGuard
import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.util.JDOMUtil
import com.intellij.platform.workspace.storage.MutableEntityStorage
import com.intellij.platform.workspace.storage.impl.DummyVersionedEntityStorage
import com.intellij.platform.workspace.storage.impl.VersionedEntityStorageOnBuilder
import org.jetbrains.annotations.ApiStatus

//todo restore internal visibility for members of this class after other classes will be moved to this module
@ApiStatus.Internal
abstract class LegacyBridgeModifiableBase(val diff: MutableEntityStorage, cacheStorageResult: Boolean) {
  init {
    SideEffectGuard.checkSideEffectAllowed(SideEffectGuard.EffectType.PROJECT_MODEL)
  }

  val entityStorageOnDiff = if (cacheStorageResult) VersionedEntityStorageOnBuilder(diff)
  else DummyVersionedEntityStorage(diff) // see https://github.com/JetBrains/intellij-community/pull/2984#issuecomment-2754569202

  private var committedOrDisposed = false

  protected var modelIsCommittedOrDisposed
    get() = committedOrDisposed
    set(value) {
      if (!value) error("Only 'true' value is accepted here")
      committedOrDisposed = true
    }

  fun assertModelIsLive() {
    if (committedOrDisposed) {
      error("${javaClass.simpleName} was already committed or disposed")
    }
  }

  companion object {
    // TODO Some common mechanics?
    @JvmStatic
    val assertChangesApplied
      get() = true

    fun serializeComponentAsString(rootElementName: String, component: PersistentStateComponent<*>?): String? {
      val state = component?.state ?: return null
      val propertiesElement = serialize(state) ?: return null
      propertiesElement.name = rootElementName
      return JDOMUtil.writeElement(propertiesElement)
    }
  }
}
