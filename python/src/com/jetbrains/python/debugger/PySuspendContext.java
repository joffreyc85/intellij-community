/*
 * Copyright 2000-2014 JetBrains s.r.o.
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
package com.jetbrains.python.debugger;

import com.intellij.xdebugger.frame.XExecutionStack;
import com.intellij.xdebugger.frame.XSuspendContext;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;


public class PySuspendContext extends XSuspendContext {

  private final XExecutionStack myActiveStack;
  private PyDebugProcess myDebugProcess;

  public PySuspendContext(@NotNull final PyDebugProcess debugProcess, @NotNull final PyThreadInfo threadInfo) {
    myDebugProcess = debugProcess;
    myActiveStack = new PyExecutionStack(debugProcess, threadInfo);
  }

  @Override
  public XExecutionStack getActiveExecutionStack() {
    return myActiveStack;
  }

  @Override
  public XExecutionStack[] getExecutionStacks() {
    final Collection<PyThreadInfo> threads = myDebugProcess.getThreads();
    if (threads.size() < 1) {
      return XExecutionStack.EMPTY_ARRAY;
    }
    else {
      XExecutionStack[] stacks = new XExecutionStack[threads.size()];
      int i = 0;
      for (PyThreadInfo thread : threads) {
        stacks[i++] = new PyExecutionStack(myDebugProcess, thread);
      }
      return stacks;
    }
  }

}
