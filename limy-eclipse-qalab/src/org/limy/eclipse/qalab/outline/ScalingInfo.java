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

import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;

/**
 * スケーリング情報を表します。
 * @author Naoki Iwami
 */
public class ScalingInfo {

    // ------------------------ Fields

    /** scale情報 */
    private Point2D.Double scale;
    
    /** translate情報 */
    private Point2D.Double translate;

    // ------------------------ Public Methods

    /**
     * 位置情報をスケーリング調整します。
     * @param point1 位置情報1
     * @param point2 位置情報2
     */
    public void adjust(Double point1, Double point2) {
        point1.x = (point1.x + translate.x) * scale.x;
        point1.y = (point1.y + translate.y) * scale.y;
        point2.x = (point2.x + translate.x) * scale.x;
        point2.y = (point2.y + translate.y) * scale.y;
    }

    // ------------------------ Getter/Setter Methods

    /**
     * scale情報を取得します。
     * @return scale情報
     */
    public Point2D.Double getScale() {
        return scale;
    }

    /**
     * scale情報を設定します。
     * @param scale scale情報
     */
    public void setScale(Point2D.Double scale) {
        this.scale = scale;
    }

    /**
     * translate情報を取得します。
     * @return translate情報
     */
    public Point2D.Double getTranslate() {
        return translate;
    }

    /**
     * translate情報を設定します。
     * @param translate translate情報
     */
    public void setTranslate(Point2D.Double translate) {
        this.translate = translate;
    }

}
