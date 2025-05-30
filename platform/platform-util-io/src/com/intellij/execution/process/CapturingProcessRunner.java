// Copyright 2000-2024 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license.
package com.intellij.execution.process;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.util.concurrency.annotations.RequiresBackgroundThread;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

public class CapturingProcessRunner {
  private static final @NotNull Logger LOG = Logger.getInstance(CapturingProcessRunner.class);

  private final @NotNull ProcessOutput myOutput;
  private final @NotNull ProcessHandler myProcessHandler;

  public CapturingProcessRunner(@NotNull BaseProcessHandler<?> processHandler) {
    this((ProcessHandler)processHandler);
  }

  public CapturingProcessRunner(@NotNull BaseProcessHandler<?> processHandler,
                                @NotNull Function<? super ProcessOutput, ? extends ProcessListener> processListenerProducer) {
    this((ProcessHandler)processHandler, processListenerProducer);
  }

  public CapturingProcessRunner(@NotNull ProcessHandler processHandler) {
    this(processHandler, processOutput -> new CapturingProcessAdapter(processOutput));
  }

  public CapturingProcessRunner(@NotNull ProcessHandler processHandler,
                                @NotNull Function<? super ProcessOutput, ? extends ProcessListener> processListenerProducer) {
    myOutput = new ProcessOutput();
    myProcessHandler = processHandler;
    myProcessHandler.addProcessListener(processListenerProducer.apply(myOutput));
  }

  @RequiresBackgroundThread(generateAssertion = false)
  public final @NotNull ProcessOutput runProcess() {
    myProcessHandler.startNotify();
    if (myProcessHandler.waitFor()) {
      setErrorCodeIfNotYetSet();
    }
    else {
      LOG.info("runProcess: exit value unavailable");
    }
    return myOutput;
  }

  @RequiresBackgroundThread(generateAssertion = false)
  public @NotNull ProcessOutput runProcess(int timeoutInMilliseconds) {
    return runProcess(timeoutInMilliseconds, true);
  }

  @RequiresBackgroundThread(generateAssertion = false)
  public @NotNull ProcessOutput runProcess(int timeoutInMilliseconds, boolean destroyOnTimeout) {
    // keep in sync with runProcessWithProgressIndicator
    if (timeoutInMilliseconds <= 0) {
      return runProcess();
    }
    else {
      myProcessHandler.startNotify();
      if (myProcessHandler.waitFor(timeoutInMilliseconds)) {
        setErrorCodeIfNotYetSet();
      }
      else {
        if (destroyOnTimeout) {
          myProcessHandler.destroyProcess();
        }
        myOutput.setTimeout();
      }
      return myOutput;
    }
  }

  public @NotNull ProcessOutput runProcess(@NotNull ProgressIndicator indicator) {
    return runProcess(indicator, -1);
  }

  public @NotNull ProcessOutput runProcess(@NotNull ProgressIndicator indicator, int timeoutInMilliseconds) {
    return runProcess(indicator, timeoutInMilliseconds, true);
  }

  public @NotNull ProcessOutput runProcess(@NotNull ProgressIndicator indicator,
                                           int timeoutInMilliseconds,
                                           boolean destroyOnTimeout) {
    // keep in sync with runProcess
    if (timeoutInMilliseconds <= 0) {
      timeoutInMilliseconds = Integer.MAX_VALUE;
    }

    final int WAIT_INTERVAL = 10;
    int waitingTime = 0;
    boolean setExitCode = true;

    myProcessHandler.startNotify();
    while (!myProcessHandler.waitFor(WAIT_INTERVAL)) {
      waitingTime += WAIT_INTERVAL;

      boolean timeout = waitingTime >= timeoutInMilliseconds;
      boolean canceled = indicator.isCanceled();

      if (canceled || timeout) {
        boolean destroying = canceled || destroyOnTimeout;
        setExitCode = destroying;

        if (destroying && !myProcessHandler.isProcessTerminating() && !myProcessHandler.isProcessTerminated()) {
          myProcessHandler.destroyProcess();
        }

        if (canceled) {
          myOutput.setCancelled();
        }
        else {
          myOutput.setTimeout();
        }
        break;
      }
    }
    if (setExitCode) {
      if (myProcessHandler.waitFor()) {
        setErrorCodeIfNotYetSet();
      }
      else {
        LOG.info("runProcess: exit value unavailable");
      }
    }
    return myOutput;
  }

  public void destroyProcess() {
    if (!myProcessHandler.isStartNotified()) {
      myProcessHandler.startNotify();
    }
    // This check mainly avoids warnings about processes that were already terminated.
    // The process status might still change after the check, but that should be harmless.
    if (!myProcessHandler.isProcessTerminating() && !myProcessHandler.isProcessTerminated()) {
      myProcessHandler.destroyProcess();
    }
  }

  private void setErrorCodeIfNotYetSet() {
    // if exit code was set on processTerminated, no need to rewrite it
    // WinPtyProcess returns -2 if pty is already closed
    if (!myOutput.isExitCodeSet() && myProcessHandler instanceof BaseProcessHandler) {
      myOutput.setExitCode(((BaseProcessHandler<?>)myProcessHandler).getProcess().exitValue());
    }
  }
}

