/*
 * Created 2004/12/02
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
package org.limy.eclipse.prop.editor;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.editors.text.TextEditor;
import org.limy.eclipse.common.LimyEclipseConstants;
import org.limy.eclipse.core.LimyEclipsePlugin;

/**
 * プロパティエディタクラスです。
 * @depend - - - PropertyDocumentProvider
 * @author Naoki Iwami
 */
public class PropertyEditor extends TextEditor {
    
    /**
     * PropertyEditorインスタンスを構築します。
     */
    public PropertyEditor() {
        super();
        setDocumentProvider(new PropertyDocumentProvider());
    }
    
    public void createPartControl(Composite parent) {
        super.createPartControl(parent);
        // 背景色をセット
        getSourceViewer().getTextWidget().setBackground(
                LimyEclipsePlugin.getDefault().getColorProvider().getColor(
                    LimyEclipseConstants.P_BGCOLOR));
    }
    
    protected void initializeEditor() {
        super.initializeEditor();
        setSourceViewerConfiguration(new PropertySourceViewerConfiguration(this));
    }
    
    protected void doSetInput(IEditorInput input) throws CoreException {
        super.doSetInput(input);
        setPreferenceStore(LimyEclipsePlugin.getDefault().getPreferenceStore());
    }
    
    protected void handlePreferenceStoreChanged(PropertyChangeEvent event) {
        super.handlePreferenceStoreChanged(event);
        
        ProviderManager.getInstance().updateProviders();
        if (getSourceViewer() instanceof SourceViewer) {
            ((SourceViewer)getSourceViewer()).unconfigure();
            getSourceViewer().configure(new PropertySourceViewerConfiguration(this));
            getSourceViewer().getTextWidget().setBackground(
                    LimyEclipsePlugin.getDefault().getColorProvider().getColor(
                        LimyEclipseConstants.P_BGCOLOR));
            ((SourceViewer)getSourceViewer()).refresh();
        }
    }
    
}
