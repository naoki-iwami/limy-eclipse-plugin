/*
 * Created 2007/01/15
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

import org.eclipse.jface.text.rules.ICharacterScanner;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.Token;
import org.limy.eclipse.common.jface.MultiTextAttribute;

/**
 *
 * @author Naoki Iwami
 */
public abstract class AbstractDollarRule implements IRule {

    // ------------------------ Fields

    /**
     * スキャナ
     */
    private ICharacterScanner scanner;
    
    /**
     * $記号の検出ルール
     */
    private VelocityDollarRule dollarRule;

    /**
     * スキャナ内カウント
     */
    private int count;

    // ------------------------ Constructors

    /**
     * AbstractDollarRuleインスタンスを構築します。
     * @param dollarRule $記号の検出ルール
     */
    public AbstractDollarRule(VelocityDollarRule dollarRule) {
        
        super();
        this.dollarRule = dollarRule;
    }
    
    // ------------------------ Protected Methods

    /**
     * スキャナから一文字取得します。
     * @param scanner 
     * @return 取得した文字
     */
    protected int readScanner() {
        ++count;
        return scanner.read();
    }

    /**
     * スキャナから全ての文字を戻します。
     */
    protected void unreadAll() {
        for (int i = 0; i < getCount(); i++) {
            scanner.unread();
        }
    }

    /**
     * $文字以降の解析を行います。
     * @param attr 
     */
    protected void doEvaluateDollar(MultiTextAttribute attr) {
        scanner.unread();
        IToken result = dollarRule.evaluate(scanner);
        if (result == Token.UNDEFINED) {
            // do nothing
            scanner.read();
        } else {
            attr.addMulti((MultiTextAttribute)result.getData(), 0);
            count += dollarRule.getCount() - 1;
        }

    }
    
    // ------------------------ Getter/Setter Methods

    /**
     * スキャナを取得します。
     * @return スキャナ
     */
    protected ICharacterScanner getScanner() {
        return scanner;
    }

    /**
     * スキャナを設定します。
     * @param scanner スキャナ
     */
    protected void setScanner(ICharacterScanner scanner) {
        this.scanner = scanner;
    }

    /**
     * スキャナ内カウントを取得します。
     * @return スキャナ内カウント
     */
    protected int getCount() {
        return count;
    }

    /**
     * スキャナ内カウントを設定します。
     * @param count スキャナ内カウント
     */
    protected void setCount(int count) {
        this.count = count;
    }
    
}
