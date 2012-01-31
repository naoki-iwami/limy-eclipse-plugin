/*
 * Created 2007/02/18
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
package org.limy.eclipse.qalab.umlgraph.javadoc.impl;

/**
 * 文字列とポジションを持つクラスです。
 * @author Naoki Iwami
 */
public class StrAndPos {
    
    /** 文字列 */
    private final String str;
    
    /** ポジション */
    private final int pos;

    /**
     * StrAndPos インスタンスを構築します。
     * @param str 文字列
     * @param pos ポジション
     */
    public StrAndPos(String str, int pos) {
        super();
        this.str = str;
        this.pos = pos;
    }

    // ------------------------ Getter/Setter Methods

    /**
     * 文字列を取得します。
     * @return 文字列
     */
    public String getStr() {
        return str;
    }

    /**
     * ポジションを取得します。
     * @return ポジション
     */
    public int getPos() {
        return pos;
    }

}
