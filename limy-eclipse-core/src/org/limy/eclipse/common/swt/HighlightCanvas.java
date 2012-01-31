/*
 * Created 2007/02/19
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
package org.limy.eclipse.common.swt;

import java.awt.geom.Rectangle2D;

import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;

/**
 * 特定の領域をハイライト表示可能なキャンバスクラスです。
 * @author Naoki Iwami
 */
public class HighlightCanvas extends Canvas {

    /** ハイライト色 */
    private RGB highlightRGB = new RGB(255, 0, 0);

    /** 影の色 */
    private RGB shadowRGB = new RGB(255, 255, 255);

    /** 現在描画中のrect */
    private Rectangle2D.Double targetRect;
    
    /**
     * HighlightCanvasインスタンスを構築します。
     * @param parent
     * @param style
     */
    public HighlightCanvas(Composite parent, int style) {
        super(parent, style);
        addPaintListener(new PaintListener() {
            public void paintControl(PaintEvent e) {
                
                if (targetRect != null) {
                    e.gc.setForeground(getColor(e, highlightRGB));
                    e.gc.drawRectangle((int)targetRect.x, (int)targetRect.y,
                            (int)targetRect.width, (int)targetRect.height);
                    e.gc.setForeground(getColor(e, shadowRGB));
                    e.gc.drawRectangle((int)targetRect.x + 1, (int)targetRect.y + 1,
                            (int)targetRect.width - 2, (int)targetRect.height - 2);
                }
            }

        });

    }

    // ------------------------ Getter/Setter Methods

    /**
     * ハイライト色を設定します。
     * @param highlightRGB ハイライト色
     */
    public void setHighlightRGB(RGB highlightRGB) {
        this.highlightRGB = highlightRGB;
    }

    /**
     * 影の色を設定します。
     * @param shadowRGB 影の色
     */
    public void setShadowRGB(RGB shadowRGB) {
        this.shadowRGB = shadowRGB;
    }

    /**
     * 現在描画中のrectを取得します。
     * @return 現在描画中のrect
     */
    public Rectangle2D.Double getTargetRect() {
        return targetRect;
    }

    /**
     * 現在描画中のrectを設定します。
     * @param targetRect 現在描画中のrect
     */
    public void setTargetRect(Rectangle2D.Double targetRect) {
        this.targetRect = targetRect;
    }
    
    // ------------------------ Private Methods

    private Color getColor(PaintEvent e, RGB rgb) {
        return ColorProvider.getColor(e.gc.getDevice(), rgb);
    }

}
