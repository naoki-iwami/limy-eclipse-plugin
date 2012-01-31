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
package org.limy.eclipse.qalab.common;

import org.eclipse.jdt.core.IJavaElement;
import org.w3c.dom.Element;

/**
 * SVG文字列を解析するインターフェイスです。
 * @author Naoki Iwami
 */
public interface ClickableXmlParser {

    /**
     * XML要素から、Java完全限定名を取得します。
     * @param el XML要素
     * @return Java完全限定名
     */
    String getQualifiedName(Element el);

    /**
     * XML要素に対応するツールチップ文字列を生成します。
     * @param javaElement XML要素
     * @return ツールチップ文字列
     */
    String getTooltip(IJavaElement javaElement);

}