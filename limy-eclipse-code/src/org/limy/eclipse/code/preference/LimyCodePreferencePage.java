/*
 * Created 2005/09/14
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

import java.io.File;

import org.eclipse.jface.preference.FileFieldEditor;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.limy.eclipse.code.LimyCodeConstants;
import org.limy.eclipse.code.LimyCodePlugin;
import org.limy.eclipse.common.jface.AbstractLimyPreferencePage;
import org.limy.eclipse.common.swt.GridDataCreator;
import org.limy.eclipse.common.swt.LimySwtUtils;

/**
 * Limy Eclipse Code Plugin の PreferencePage です。
 * @author Naoki Iwami
 */
public class LimyCodePreferencePage extends AbstractLimyPreferencePage {
    
    // ------------------------ Constructors

    /**
     * LimyCodePreferencePageインスタンスを構築します。
     * @param store
     */
    public LimyCodePreferencePage() {
        super(LimyCodePlugin.getDefault().getPreferenceStore());
        initializeDefaults();
    }

    // ------------------------ Override Methods

    @Override
    protected Control createContents(Composite parent) {
        System.out.println(parent.getLayout());
        Composite comp = new Composite(parent, SWT.NONE);
        comp.setLayout(new GridLayout());
        
        FileFieldEditor gnuHeaderFile = new FileFieldEditor(
                LimyCodeConstants.PREF_JAVA_HEADER,
                "Java Header Text File : ",
                comp);
        gnuHeaderFile.setFileExtensions(new String[] { "*.txt" });
        
        FileFieldEditor javadocProp = new FileFieldEditor(
                LimyCodeConstants.PREF_PROP_PATH,
                "Javadoc Comment File : ",
                comp);
        
        initField(gnuHeaderFile);
        initField(javadocProp);

        Button button = new Button(comp, SWT.NONE);
        button.setText("&Edit...");
        button.setLayoutData(GridDataCreator.createHorizontalSpan(3, SWT.RIGHT));
        button.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                String path = LimyCodePlugin.getDefault().getPreferenceStore().getString(
                        LimyCodeConstants.PREF_PROP_PATH);
                if (path != null && path.length() > 0) {
                    JavadocPropDialog dialog = new JavadocPropDialog(getShell(), new File(path));
                    dialog.open();
                } else {
                    LimySwtUtils.showAlertDialog(LimyCodePlugin.getResourceString(
                            "not.specified.javadoc.file"));
                }
            }
        });

        Composite comp2 = new Composite(parent, SWT.NONE);
        comp2.setLayout(new GridLayout());
        comp2.setLayoutData(GridDataCreator.createFillGrab());

        StringFieldEditor getter = new StringFieldEditor(LimyCodeConstants.PREF_GETTER_DESC,
                "Getter-method Description : ",
                comp2);

        StringFieldEditor setter = new StringFieldEditor(LimyCodeConstants.PREF_SETTER_DESC,
                "Setter-method Description : ",
                comp2);
        
        initField(getter);
        initField(setter);

        return comp;
    }

    // ------------------------ Private Methods

    /**
     * デフォルト値を初期化します。
     */
    private void initializeDefaults() {
        IPreferenceStore store = getPreferenceStore();
        store.setDefault(LimyCodeConstants.PREF_GETTER_DESC, "を取得します。");
        store.setDefault(LimyCodeConstants.PREF_SETTER_DESC, "を設定します。");
    }

}
