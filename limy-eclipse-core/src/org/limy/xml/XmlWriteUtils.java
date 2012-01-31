/*
 * Created 2007/08/14
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
package org.limy.xml;

import java.io.IOException;
import java.util.Arrays;

/**
 * XML出力関連のユーティリティクラスです。
 * @author Naoki Iwami
 * @depend - - - XmlElement
 */
public final class XmlWriteUtils {
    
    /**
     * 改行文字
     */
    private static final String BR = "\n";

    /**
     * private constructor
     */
    private XmlWriteUtils() { }

    /**
     * XML要素の内容を出力します。
     * @param out 出力先
     * @param root 出力内容
     * @throws IOException I/O例外
     */
    public static void writeXml(Appendable out, XmlElement root) throws IOException {
        writeXml(out, (SimpleElement)root, 0);
    }

    /**
     * XML要素の内容を出力します。
     * @param out 出力先
     * @param root 出力内容
     * @param index インデックス
     * @throws IOException I/O例外
     */
    public static void writeXml(Appendable out, XmlElement root, int index)
            throws IOException {
        
        char[] spaceChar = new char[index * 2];
        Arrays.fill(spaceChar, ' ');
        String space = new String(spaceChar);
        
        out.append(space);
        writeStartTag(out, root);
        
        if (root.getValue() == null && !root.hasChildren()) {
            // 要素値も無く、子要素も無い場合
            out.append("/>").append(BR);
            return;
        }
        
        out.append('>');
        
        if (root.getValue() != null) {
            // 要素値がある場合、それを出力して終了（子要素は出力しない）
            out.append(root.getValue());
            out.append("</");
            out.append(root.getName());
            out.append('>');
            out.append(BR);
            return;
        }
        
        out.append(BR);
        
        writeChildren(out, root, index);
        
        out.append(space);
        out.append("</");
        out.append(root.getName());
        out.append('>');
        out.append(BR);
    
    }

    // ------------------------ Private Methods

    /**
     * XML要素名および属性を出力します。
     * <p>
     * 最後の &gt; は出力しません。<br>
     * &lt;tag value="abc" ... ここまで
     * </p>
     * @param out 出力先
     * @param el XML要素
     * @throws IOException I/O例外
     */
    private static void writeStartTag(Appendable out, XmlElement el)
            throws IOException {
        
        out.append('<');
        out.append(el.getName());
        if (el.hasAttributes()) {
            for (XmlAttribute attr : el.getAttributes()) {
                out.append(' ');
                out.append(attr.getName());
                out.append("=\"");
                out.append(attr.getValue());
                out.append('"');
            }
        }
    }

    /**
     * XMLの子要素を出力します。
     * @param out 出力先
     * @param el XML要素
     * @param tabIndex タブインデックス
     * @throws IOException 
     */
    private static void writeChildren(Appendable out, XmlElement el,
            int tabIndex) throws IOException {
        
        if (el.hasChildren()) {
            for (XmlElement child : el.getChildren()) {
                writeXml(out, child, tabIndex + 1);
            }
        }
    }

}
