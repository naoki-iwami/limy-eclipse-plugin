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
package org.limy.eclipse.core;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.limy.eclipse.common.LimyEclipseConstants;
import org.limy.eclipse.common.jface.AbstractLimyPreferencePage;
import org.limy.eclipse.common.jface.LimyColorFieldEditor;

/**
 * Limy Eclipse Core Plugin の PreferencePage です。
 * @author Naoki Iwami
 */
public class LimyPreferencePage extends AbstractLimyPreferencePage {
    
    // ------------------------ Constants

    /**
     * エディタ背景色
     */
    private LimyColorFieldEditor bgColorField;
    
    // ------------------------ Constructors

    /**
     * LimyPreferencePageインスタンスを構築します。
     * @param store 
     */
    public LimyPreferencePage() {
        super(LimyEclipsePlugin.getDefault().getPreferenceStore());
        initializeDefaults();
    }

    // ------------------------ Implement Methods
    
    protected Control createContents(Composite parent) {
        Composite composite = new Composite(parent, SWT.NULL);
        
        bgColorField = new LimyColorFieldEditor(
                LimyEclipseConstants.P_BGCOLOR,
                "Editor Background &Color : ",
                composite);
        initColorField(bgColorField);

        return composite;
    }
   
    // -------------------------------- Private Methods
    
    /**
     * デフォルト値を初期化します。
     */
    private void initializeDefaults() {
        IPreferenceStore store = getPreferenceStore();
        store.setDefault(LimyEclipseConstants.P_BGCOLOR, "240,255,240");
        store.setDefault(LimyEclipseConstants.P_BROWSER_PATH, "");
        
    }

}