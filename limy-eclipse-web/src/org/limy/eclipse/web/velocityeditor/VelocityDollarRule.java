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

import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.rules.ICharacterScanner;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.Token;
import org.limy.eclipse.common.jface.MultiTextAttribute;

/**
 * Velocity($記号)の検出ルールを定めたクラスです。
 * @author Naoki Iwami
 */
public class VelocityDollarRule implements IRule {

    // ------------------------ Fields

    /**
     * プロパティ文字列の属性
     */
    private TextAttribute propertyToken;

    /**
     * スキャナ
     */
    private ICharacterScanner scanner;
    
    /**
     * スキャナ内カウント
     */
    private int count;

    // ------------------------ Constructors

    /**
     * VelocityDollarRuleインスタンスを構築します。
     * @param propertyToken
     */
    public VelocityDollarRule(TextAttribute propertyToken) {
        this.propertyToken = propertyToken;
    }
    
    // ------------------------ Public Methods

    /**
     * スキャナ内カウントを返します。
     * @return スキャナ内カウント
     */
    public int getCount() {
        return count;
    }
    
    // ------------------------ Implement Methods

    public IToken evaluate(ICharacterScanner scanner) {
        int c = scanner.read();
        if (c == '$') {
            this.scanner = scanner;
            return doEvaluate();
        }
        scanner.unread();
        return Token.UNDEFINED;
    }
    
    // ------------------------ Private Methods

    /**
     * 要素を検出します。
     * @param scanner スキャナ
     * @return 検出されたトークン
     */
    private IToken doEvaluate() {
        MultiTextAttribute attr = new MultiTextAttribute();

        // 括弧閉じが必要な場合は真にする
        boolean useBrace = false;

        // 変数解析が始まったら真にする
        boolean start = false;

        // 既に $ を読み出しているので初期カウントは1
        count = 1;

        while (true) {
            int c = readScanner();

            if (!start) {
                if (c == '!') {
                    continue;
                } else if (c == '{') {
                    useBrace = true;
                    start = true;
                    continue;
                } else {
                    start = true;
                }
            }

            if (c == ICharacterScanner.EOF) {
                break;
            }

            if (start) {
                if (isVariableCharacter(c)) {
                    // do nothing
                } else {
                    if (!useBrace) {
                        unreadScanner();
                        attr.add(propertyToken, count);
                        return new Token(attr);
                    } else {
                        if (c == '}') {
                            attr.add(propertyToken, count);
                            return new Token(attr);
                        } else {
                            unreadScanner();
                            attr.add(propertyToken, count);
                            return new Token(attr);
                        }
                    }
                }
            }
            
        }

        for (int i = 0; i < count; i++) {
            scanner.unread();
        }
        return Token.UNDEFINED;
    }

    /**
     * 文字が変数文字列として有効かどうかを返します。
     * @param c 文字
     * @return 文字が変数文字列として有効ならば真
     */
    private boolean isVariableCharacter(int c) {
        return Character.isLetterOrDigit(c) || ("-_.".indexOf(c) >= 0);
    }

    /**
     * スキャナから一文字取得します。
     * @param scanner 
     * @return 取得した文字
     */
    private int readScanner() {
        ++count;
        return scanner.read();
    }

    /**
     * スキャナから一文字戻します。
     * @param scanner 
     */
    private void unreadScanner() {
        --count;
        scanner.unread();
    }

}
