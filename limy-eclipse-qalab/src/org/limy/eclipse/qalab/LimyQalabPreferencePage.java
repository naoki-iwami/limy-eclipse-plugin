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
package org.limy.eclipse.qalab;

import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FileFieldEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Link;
import org.limy.eclipse.common.LimyEclipsePluginUtils;
import org.limy.eclipse.common.jface.AbstractLimyPreferencePage;
import org.limy.eclipse.common.swt.GridDataCreator;
import org.limy.eclipse.common.ui.LimyUIUtils;
import org.limy.eclipse.core.LimyEclipsePlugin;
import org.limy.eclipse.qalab.common.LimyQalabConstants;

/**
 *
 * @author Naoki Iwami
 */
public class LimyQalabPreferencePage extends AbstractLimyPreferencePage {
    
    /**
     * dot.exeファイル
     */
    private FileFieldEditor dotExeFile;
    
    // ------------------------ Constructors

    /**
     * LimyQalabPreferencePageインスタンスを構築します。
     * @param store
     */
    public LimyQalabPreferencePage() {
        super(LimyEclipsePlugin.getDefault().getPreferenceStore());
    }

    // ------------------------ Override Methods

    @Override
    protected Control createContents(Composite parent) {
        Composite comp = new Composite(parent, SWT.NULL);
        comp.setLayout(new GridLayout());
        
        dotExeFile = new FileFieldEditor(
                LimyQalabConstants.KEY_DOT_EXE,
                "dot.exe : ",
                comp);
        dotExeFile.setFileExtensions(new String[] { "*.exe" });
        
        Link link = new Link(comp, SWT.NONE);
        link.setText("<a>Go to Graphviz HomePage</a>");
        link.setLayoutData(GridDataCreator.createHorizontalSpan(3, SWT.RIGHT));
        link.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent evt) {
                try {
                    LimyUIUtils.openBrowser(new URL("http://www.graphviz.org/"));
                } catch (MalformedURLException e) {
                    LimyEclipsePluginUtils.log(e);
                }            
            }
        });
        initField(dotExeFile);
        
        Composite subComp = new Composite(comp, SWT.NULL);
        subComp.setLayoutData(GridDataCreator.createHorizontalSpan(3, SWT.LEFT));
        GridData gd = new GridData(GridData.FILL_HORIZONTAL);
        gd.horizontalSpan = 3;
        gd.horizontalAlignment = SWT.LEFT;
        subComp.setLayoutData(gd);

        BooleanFieldEditor adjustScale = new BooleanFieldEditor(
                LimyQalabConstants.ADJUST_SCALING,
                Messages.LABEL_ADJUST_SCALING,
                subComp);

        initField(adjustScale);

        return comp;
    }

}
