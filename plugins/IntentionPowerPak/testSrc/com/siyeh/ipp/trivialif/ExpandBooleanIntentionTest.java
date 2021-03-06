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

/**
 * (c) 2015 Silent Forest AB
 * created: 03 August 2015
 */
package com.siyeh.ipp.trivialif;

import com.intellij.lang.java.JavaLanguage;
import com.intellij.psi.codeStyle.CodeStyleSettingsManager;
import com.intellij.psi.codeStyle.CommonCodeStyleSettings;
import com.siyeh.IntentionPowerPackBundle;
import com.siyeh.ipp.IPPTestCase;

/**
 * @author Bas Leijdekkers
 */
public class ExpandBooleanIntentionTest extends IPPTestCase {

  public void testBraces() {
    final CommonCodeStyleSettings settings = CodeStyleSettingsManager.getSettings(getProject()).getCommonSettings(JavaLanguage.INSTANCE);
    final int oldValue = settings.IF_BRACE_FORCE;
    try {
      settings.IF_BRACE_FORCE = CommonCodeStyleSettings.FORCE_BRACES_ALWAYS;
      doTest();
    } finally {
      settings.IF_BRACE_FORCE = oldValue;
    }
  }

  @Override
  protected String getRelativePath() {
    return "trivialif/expand_boolean";
  }

  @Override
  protected String getIntentionName() {
    return IntentionPowerPackBundle.message("expand.boolean.declaration.intention.name");
  }
}
