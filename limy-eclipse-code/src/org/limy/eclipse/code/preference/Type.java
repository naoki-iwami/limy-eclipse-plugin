/*
 * Created 2007/08/21
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
package org.limy.eclipse.code.preference;

import java.util.ArrayList;
import java.util.List;

/**
 * パラメータ種別を表します。
 * @author Naoki Iwami
 */
/* package */ enum Type {
    
    /** クラス */
    CLAZZ("Class", 0),
    /** 例外 */
    EXCEPTION("Exception", 1),
    /** byte */
    BYTE("byte", 2),
    /** short */
    SHORT("short", 3),
    /** int */
    INT("int", 4),
    /** long */
    LONG("long", 5),
    /** char */
    CHAR("char", 6),
    /** float */
    FLOAT("float", 7),
    /** double */
    DOUBLE("double", 8);
    
    // ------------------------ Fields
    
    /**
     * 表示文字列
     */
    private final String str;
    
    /**
     * 数値（コンボボックス用）
     */
    private final int number;
    
    /**
     * @param str 表示文字列
     * @param number 数値（コンボボックス用）
     */
    private Type(String str, int number) {
        this.str = str;
        this.number = number;
    }
    
    /**
     * パラメータ種別を取得します。
     * @param number 数値（コンボボックス用）
     * @return パラメータ種別
     */
    public static Type getType(int number) {
        for (Type type : Type.values()) {
            if (type.getValue() == number) {
                return type;
            }
        }
        return null;
    }
    
    /**
     * 数値を取得します。
     * @return 数値
     */
    public int getValue() {
        return number;
    }
    
    /**
     * 表示文字列を取得します。
     * @return 表示文字列
     */
    public String getString() {
        return str;
    }
    
    /**
     * 表示文字列一覧を取得します。
     * @return 表示文字列一覧
     */
    public static String[] getStrings() {
        List<String> list = new ArrayList<String>();
        for (Type type : Type.values()) {
            list.add(type.getString());
        }
        return list.toArray(new String[list.size()]);
    }
    
}