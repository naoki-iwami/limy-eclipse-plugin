/*
 * Created 2005/09/19
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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.dialogs.PropertyPage;

/**
 * Propertyページの基底クラスです。
 * @author Naoki Iwami
 */
public abstract class AbstractLimyPropertyPage extends PropertyPage {
    
    // ------------------------ Fields

    /** フィールド一覧 */
    private List<FieldEditor> fields = new ArrayList<FieldEditor>();
    
    // ------------------------ Override Methods

    @Override
    public boolean performOk() {
        for (Iterator<FieldEditor> it = fields.iterator(); it.hasNext();) {
            FieldEditor editor = it.next();
            editor.store();
        }
        return super.performOk();
    }
    
    protected void performDefaults() {
        for (Iterator<FieldEditor> it = fields.iterator(); it.hasNext();) {
            FieldEditor editor = it.next();
            editor.loadDefault();
        }
        super.performDefaults();
    }
    
    // ------------------------ Abstract Methods
    
    protected abstract Control createContents(Composite parent);

    // ------------------------ Protected Methods
    
    /**
     * フィールドを初期化します。
     * @param field フィールド
     */
    protected void initField(FieldEditor field) {
        field.setPreferenceStore(getPreferenceStore());
        field.setPage(this);
        field.load();
        
        fields.add(field);
    }

    /**
     * コントロールのレイアウトを設定します。
     * @param comp
     * @param column
     */
    protected void setControlLayout(Composite comp, int column) {
        GridData gd = new GridData(GridData.FILL_HORIZONTAL);
        gd.horizontalSpan = column;
        comp.setLayoutData(gd);
    }

}
