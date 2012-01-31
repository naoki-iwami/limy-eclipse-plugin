/*
 * Created 2004/11/16
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
package org.limy.eclipse.common.jface;

import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.graphics.Image;

/**
 * SWTテーブル用ラベルプロバイダクラスです。
 * @author Naoki Iwami
 */
public class TableLabelProvider implements ITableLabelProvider {
    
    // ------------------------ Implement Methods
    
    public Image getColumnImage(Object element, int columnIndex) {
        if (element instanceof ITableObjectImage) {
            ITableObjectImage object = (ITableObjectImage)element;
            return object.getImage(columnIndex);
        }
        return null;
    }

    public String getColumnText(Object element, int columnIndex) {
        if (element instanceof ITableObject) {
            ITableObject object = (ITableObject) element;
            return object.getViewString(columnIndex);
        }
        return null;
    }

    public void addListener(ILabelProviderListener listener) {
        // empty
    }

    public void dispose() {
        // empty
    }

    public boolean isLabelProperty(Object element, String property) {
        return false;
    }

    public void removeListener(ILabelProviderListener listener) {
        // empty
    }
}