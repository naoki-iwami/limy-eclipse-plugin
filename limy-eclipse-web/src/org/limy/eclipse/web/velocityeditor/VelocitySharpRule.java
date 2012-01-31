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
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.Token;
import org.limy.eclipse.common.jface.MultiTextAttribute;

/**
 * Velocity(#記号)の検出ルールを定めたクラスです。
 * @author Naoki Iwami
 */
public class VelocitySharpRule extends AbstractDollarRule {

    /**
     * 検出内モードを表す列挙型です。
     */
    enum Mode {
        /** 初期モード */
        DEFAULT,
        /** 単行コメントモード */
        SINGLE_COMMENT,
        /** 複数行コメントモード */
        MULTI_COMMENT,
        /** キーワード内部モード */
        KEYWORD_INNER;
    }

    /**
     * 括弧無キーワード一覧
     */
    private static final String[] KEYWORDS = new String[] {
        "else",
        "end",
    };

    // ------------------------ Fields

    /**
     * コメント文字列の属性
     */
    private TextAttribute commentToken;

    /**
     * キーワード文字列の属性
     */
    private TextAttribute keywordToken;

    /**
     * キーワード内部文字列の属性
     */
    private TextAttribute keywordInnerToken;

    // ------------------------ Constructors

    /**
     * VelocitySharpRuleインスタンスを構築します。
     * @param commentToken
     * @param keywordToken
     * @param keywordInnerToken
     * @param propertyToken
     */
    public VelocitySharpRule(TextAttribute commentToken, TextAttribute keywordToken,
            TextAttribute keywordInnerToken, TextAttribute propertyToken) {
        
        super(new VelocityDollarRule(propertyToken));
        
        this.commentToken = commentToken;
        this.keywordToken = keywordToken;
        this.keywordInnerToken = keywordInnerToken;
    }
    
    // ------------------------ Implement Methods

    public IToken evaluate(ICharacterScanner scanner) {
        int c = scanner.read();
        if (c == '#') {
            setScanner(scanner);
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
        StringBuffer buff = new StringBuffer();
        Mode mode = Mode.DEFAULT;

        // キーワードカウンタ
        int charCount = 0;

        // 括弧数(左括弧 - 右括弧)
        int braceCount = 0;

        // 既に # を読み出しているので初期カウントは1
        setCount(1);

        while (true) {
            int c = readScanner();

            mode = getAfterMode(c, mode);

            if (mode == Mode.SINGLE_COMMENT) {
                // 1行コメント
                if (c == ICharacterScanner.EOF || c == '\n' || c == '\r') {
                    attr.add(commentToken, getCount());
                    return new Token(attr);
                }
            } else if (mode == Mode.MULTI_COMMENT) {
                // 複数行コメント
                switch (charCount) {
                case 0:
                    if (c == '*') {
                        charCount = 1;
                    }
                    break;
                case 1:
                    if (c == '#') {
                        attr.add(commentToken, getCount());
                        return new Token(attr);
                    }
                    charCount = 0;
                    break;
                default:
                    break;
                }
            } else if (mode == Mode.DEFAULT) {
                // キーワードより前
                if (c == '(') {
                    // ( の前までを登録
                    attr.add(keywordToken, getCount() - 1);
                    ++braceCount;
                    mode = Mode.KEYWORD_INNER;
                } else {
                    buff.append((char)c);
                    for (String keyword : KEYWORDS) {
                        if (keyword.length() == buff.length()
                            && keyword.equals(buff.toString())) {
                            // 括弧無のキーワードが検出されたらその場で解析終了
                            attr.add(keywordToken, getCount());
                            return new Token(attr);
                        }
                    }
                }
            } else /* if (mode == Mode.KEYWORD_INNER) */ {
                // キーワード内部
                if (c == '(') {
                    ++braceCount;
                } else if (c == ')') {
                    --braceCount;
                    if (braceCount == 0) {
                        // 括弧が閉じ終わったら解析終了
                        attr.add(keywordInnerToken, getCount());
                        return new Token(attr);
                    }
                } else {
                    if (c == '$') {
                        // 解析中に$記号が発見されたら、$RULEに処理を委ねる
                        evaluateDollar(attr);
                    }
                }
            }
            if (c == ICharacterScanner.EOF) {
                unreadAll();
                return Token.UNDEFINED;
            }
        }
    }

    /**
     * 文字によってモードを変更して返します。
     * @param c 文字
     * @param mode 現在のモード
     * @return 変更後のモード
     */
    private Mode getAfterMode(int c, Mode mode) {
        if (getCount() == 2) {
            if (c == '#') {
                return Mode.SINGLE_COMMENT;
            }
            if (c == '*') {
                return Mode.MULTI_COMMENT;
            }
        }
        return mode;
    }

    /**
     * $文字列を解析します。
     * @param attr テキスト属性
     */
    private void evaluateDollar(MultiTextAttribute attr) {
        // $ の前までを登録
        attr.add(keywordInnerToken, getCount() - 1);
        doEvaluateDollar(attr);
    }
    
}
