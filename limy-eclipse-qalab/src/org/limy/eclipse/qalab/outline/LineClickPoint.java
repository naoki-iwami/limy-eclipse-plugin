/*
 * Created 2007/08/30
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

import java.awt.geom.Rectangle2D.Double;

import org.w3c.dom.Element;

/**
 * 行番号情報を持ったクリッカブル位置情報を表します。
 * @author Naoki Iwami
 */
public class LineClickPoint extends ClickablePointInfo {
    
    /**
     * 作成担当クラスです。
     * @author Naoki Iwami
     */
    static class Creator implements PointInfoCreator {

        public ClickablePointInfo create(Element el, Double rect) {
            String elementName = el.getFirstChild().getNodeValue().trim();
            LineClickPoint info = new LineClickPoint();
            info.setRect(rect);
            if (elementName.startsWith("[L.")) {
                info.setLineNumber(
                        Integer.parseInt(elementName.substring(3, elementName.indexOf(']'))));
            }
            return info;
        }
    }

    // ------------------------ Fields

    /** 行番号 */
    private int lineNumber;

    // ------------------------ Getter/Setter Methods

    /**
     * 行番号を取得します。
     * @return 行番号
     */
    public int getLineNumber() {
        return lineNumber;
    }

    /**
     * 行番号を設定します。
     * @param lineNumber 行番号
     */
    public void setLineNumber(int lineNumber) {
        this.lineNumber = lineNumber;
    }
    
}