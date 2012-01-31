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

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Stack;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.source.projection.ProjectionAnnotation;
import org.limy.eclipse.common.LimyEclipsePluginUtils;
import org.limy.eclipse.web.LimyWebConstants;
import org.limy.eclipse.web.LimyWebPlugin;

/**
 * VelocityエディタのFolding機能をサポートするパーサクラスです。
 * @author Naoki Iwami
 */
public class VelocityFoldingParser {
    
    /**
     * ドキュメント
     */
    private IDocument doc;
    
    /**
     * 内部で使用するポインタ値
     */
    private int pos;

    /**
     * ドキュメント文字長
     */
    private int max;
    
    /**
     * 関数のfolding有効フラグ
     */
    private boolean foldFunctions;

    /**
     * VelocityFoldingParserインスタンスを構築します。
     * @param doc 解析するドキュメント
     */
    public VelocityFoldingParser(IDocument doc) {
        this.doc = doc;
        this.pos = 0;
        this.max = doc.getLength();
        
        foldFunctions = LimyEclipsePluginUtils.getPreferenceBoolean(
                LimyWebPlugin.getDefault().getPreferenceStore(),
                LimyWebConstants.P_FOLDING, true);

    }
    
    /**
     * ドキュメントを解析してFoldingアノテーションマップを返します。
     * @return Foldingアノテーションマップ
     * @throws BadLocationException 文字ポジション例外
     */
    public Map<ProjectionAnnotation, Position> parseAnnotations() throws BadLocationException {

        // アノテーション一覧(返却値)
        Map<ProjectionAnnotation, Position> annotations
                = new LinkedHashMap<ProjectionAnnotation, Position>();

        // ブロックの開始位置リスト
        Stack<Integer> startPositions = new Stack<Integer>();

        while (pos < max) {
            char c = doc.getChar(pos);
            switch (c) {
            case '{':
                startPositions.push(Integer.valueOf(pos));
                break;
            case '}':
                if (startPositions.size() == 1) {
                    int startPos = startPositions.pop().intValue();
                    int lineDiff = doc.getLineOfOffset(pos - 1) - doc.getLineOfOffset(startPos);
                    // 同一行の括弧はFoldingしない
                    if (lineDiff > 0) {
                        ProjectionAnnotation annotation = new ProjectionAnnotation(foldFunctions);
                        Position position = new Position(startPos, pos - startPos);
                        annotations.put(annotation, position);
                    }
                } else if (startPositions.size() == 0) {
                    // 左括弧が出現していないのに右括弧が出現したら、無視する
                } else {
                    // 直前の左括弧情報を廃棄
                    startPositions.pop();
                }
                break;
            default:
                break;
            }
            ++pos;
        }
        return groupAnnotations(annotations);
    }

    /**
     * 連続するアノテーションを一つにまとめます。
     * @param annotations アノテーション一覧（順序付）
     * @return まとめたアノテーション一覧
     * @throws BadLocationException 文字ポジション例外
     */
    private Map<ProjectionAnnotation, Position> groupAnnotations(
            Map<ProjectionAnnotation, Position> annotations) throws BadLocationException {
        
        // 返却値
        Map<ProjectionAnnotation, Position> results
                = new LinkedHashMap<ProjectionAnnotation, Position>();
        
        ProjectionAnnotation lastAnnotation = null;
        for (Map.Entry<ProjectionAnnotation, Position> entry : annotations.entrySet()) {
            ProjectionAnnotation entryKey = entry.getKey();
            Position entryValue = entry.getValue();
            
            if (results.isEmpty()) {
                results.put(entryKey, entryValue);
                lastAnnotation = entryKey;
            } else {
                Position position = annotations.get(lastAnnotation);
                int endPos = position.offset + position.length;
                int lastLine = doc.getLineOfOffset(endPos) + 1;
                int nowLine = doc.getLineOfOffset(entryValue.offset);
                while (lastLine < nowLine) {
                    int offset = doc.getLineOffset(lastLine);
                    char c = doc.getChar(offset);
                    if (" \t/\n\r".indexOf(c) < 0) {
                        break;
                    }
                    ++lastLine;
                }
                if (lastLine == nowLine) {
                    // 連続したアノテーションはまとめる
                    position.length = entryValue.offset + entryValue.length - position.offset;
                    annotations.put(lastAnnotation, position);
                } else {
                    results.put(entryKey, entryValue);
                    lastAnnotation = entryKey;
                }
            }
        }
        
        return results;
    }
    
}
