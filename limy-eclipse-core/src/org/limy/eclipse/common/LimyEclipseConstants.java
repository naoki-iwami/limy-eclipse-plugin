/*
 * Created 2003/11/24
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

/**
 * Limy Eclipse全般で使用する定数クラスです。
 * @author Naoki Iwami
 */
public final class LimyEclipseConstants {
    
    // ------------------------ Constants

    /** プラグインID */
    public static final String PLUGIN_ID = "org.limy.eclipse.core";
    /**
     * LimyEclipseエラーコード
     */
    public static final int INTERNAL_ERROR = 150;

    /**
     * 改行文字
     */
    public static final char NL = '\n';

    /**
     * 空文字列
     */
    public static final String EMPTY_STR = "";

    /**
     * Preferenceキー : 背景色
     */
    public static final String P_BGCOLOR = "colorBackground";

    /**
     * Preferenceキー : 外部ブラウザ
     */
    public static final String P_BROWSER_PATH = "browserPath";

    // ------------------------ Constructors

    /**
     * private constructor
     */
    private LimyEclipseConstants() { }

}
