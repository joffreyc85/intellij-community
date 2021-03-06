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
package com.intellij.execution.compound;

import com.intellij.execution.configurations.ConfigurationFactory;
import com.intellij.execution.configurations.ConfigurationTypeBase;
import com.intellij.execution.configurations.ConfigurationTypeUtil;
import com.intellij.execution.configurations.RunConfiguration;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.project.Project;
import com.intellij.ui.LayeredIcon;

public class CompoundRunConfigurationType extends ConfigurationTypeBase {

  public static CompoundRunConfigurationType getInstance() {
    return ConfigurationTypeUtil.findConfigurationType(CompoundRunConfigurationType.class);
  }

  public CompoundRunConfigurationType() {
    super("CompoundRunConfigurationType",
          "Compound Run Configuration",
          "It runs batch of run configurations at once",
          LayeredIcon.create(AllIcons.Nodes.Folder, AllIcons.Nodes.RunnableMark));
    addFactory(new ConfigurationFactory(this) {
      @Override
      public RunConfiguration createTemplateConfiguration(Project project) {
        return new CompoundRunConfiguration(project, CompoundRunConfigurationType.this, "Compound Run Configuration");
      }

      @Override
      public String getName() {
        return super.getName();
      }
    });
  }


}
