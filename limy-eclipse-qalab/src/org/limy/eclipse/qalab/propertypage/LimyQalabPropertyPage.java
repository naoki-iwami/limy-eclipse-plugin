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
package org.limy.eclipse.qalab.propertypage;

import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.ui.dialogs.PropertyPage;
import org.limy.eclipse.qalab.LimyQalabPluginUtils;
import org.limy.eclipse.qalab.common.LimyQalabEnvironment;
import org.limy.eclipse.qalab.mark.CheckstyleMarkCreator;
import org.limy.eclipse.qalab.mark.PmdCpdMarkCreator;

/**
 * QALab用のプロパティページです。
 * @author Naoki Iwami
 */
public class LimyQalabPropertyPage extends PropertyPage {

    // ------------------------ Fields

    /** Javaプロジェクト */
    private IJavaProject javaProject;
    
    /** ページ一覧 */
    private Collection<StoredComposite> pages = new ArrayList<StoredComposite>();

    // ------------------------ Override Methods

    @Override
    public void setElement(IAdaptable element) {
        super.setElement(element);
        if (element instanceof IJavaProject) {
            javaProject = (IJavaProject)element;
        }
    }

    @Override
    protected Control createContents(Composite parent) {
        
        LimyQalabEnvironment env;
        try {
            env = LimyQalabPluginUtils.createEnv(javaProject.getProject());
        } catch (CoreException e) {
            throw new IllegalStateException(e);
        }
        
        TabFolder comp = new TabFolder(parent, SWT.NONE);
        
        TabItem item1 = new TabItem(comp, SWT.NULL);
        item1.setText("Main");
        StoredComposite page1 = new PageMain(comp, SWT.NONE, env);
        item1.setControl(page1);
        pages.add(page1);

        TabItem item3 = new TabItem(comp, SWT.NULL);
        item3.setText("QALab Items");
        StoredComposite page3 = new PageItem(comp, SWT.NONE, env);
        item3.setControl(page3);
        pages.add(page3);

        TabItem item4 = new TabItem(comp, SWT.NULL);
        item4.setText("Report");
        StoredComposite page4 = new PageReport(this, comp, SWT.NONE, env);
        item4.setControl(page4);
        pages.add(page4);

        TabItem item5 = new TabItem(comp, SWT.NULL);
        item5.setText("Graph");
        StoredComposite page5 = new PageUmlGraph(comp, SWT.NONE, env);
        item5.setControl(page5);
        pages.add(page5);

        TabItem item6 = new TabItem(comp, SWT.NULL);
        item6.setText("Test");
        StoredComposite page6 = new PageTesting(comp, SWT.NONE, env);
        item6.setControl(page6);
        pages.add(page6);

        noDefaultAndApplyButton();
        return comp;
    }

    @Override
    public boolean performOk() {
        for (StoredComposite page : pages) {
            boolean result = page.performOk();
            if (!result) {
                return false;
            }
        }
        
        CheckstyleMarkCreator.getInstance().clearCache();
        PmdCpdMarkCreator.getInstance().clearCache();

        return super.performOk();
    }
    
    

}
