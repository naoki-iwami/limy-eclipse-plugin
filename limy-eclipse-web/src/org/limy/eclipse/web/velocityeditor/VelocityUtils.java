/*
 * Created 2006/01/14
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
package org.limy.eclipse.web.velocityeditor;

import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.source.projection.ProjectionAnnotation;

/**
 * Velocity関連のユーティリティクラスです。
 * @author Naoki Iwami
 */
public final class VelocityUtils {
    
    /** リテラル文字 */
    private static final String LITERAL_CHARS = "_/\\";
    
    /**
     * private constructor
     */
    private VelocityUtils() { }

    /**
     * ドキュメントからFolding対象となる要素を取得します。
     * @param doc ドキュメント
     * @return Folding対象マップ
     * @throws BadLocationException 文字ポジション例外
     */
    public static Map<ProjectionAnnotation, Position> getFoldingAnnotations(IDocument doc)
            throws BadLocationException {
        
        Map<ProjectionAnnotation, Position> results
                = new VelocityFoldingParser(doc).parseAnnotations();
        if (!results.isEmpty()) {
            int offset = results.entrySet().iterator().next().getValue().getOffset();
            if (doc.getLineOfOffset(offset) == 0) {
                // ファイル先頭からFoldingの場合、JS専用ファイルであるとみなしFolgingをOFFにする
                for (Entry<ProjectionAnnotation, Position> entry : results.entrySet()) {
                    entry.getKey().markExpanded();
                }
            }
        }
        return results;
    }

    /**
     * 指定した位置にあるリテラル（URLなど）の位置を取得します。
     * @param doc ドキュメント
     * @param offset ドキュメント中の文字オフセット
     * @return 単語の位置
     * @throws BadLocationException 文字ポジション例外
     */
    public static Position getLiteralPosition(IDocument doc, int offset)
            throws BadLocationException {
        
        int pos = offset;
        char c = doc.getChar(pos);
        if (!isLiteral(c)) {
            return null;
        }

        Position r = new Position(0);
        for (pos = pos - 1; pos >= 0 && isLiteral(doc.getChar(pos)); --pos) {
            // empty
        }
        r.setOffset(++pos);

        for (pos = offset + 1; pos < doc.getLength(); ++pos) {
            if (!isLiteral(doc.getChar(pos))) {
                break;
            }
        }
        r.setLength(pos - r.getOffset());
        return r;
    }

    /**
     * 指定した位置にある単語（英数字およびアンダーライン）の位置を取得します。
     * @param doc ドキュメント
     * @param offset ドキュメント中の文字オフセット
     * @return 単語の位置
     * @throws BadLocationException 文字ポジション例外
     */
    public static Position getWordPosition(IDocument doc, int offset) throws BadLocationException {
        int pos = offset;
        char c = doc.getChar(pos);
        if (!isFunctionLiteral(c)) {
            return null;
        }

        Position r = new Position(0);
        for (pos = pos - 1; pos >= 0 && isFunctionLiteral(doc.getChar(pos)); --pos) {
            // empty
        }
        r.setOffset(++pos);

        for (pos = offset + 1; pos < doc.getLength(); ++pos) {
            if (!isFunctionLiteral(doc.getChar(pos))) {
                break;
            }
        }
        r.setLength(pos - r.getOffset());
        return r;
    }

    /**
     * 文字がVelocity関数名として有効かどうかを返します。
     * @param c 文字
     * @return 関数名として有効ならば真
     */
    private static boolean isFunctionLiteral(char c) {
        return Character.isLetterOrDigit(c) || c == '_';
    }

    /**
     * 文字がリテラルとして有効かどうかを返します。
     * @param c 文字
     * @return リテラルとして有効ならば真
     */
    private static boolean isLiteral(char c) {
        return Character.isLetterOrDigit(c) || (LITERAL_CHARS.indexOf(c) >= 0);
    }

}
