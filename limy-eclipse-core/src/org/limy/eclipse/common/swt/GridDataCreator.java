/*
 * Created 2007/01/08
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
package org.limy.eclipse.common.swt;

import org.eclipse.swt.layout.GridData;

/**
 * GridData作成用のユーティリティクラスです。
 * @author Naoki Iwami
 */
public final class GridDataCreator {
    
    /**
     * private constructor
     */
    private GridDataCreator() { }

    /**
     * @return
     */
    public static GridData create() {
        return new GridData();
    }

    /**
     * @return
     */
    public static GridData createFillHorizontal() {
        return new GridData(GridData.FILL_HORIZONTAL);
    }

    /**
     * @return
     */
    public static GridData createFillHorizontal(int horizontalAlignment) {
        GridData data = new GridData(GridData.FILL_HORIZONTAL);
        data.horizontalAlignment = horizontalAlignment;
        return data;
    }

    /**
     * @return
     */
    public static GridData createFillVertical() {
        return new GridData(GridData.FILL_VERTICAL);
    }

    /**
     * @return
     */
    public static GridData createFillBoth() {
        return new GridData(GridData.FILL_BOTH);
    }

    /**
     * @return
     */
    public static GridData createFillGrab() {
        GridData data = createFillBoth();
        data.grabExcessHorizontalSpace = true;
        data.grabExcessVerticalSpace = true;
        return data;
    }

    public static GridData createHorizontalSpan(int hirozontalSpan) {
        GridData gridData = create();
        gridData.horizontalSpan = hirozontalSpan;
        return gridData;
    }

    public static GridData createHorizontalSpan(int hirozontalSpan, int horizontalAlignment) {
        GridData data = createHorizontalSpan(hirozontalSpan);
        data.horizontalAlignment = horizontalAlignment;
        return data;
    }

}
