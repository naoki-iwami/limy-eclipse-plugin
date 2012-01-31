/*
 * Created 2005/07/21
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

import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.rules.ICharacterScanner;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.Token;
import org.limy.eclipse.common.jface.MultiTextAttribute;

/**
 * プロパティエディタで使用する色付けルールクラスです。
 * @author Naoki Iwami
 */
public class PropertyRule implements IRule {

    // ------------------------ Fields

    /**
     * プロパティ名の属性
     */
    private TextAttribute nameAttr;

    /**
     * プロパティ値の属性
     */
    private TextAttribute valueAttr;

    /**
     * コメントの属性
     */
    private TextAttribute commentAttr;
    
    // ------------------------ Constructors

    /**
     * PropertyRuleインスタンスを構築します。
     * @param nameToken
     * @param valueToken
     * @param commentAttr
     */
    public PropertyRule(
            TextAttribute nameToken,
            TextAttribute valueToken,
            TextAttribute commentAttr) {
        this.nameAttr = nameToken;
        this.valueAttr = valueToken;
        this.commentAttr = commentAttr;
    }
    
    // ------------------------ Implement Methods
    
    public IToken evaluate(ICharacterScanner scanner) {
        if (scanner.getColumn() != 0) {
            return Token.UNDEFINED;
        }
        
        int count = 0;
        boolean nameStart = false; // 名前が出現したらtrue
        boolean isCont = false; // 値が次の行に継続する場合はtrue
        MultiTextAttribute attr = new MultiTextAttribute();
        while (true) {
            int c = scanner.read();
            ++count;
            if (c == ICharacterScanner.EOF) {
                if (nameStart) {
                    // = の後にEOFが出現したら、そこまでをValueとみなしてルール適用
                    attr.add(valueAttr, count - 1);
                    break;
                }
                for (int i = 0; i < count; i++) {
                    scanner.unread();
                }
                return Token.UNDEFINED;
            }
            
            if (!nameStart) {
                if (c == '=') {
                    attr.add(nameAttr, count - 1);
                    attr.add(null, count);
                    nameStart = true;
                } else if (scanner.getColumn() <= 0) {
                    // = が出現しないまま次の行へ移ったら、何もせずに終了
                    for (int i = 0; i < count; i++) {
                        scanner.unread();
                    }
                    return Token.UNDEFINED;
                } else if (c == '#') {
                    taskComment(scanner, attr, count);
                    return new Token(attr);
                }
            } else {
                if (c == '\\') {
                    isCont = true;
                } else if (scanner.getColumn() <= 0) {
                    // = の出現後に次の行へ移ったら、ルール適用
                    if (isCont) {
                        // 継続時は次の行も検索続行
                        isCont = false;
                    } else {
                        attr.add(valueAttr, count);
                        break;
                    }
                } else if (Character.isWhitespace((char)c)) {
                    // empty
                } else {
                    isCont = false;
                }
            }
        }
        
        return new Token(attr);
    }

    // ------------------------ Private Methods

    /**
     * @param scanner
     * @param attr
     * @param tmpCount
     */
    private void taskComment(ICharacterScanner scanner, MultiTextAttribute attr, int tmpCount) {
        
        int count = tmpCount;
        while (true) {
            int c = scanner.read();
            ++count;
            if (c == ICharacterScanner.EOF) {
                attr.add(commentAttr, count - 1);
                break;
            } else if (scanner.getColumn() == 0) {
                attr.add(commentAttr, count);
                break;
            } else {
                // empty
            }
        }
        
    }

}
