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
package com.intellij.openapi.roots.impl;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.DependencyScope;
import com.intellij.openapi.roots.ExternalLibraryDescriptor;
import com.intellij.openapi.roots.ProjectModelModificationService;
import com.intellij.openapi.roots.ProjectModelModifier;
import com.intellij.openapi.roots.libraries.Library;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

/**
 * @author nik
 */
public class ProjectModelModificationServiceImpl extends ProjectModelModificationService {
  private final Project myProject;

  public ProjectModelModificationServiceImpl(Project project) {
    myProject = project;
  }

  @Override
  public void addDependency(@NotNull Module from, @NotNull Module to, @NotNull DependencyScope scope) {
    for (ProjectModelModifier modifier : getModelModifiers()) {
      if (modifier.addModuleDependency(from, to, scope)) {
        return;
      }
    }
  }

  @Override
  public void addDependency(@NotNull Collection<Module> from, @NotNull ExternalLibraryDescriptor libraryDescriptor, @NotNull DependencyScope scope) {
    for (ProjectModelModifier modifier : getModelModifiers()) {
      if (modifier.addExternalLibraryDependency(from, libraryDescriptor, scope)) {
        return;
      }
    }
  }

  @Override
  public void addDependency(@NotNull Module from, @NotNull Library library, @NotNull DependencyScope scope) {
    for (ProjectModelModifier modifier : getModelModifiers()) {
      if (modifier.addLibraryDependency(from, library, scope)) {
        return;
      }
    }
  }

  @NotNull
  private ProjectModelModifier[] getModelModifiers() {
    return ProjectModelModifier.EP_NAME.getExtensions(myProject);
  }
}
