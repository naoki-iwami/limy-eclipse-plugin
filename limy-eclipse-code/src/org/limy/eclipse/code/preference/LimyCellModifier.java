/*
 * Created 2005/07/21
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

import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.swt.widgets.TableItem;
import org.limy.eclipse.common.jface.ITableObject;

/**
 * セル変更インターフェイスの汎用実装クラスです。
 * @author Naoki Iwami
 */
public class LimyCellModifier implements ICellModifier {
    
    /**
     * セルターゲット
     */
    private LimyModifierTarget target;
    
    /**
     * LimyCellModifierインスタンスを構築します。
     * @param target セルターゲット
     */
    public LimyCellModifier(LimyModifierTarget target) {
        this.target = target;
        
    }

    public boolean canModify(Object element, String property) {
        
        boolean b = true;
        
        ITableObject object = (ITableObject)element;
        int columnIndex = Integer.parseInt(property);
        if (columnIndex == 1/*Class Name*/
                && ((Integer)object.getValue(0/*type*/)).intValue() >= 2/*Primitive*/) {
            b = false;
        }
        if (columnIndex == 2/*Field Name*/
                && ((Integer)object.getValue(0/*type*/)).intValue() == 1/*Exception*/) {
            b = false;
        }
        
        return b;
    }

    public Object getValue(Object element, String property) {
        ITableObject item = (ITableObject)element;
        int columnIndex = Integer.parseInt(property);
        return item.getValue(columnIndex);
    }

    public void modify(Object element, String property, Object value) {

        ITableObject object = (ITableObject)((TableItem)element).getData();

        int columnIndex = Integer.parseInt(property);
        
        Object oldValue = object.getValue(columnIndex);
        if ((oldValue == null && value != null) || (oldValue != null && !oldValue.equals(value))) {
            object.setValue(columnIndex, value);
            target.update(object, null);
            target.setModified(true);
        }
    }

}
