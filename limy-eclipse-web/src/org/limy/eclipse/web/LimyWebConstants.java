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
package org.limy.eclipse.web;

/**
 * Limy Eclipse Web Plugin 用定数クラスです。
 * @author Naoki Iwami
 */
public final class LimyWebConstants {

    /**
     * Velocityプレビュー用ファイルパス
     */
    public static final String CONTENT_PREVIEW = "template/preview_velocity.vm";
    /**
     * Preferenceキー : コメント色
     */
    public static final String P_COLOR_COMMENT = "colorWebComment";
    /**
     * Preferenceキー : キーワード色
     */
    public static final String P_COLOR_KEYWORD = "colorWebKeyword";
    /**
     * Preferenceキー : キーワード内部色
     */
    public static final String P_COLOR_INNER = "colorWebKeywordInner";
    /**
     * Preferenceキー : プロパティ色
     */
    public static final String P_COLOR_PROPERTY = "colorWebProperty";
    /**
     * Preferenceキー : タグ色
     */
    public static final String P_COLOR_TAG = "colorWebTag";
    /**
     * Preferenceキー : Folding有効／無効
     */
    public static final String P_FOLDING_ENABLE = "webFoldingEnable";
    /**
     * Preferenceキー : 関数Foldingの有効／無効
     */
    public static final String P_FOLDING = "webFoldingFunctions";

    /**
     * private constructor
     */
    private LimyWebConstants() { }
    
}
