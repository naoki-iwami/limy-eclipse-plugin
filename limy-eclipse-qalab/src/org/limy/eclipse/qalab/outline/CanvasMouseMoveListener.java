/*
 * Created 2007/02/28
 * Copyright (C) 2003-2009  Naoki Iwami (naoki@limy.org)
 *
 * This file is part of Limy Eclipse Plugin.
 *
 * Limy Eclipse Plugin is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Limy Eclipse Plugin is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Limy Eclipse Plugin.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.limy.eclipse.qalab.outline;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D.Double;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.ui.texteditor.ITextEditor;
import org.limy.eclipse.common.LimyEclipsePluginUtils;
import org.limy.eclipse.common.swt.HighlightCanvas;
import org.limy.eclipse.common.ui.LimyUIUtils;

/**
 * ハイライト対応キャンパスへのマウス移動リスナ実装クラスです。
 * @author Naoki Iwami
 */
public class CanvasMouseMoveListener implements MouseMoveListener {
    
    /** ハイライト表示対応キャンパス */
    private HighlightCanvas canvas;
    
    /** イメージ情報 */
    private PopupImage image;

    public CanvasMouseMoveListener(HighlightCanvas canvas, PopupImage image) {
        this.canvas = canvas;
        this.image = image;
    }
    
    public void mouseMove(MouseEvent e) {
        Point2D.Double point = new Point2D.Double(e.x, e.y);
        for (ClickablePointInfo info : image.getClickableElements()) {

            ClickablePointInfo pointInfo = (ClickablePointInfo)info;
            Double rect = info.getRect();
            
            if (rect.contains(point)) {
                if (!rect.equals(canvas.getTargetRect())) {
                    highlightInfo(pointInfo);
                }
                return;
            }
        }
        canvas.setTargetRect(null);
        canvas.setToolTipText("");
        canvas.redraw();
    }

    // ------------------------ Private Methods

    /**
     * 対象エレメントをハイライト表示します。
     * @param info 対象エレメント
     */
    private void highlightInfo(ClickablePointInfo info) {
        canvas.setTargetRect(info.getRect());
        canvas.setToolTipText(info.getTooltipText());
        canvas.redraw();
        if (info instanceof LineClickPoint) {
            LineClickPoint lineInfo = (LineClickPoint)info;
            highlightEditor(lineInfo.getLineNumber());
        }
    }

    private void highlightEditor(int lineNumber) {
        if (lineNumber == 0) {
            return;
        }
        ITextEditor editor = LimyUIUtils.getActiveTextEditor();
        IDocument doc = editor.getDocumentProvider().getDocument(editor.getEditorInput());
        try {
            int pos = doc.getLineOffset(lineNumber - 1);
            int len = doc.getLineLength(lineNumber - 1);
            editor.setHighlightRange(pos, len, true);
        } catch (BadLocationException e) {
            LimyEclipsePluginUtils.log(e);
        }
    }

    /**
     * イメージ情報を設定します。
     * @param image イメージ情報
     */
    public void setImage(PopupImage image) {
        this.image = image;
    }

}
