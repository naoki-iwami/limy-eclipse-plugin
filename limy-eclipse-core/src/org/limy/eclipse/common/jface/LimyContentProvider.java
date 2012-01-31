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

import java.util.List;
import java.util.Set;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

/**
 * SWTテーブル用コンテンツプロバイダクラスです。
 * @author Naoki Iwami
 */
public class LimyContentProvider implements IStructuredContentProvider {
    
    // ------------------------ Implement Methods

    public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
        // empty
    }

    public void dispose() {
        // empty
    }

    public Object[] getElements(Object inputElement) {
        Object[] r = null;
        if (inputElement != null) {
            if (inputElement instanceof List) {
                r = ((List<Object>)inputElement).toArray();
            }
            if (inputElement instanceof Set) {
                r = ((Set<Object>)inputElement).toArray();
            }
        }
        return r;
    }
}