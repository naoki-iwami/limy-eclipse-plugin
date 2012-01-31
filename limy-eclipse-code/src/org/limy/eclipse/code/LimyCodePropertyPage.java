/*
 * Created 2005/09/17
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
package org.limy.eclipse.code;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ProjectScope;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.preferences.ScopedPreferenceStore;
import org.limy.eclipse.common.jface.AbstractLimyPropertyPage;

/**
 *
 * @author Naoki Iwami
 */
public class LimyCodePropertyPage extends AbstractLimyPropertyPage {

    // ------------------------ Override Methods

    @Override
    protected Control createContents(Composite parent) {
        
        IAdaptable resource = getElement();
        IProject project = (IProject) resource.getAdapter(IProject.class);
        ScopedPreferenceStore store = new ScopedPreferenceStore(new ProjectScope(project), 
                LimyCodePlugin.getDefault().getBundle().getSymbolicName());
        setPreferenceStore(store);

        Composite comp = new Composite(parent, SWT.NONE);
        comp.setLayout(new FormLayout());
        
        initField(new StringFieldEditor(
                LimyCodeConstants.PREF_PROJECT_NAME,
                "Project Name : ", comp));
        
        return comp;
    }
    
}
