/*
 * Created 2007/02/27
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
package org.limy.eclipse.qalab.outline;

import java.awt.geom.Point2D;
import java.io.File;
import java.util.Collection;

/**
 * ポップアップイメージ用のインターフェイスです。
 * @author Naoki Iwami
 */
public interface PopupImage {

    /**
     * イメージファイルを返します。
     * @return
     */
    File getImageFile();
    
    /**
     * SVGファイルを取得します。
     * @return SVGファイル
     */
    File getSvgImageFile();

    /**
     * クリック可能な要素一覧を返します。
     * @return クリック可能な要素一覧
     */
    Collection<? extends ClickablePointInfo> getClickableElements();
    
    /**
     * 指定された座標に対応する要素を返します。
     * @param point 座標
     * @return 要素
     */
    ClickablePointInfo getElement(Point2D.Double point);

    /**
     * 水平方向表示フラグを取得します。
     * @return 水平方向表示フラグ
     */
    boolean isHorizontal();
    
    /**
     * 汎用値を取得します。
     * @param key キー
     * @return 値
     */
    Object getParam(String key);

    /**
     * 汎用値を設定します。
     * @param key キー
     * @param value 値
     */
    void setParam(String key, Object value);
    
    /**
     * 水平方向表示フラグを設定します。
     * @param horizontal 水平方向表示フラグ
     */
    void setHorizontal(boolean horizontal);

}
