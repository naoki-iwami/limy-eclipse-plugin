/*
 * Created 2005/09/17
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
package org.limy.eclipse.prop.editor;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentExtension4;

/**
 * プロパティドキュメント用のユーティリティクラスです。
 * @author Naoki Iwami
 */
public final class PropertyDocumentUtils {
    
    /**
     * private constructor
     */
    private PropertyDocumentUtils() { }

    /**
     * @param document ドキュメント
     */
    public static void createDocumentLocal(IDocument document) {
        String lines = document.get();
        StringBuffer buff = new StringBuffer();
        int cnt = 0;
        int ucnt = 0;
        char uc = 0;
        for (int i = 0; i < lines.length(); i++) {
            char c = lines.charAt(i);
            switch (cnt) {
            case 0:
                if (c == '\\') {
                    cnt = 1;
                } else {
                    buff.append(c);
                }
                break;
            case 1:
                if (c == 'u') {
                    cnt = 2;
                    ucnt = 0;
                } else {
                    buff.append(lines.charAt(i - 1));
                    buff.append(c);
                    cnt = 0;
                    ucnt = 0;
                }
                break;
            case 2:
                uc <<= 4;
                if (c >= '0' && c <= '9') {
                    uc += c - '0';
                } else if (c >= 'a' && c <= 'f') {
                    uc += 10 + (c - 'a');
                } else if (c >= 'A' && c <= 'F') {
                    uc += 10 + (c - 'A');
                }
                if (++ucnt == 4) {
                    cnt = 0;
                    ucnt = 0;
                    buff.append(uc);
                    uc = 0;
                }
                break;
            default:
                break;
            }
        }
        if (document instanceof IDocumentExtension4) {
            IDocumentExtension4 newDoc = (IDocumentExtension4)document;
            newDoc.set(buff.toString(), newDoc.getModificationStamp());
        } else {
            document.set(buff.toString());
        }
    }

    /**
     * ドキュメントのマルチ文字を\\uxx形式に変換した文字列を返します。
     * @param document ドキュメント
     * @return \\uxx形式に変換した文字列
     */
    public static String createSaveString(IDocument document) {
        StringBuilder buff = new StringBuilder();
        String lines = document.get();
        for (int i = 0; i < lines.length(); i++) {
            char c = lines.charAt(i);
            if (c < 0x100) {
                buff.append(c);
            } else {
                buff.append("\\u");
                appendHex(buff, (c >> 12) & 0x0f);
                appendHex(buff, (c >> 8) & 0x0f);
                appendHex(buff, (c >> 4) & 0x0f);
                appendHex(buff, c & 0x0f);
            }
        }
        return buff.toString();
    }

    /**
     * @param buff
     * @param i
     */
    private static void appendHex(StringBuilder buff, int i) {
        if (i >= 0 && i < 10) {
            buff.append((char)('0' + i));
        } else {
            buff.append((char)('a' + (i - 10)));
        }
    }

}
