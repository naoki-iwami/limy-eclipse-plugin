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
 * HTMLタグの検出ルールを定めたクラスです。
 * @author Naoki Iwami
 */
public class HtmlTagRule extends AbstractDollarRule {

    // ------------------------ Constants

    /**
     * スキップ文字
     */
    private static final String SKIP_CHARACTER = " \t\n\r";

    // ------------------------ Fields

    /**
     * タグの属性
     */
    private TextAttribute tagToken;

    /**
     * タグ文字列の属性
     */
    private TextAttribute keywordToken;
    
    // ------------------------ Constructors

    /**
     * HtmlTagRuleインスタンスを構築します。
     * @param tagToken
     * @param keywordToken
     * @param propertyToken 
     */
    public HtmlTagRule(
            TextAttribute tagToken, TextAttribute keywordToken,
            TextAttribute propertyToken) {
        
        super(new VelocityDollarRule(propertyToken));

        this.tagToken = tagToken;
        this.keywordToken = keywordToken;
    }
    
    // ------------------------ Implement Methods

    public IToken evaluate(ICharacterScanner scanner) {
        int c = scanner.read();
        if (c == '<') {
            setScanner(scanner);
            return doEvaluate();
        }
        scanner.unread();
        return Token.UNDEFINED;
    }
    
    // ------------------------ Private Methods

    /**
     * 要素を検出します。
     * @return 検出されたトークン
     */
    private IToken doEvaluate() {
        MultiTextAttribute attr = new MultiTextAttribute();

        // タグ内部解析が始まったら真にする（基本的にはループ初回にすぐに真になるが、</abc>形式の場合はaの時点で真になる）
        boolean start = false;

        // タグ名解析が終わったら真にする
        boolean inner = false;

        // 既に < を読み出しているので初期カウントは1
        setCount(1);

        while (true) {
            int c = readScanner();

            // タグ解析初回のみ通るルーチン
            if (!start) {
                if (c == '/') {
                    // do nothing
                } else {
                    start = true;
                    attr.add(tagToken, getCount() - 1); // < をタグ文字として登録
                }
            }

            // 初回ループ時以外は全て通るルーチン
            if (start) {
                if (c == '>') {
                    // タグが終了した場合
                    if (!inner) {
                        // <a> などの場合はここを通る。まずaをキーワードとして登録
                        attr.add(keywordToken, getCount() - 1);
                    } else {
                        // <a href> などの場合はここを通る。まず href は通常文字として登録
                        attr.add((TextAttribute)null, getCount() - 1);
                    }
                    // > をタグ文字として登録して終了
                    attr.add(tagToken, getCount());
                    return new Token(attr);
                } else if (!inner && isSkipCharacter(c)) {
                    // タグ名解析中にスキップ文字が来た場合、直前までをタグ名として登録
                    attr.add(keywordToken, getCount() - 1);
                    inner = true; // タグ解析終了フラグをONに
                } else {
                    // タグ文字解析中、およびタグ以降の文字列解析中はここを通る
                    if (c == '$') {
                        evaluateDollar(attr, inner);
                    }
                    
                }
            }
            
            // EOFが来たら全てをキャンセルして終了
            if (c == ICharacterScanner.EOF) {
                unreadAll();
                return Token.UNDEFINED;
            }
        }
    }

    /**
     * 文字がスキップ文字(スペース、タブ、改行)かどうかを判定します。
     * @param c 文字
     * @return 文字がスキップ文字ならば真
     */
    private boolean isSkipCharacter(int c) {
        return SKIP_CHARACTER.indexOf(c) >= 0;
    }

    /**
     * $文字を解析します。
     * @param attr 文字属性
     * @param inner タグ名解析中ならば真
     */
    private void evaluateDollar(MultiTextAttribute attr, boolean inner) {
        // 解析中に$記号が発見されたら、$RULEに処理を委ねる
        
        if (!inner) {
            // <href$.. などの場合、hrefをキーワードとして登録
            attr.add(keywordToken, getCount() - 1);
        } else {
            // <href aaa $.. などの場合 " aaa " を通常文字として登録
            attr.add((TextAttribute)null, getCount() - 1);
        }

        doEvaluateDollar(attr);
    }

}
