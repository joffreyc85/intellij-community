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
package com.intellij.openapi.editor.impl;

import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.event.DocumentEvent;
import com.intellij.openapi.editor.ex.DocumentEx;
import com.intellij.openapi.editor.impl.event.DocumentEventImpl;
import com.intellij.openapi.util.Pair;
import com.intellij.openapi.util.ProperTextRange;
import com.intellij.openapi.util.Segment;
import com.intellij.util.ObjectUtils;
import com.intellij.util.diff.FilesTooBigForDiffException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * This class is an extension to range marker that tries to restore its range even in situations when target text referenced by it
 * is replaced.
 * <p/>
 * Example: consider that the user selects all text at editor (Ctrl+A), copies it to the buffer (Ctrl+C) and performs paste (Ctrl+V).
 * All document text is replaced then but in essence it's the same, hence, we may want particular range markers to be still valid.
 *
 * @author max
 */
class PersistentRangeMarker extends RangeMarkerImpl {
  private LinesCols myLinesCols;

  PersistentRangeMarker(DocumentEx document, int startOffset, int endOffset, boolean register) {
    super(document, startOffset, endOffset, register);
    myLinesCols = ObjectUtils.assertNotNull(storeLinesAndCols(this, document));
  }

  @Nullable
  static LinesCols storeLinesAndCols(Segment range, Document myDocument) {
    int myStartLine;
    int myStartColumn;
    int myEndLine;
    int myEndColumn;

    // document might have been changed already
    int startOffset = range.getStartOffset();
    if (startOffset <= myDocument.getTextLength()) {
      myStartLine = myDocument.getLineNumber(startOffset);
      myStartColumn = startOffset - myDocument.getLineStartOffset(myStartLine);
      if (myStartColumn < 0) {
        return null;
      }
    }
    else {
      return null;
    }
    int endOffset = range.getEndOffset();
    if (endOffset <= myDocument.getTextLength()) {
      myEndLine = myDocument.getLineNumber(endOffset);
      myEndColumn = endOffset - myDocument.getLineStartOffset(myEndLine);
      if (myEndColumn < 0) {
        return null;
      }
    }
    else {
      return null;
    }

    return new LinesCols(myStartLine, myStartColumn, myEndLine, myEndColumn);
  }

  @Nullable
  private static Pair<ProperTextRange, LinesCols> translateViaDiff(final DocumentEventImpl event, LinesCols linesCols) {
    try {
      int myStartLine = event.translateLineViaDiffStrict(linesCols.myStartLine);
      Document document = event.getDocument();
      if (myStartLine < 0 || myStartLine >= document.getLineCount()) {
        return null;
      }

      int start = document.getLineStartOffset(myStartLine) + linesCols.myStartColumn;
      if (start >= document.getTextLength()) return null;

      int myEndLine = event.translateLineViaDiffStrict(linesCols.myEndLine);
      if (myEndLine < 0 || myEndLine >= document.getLineCount()) {
        return null;
      }

      int end = document.getLineStartOffset(myEndLine) + linesCols.myEndColumn;
      if (end > document.getTextLength() || end < start) return null;

      return Pair.create(new ProperTextRange(start, end), new LinesCols(myStartLine, linesCols.myStartColumn, myEndLine, linesCols.myEndColumn));
    }
    catch (FilesTooBigForDiffException e) {
      return null;
    }
  }

  @Override
  protected void changedUpdateImpl(@NotNull DocumentEvent e) {
    if (!isValid()) return;

    Pair<ProperTextRange, LinesCols> pair =
      applyChange(e, this, intervalStart(), intervalEnd(), isGreedyToLeft(), isGreedyToRight(), myLinesCols);
    if (pair == null) {
      invalidate(e);
      return;
    }

    setIntervalStart(pair.first.getStartOffset());
    setIntervalEnd(pair.first.getEndOffset());
    myLinesCols = pair.second;
  }

  @Nullable
  static Pair<ProperTextRange, LinesCols> applyChange(DocumentEvent event, Segment range, int intervalStart, int intervalEnd, boolean greedyLeft, boolean greedyRight, LinesCols linesCols) {
    final boolean shouldTranslateViaDiff = PersistentRangeMarkerUtil.shouldTranslateViaDiff(event, range);
    Pair<ProperTextRange, LinesCols> translated = null;
    if (shouldTranslateViaDiff) {
      translated = translateViaDiff((DocumentEventImpl)event, linesCols);
    }
    if (translated == null) {
      ProperTextRange fallback = applyChange(event, intervalStart, intervalEnd, greedyLeft, greedyRight);
      if (fallback == null) return null;

      LinesCols lc = storeLinesAndCols(fallback, event.getDocument());
      if (lc == null) return null;

      translated = Pair.create(fallback, lc);
    }
    if (translated.first.getEndOffset() > event.getDocument().getTextLength() ||
        translated.second.myEndLine < translated.second.myStartLine ||
        translated.second.myStartLine == translated.second.myEndLine && translated.second.myEndColumn < translated.second.myStartColumn ||
        event.getDocument().getLineCount() < translated.second.myEndLine) {
      return null;
    }

    return translated;
  }

  @Override
  public String toString() {
    return "PersistentRangeMarker" +
           (isGreedyToLeft() ? "[" : "(") +
           (isValid() ? "valid" : "invalid") + "," + getStartOffset() + "," + getEndOffset() +
           " " + myLinesCols +
           (isGreedyToRight() ? "]" : ")");
  }

  static class LinesCols {
    private final int myStartLine;
    private final int myStartColumn;
    private final int myEndLine;
    private final int myEndColumn;

    LinesCols(int startLine, int startColumn, int endLine, int endColumn) {
      myStartLine = startLine;
      myStartColumn = startColumn;
      myEndLine = endLine;
      myEndColumn = endColumn;
    }

    @Override
    public String toString() {
      return myStartLine + ":" + myStartColumn + "-" + myEndLine + ":" + myEndColumn;
    }
  }

}
