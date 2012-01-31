/*
 * Created 2007/02/28
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

import java.awt.geom.Point2D.Double;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * ポップアップイメージの基底クラスです。
 * @author Naoki Iwami
 */
public class BasePopupImage implements PopupImage {

    // ------------------------ Fields

    /** 水平方向表示フラグ */
    private boolean horizontal;

    /** 画像ファイル */
    private File imageFile;

    /** SVGファイル */
    private File svgImageFile;
    
    /** クリック可能な要素一覧 */
    private Collection<? extends ClickablePointInfo> elements = new ArrayList<ClickablePointInfo>();
    
    /** パラメータ一覧 */
    private Map<String, Object> params = new HashMap<String, Object>();
    
    // ------------------------ Implement Methods

    public Collection<? extends ClickablePointInfo> getClickableElements() {
        return elements;
    }

    public File getImageFile() {
        return imageFile;
    }

    public File getSvgImageFile() {
        return svgImageFile;
    }

    public boolean isHorizontal() {
        return horizontal;
    }

    public Object getParam(String key) {
        return params.get(key);
    }
    
    public void setParam(String key, Object value) {
        params.put(key, value);
    }

    public ClickablePointInfo getElement(Double point) {
        for (ClickablePointInfo el : elements) {
            if (el.getRect().contains(point)) {
                return el;
            }
        }
        return null;
    }
    
    // ------------------------ Public Methods

    /**
     * SVGファイルを設定します。
     * @param svgImageFile SVGファイル
     */
    public void setSvgImageFile(File svgImageFile) {
        this.svgImageFile = svgImageFile;
    }

    /**
     * 画像ファイルを設定します。
     * @param imageFile 画像ファイル
     */
    public void setImageFile(File imageFile) {
        this.imageFile = imageFile;
    }

    /**
     * クリック可能な要素一覧を設定します。
     * @param elements クリック可能な要素一覧
     */
    public void setElements(Collection<? extends ClickablePointInfo> elements) {
        this.elements = elements;
    }

    /**
     * 水平方向表示フラグを設定します。
     * @param horizontal 水平方向表示フラグ
     */
    public void setHorizontal(boolean horizontal) {
        this.horizontal = horizontal;
    }

}
