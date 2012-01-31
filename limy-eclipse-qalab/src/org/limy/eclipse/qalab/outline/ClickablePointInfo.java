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

import java.awt.geom.Rectangle2D;

/**
 * クリック可能なエレメント情報を表します。
 * @author Naoki Iwami
 */
public class ClickablePointInfo {
    
    // ------------------------ Fields

    /** 表示位置 */
    private Rectangle2D.Double rect;
    
    /** ツールチップ文字列 */
    private String tooltipText;

    // ------------------------ Getter/Setter Methods

    /**
     * 表示位置を取得します。
     * @return 表示位置
     */
    public Rectangle2D.Double getRect() {
        return rect;
    }

    /**
     * 表示位置を設定します。
     * @param rect 表示位置
     */
    public void setRect(Rectangle2D.Double rect) {
        this.rect = rect;
    }

    /**
     * ツールチップ文字列を取得します。
     * @return ツールチップ文字列
     */
    public String getTooltipText() {
        return tooltipText;
    }

    /**
     * ツールチップ文字列を設定します。
     * @param tooltipText ツールチップ文字列
     */
    public void setTooltipText(String tooltipText) {
        this.tooltipText = tooltipText;
    }

}
