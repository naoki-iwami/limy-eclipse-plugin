/*
 * Created 2003/11/01
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
package org.limy.eclipse.common;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * 全文を行ごとに処理するイテレータクラスです。
 * @author Naoki Iwami
 */
public class LimyLineIterator implements Iterator<String> {

    // ------------------------ Fields

    /**
     * 全文情報
     */
    private String lines;
    
    /**
     * 現在ポジション
     */
    private int pos;
    
    // ------------------------ Constructors

    /**
     * LimyLineIteratorインスタンスを構築します。
     * @param lines 全文情報
     */
    public LimyLineIterator(String lines) {
        this.lines = lines;
    }
    
    // ------------------------ Implement Methods

    public void remove() {
        // empty
    }

    public boolean hasNext() {
        return pos < lines.length();
    }

    public String next() {
        if (pos >= lines.length()) {
            throw new NoSuchElementException();
        }
        return nextLine();
    }
    
    // ------------------------ Public Methods

    /**
     * 次の行を取得します。
     * @return 1行文字列
     */
    public String nextLine() {
        String r;
        int index = lines.indexOf(10, pos);
        if (index < 0) {
            r = lines.substring(pos);
            pos = lines.length();
        } else {
            if (index > 0 && lines.charAt(index - 1) == 13) {
                r = lines.substring(pos, index - 1);
                pos = index + 1;
            } else {
                r = lines.substring(pos, index);
                pos = index + 1;
            }
        }
        return r;
    }

    /**
     * 現在ポジションを取得します。
     * @return 現在ポジション
     */
    public int getPos() {
        return pos;
    }

}
