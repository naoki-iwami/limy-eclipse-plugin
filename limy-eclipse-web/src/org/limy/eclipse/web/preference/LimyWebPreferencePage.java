/*
 * Created 2006/01/14
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
package org.limy.eclipse.web.preference;

import org.eclipse.jdt.ui.PreferenceConstants;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbench;
import org.limy.eclipse.common.LimyEclipseConstants;
import org.limy.eclipse.common.LimyEclipsePluginUtils;
import org.limy.eclipse.common.jface.AbstractLimyPreferencePage;
import org.limy.eclipse.common.jface.LimyColorFieldEditor;
import org.limy.eclipse.core.LimyEclipsePlugin;
import org.limy.eclipse.web.LimyWebConstants;
import org.limy.eclipse.web.LimyWebPlugin;
import org.limy.eclipse.web.velocityeditor.ProviderManager;
import org.limy.eclipse.web.velocityeditor.VelocityEditor;
import org.limy.eclipse.web.velocityeditor.VelocitySourceViewerConfiguration;

/**
 * WebモジュールのPreferencePageを表すクラスです。
 * @author Naoki Iwami
 */
public class LimyWebPreferencePage extends AbstractLimyPreferencePage {

    // ------------------------ Constants

    /**
     * LimyWebPreferencePageインスタンスを構築します。
     */
    public LimyWebPreferencePage() {
        super(LimyWebPlugin.getDefault().getPreferenceStore());
        setDescription("Web setting page");
        initializeDefaults();
    }
    
    // ------------------------ Override Methods
    
    protected void performDefaults() {
        super.performDefaults();
        
        ProviderManager.getInstance().getVelocityCodeScanner()
                .refreshViewer(getSourceViewer(), getColorFields());

    }

    protected Control createContents(Composite parent) {
        Composite comp = new Composite(parent, SWT.NULL);
        comp.setLayout(new GridLayout());
        
        
        LimyColorFieldEditor commentColor = new LimyColorFieldEditor(
                LimyWebConstants.P_COLOR_COMMENT, "Comment : ", comp);
        LimyColorFieldEditor keywordColor = new LimyColorFieldEditor(
                LimyWebConstants.P_COLOR_KEYWORD, "Keyword : ", comp);
        LimyColorFieldEditor innerColor = new LimyColorFieldEditor(
                LimyWebConstants.P_COLOR_INNER, "Keyword Inner : ", comp);
        LimyColorFieldEditor propertyColor = new LimyColorFieldEditor(
                LimyWebConstants.P_COLOR_PROPERTY, "Property : ", comp);
        LimyColorFieldEditor tagColor = new LimyColorFieldEditor(
                LimyWebConstants.P_COLOR_TAG, "Tag : ", comp);
        
        createPreviewViewer(comp);

        initColorField(commentColor);
        initColorField(keywordColor);
        initColorField(innerColor);
        initColorField(propertyColor);
        initColorField(tagColor);

        return comp;
    }
    
    protected void createFieldEditors() {
        // empty
    }

    public void init(IWorkbench workbench) {
        // empty
    }

    protected void initColorField(LimyColorFieldEditor field) {
        super.initColorField(field);
        field.setPropertyChangeListener(ProviderManager.getInstance().getVelocityCodeScanner());
    }
    
    // ------------------------ Private Methods

    /**
     * プレビュー用ビューアを作成します。
     * @param comp
     */
    private void createPreviewViewer(Composite comp) {
        
        Label previewLabel = new Label(comp, SWT.NONE);
        GridData gridData = new GridData();
        gridData.horizontalSpan = 2;
        previewLabel.setLayoutData(gridData);
        previewLabel.setText("Preview:");

        SourceViewer previewViewer = new SourceViewer(
                comp, null, SWT.V_SCROLL | SWT.H_SCROLL | SWT.BORDER);
        previewViewer.configure(new VelocitySourceViewerConfiguration((VelocityEditor)null));
        String content = LimyEclipsePluginUtils.loadContent(LimyWebPlugin.getDefault(),
                LimyWebConstants.CONTENT_PREVIEW);
        IDocument document = new Document(content);
        previewViewer.setDocument(document);
        previewViewer.setEditable(false);
        Font font = JFaceResources.getFont(PreferenceConstants.EDITOR_TEXT_FONT);
        previewViewer.getTextWidget().setFont(font);
        previewViewer.getTextWidget().setBackground(
                LimyEclipsePlugin.getDefault().getColorProvider().getColor(
                        LimyEclipseConstants.P_BGCOLOR));
        
        Control preview = previewViewer.getControl();
        gridData = new GridData(GridData.FILL_BOTH);
        gridData.horizontalSpan = 2;
        preview.setLayoutData(gridData);
        setSourceViewer(previewViewer);
    }

    /**
     * デフォルト値を初期化します。
     */
    private void initializeDefaults() {
        IPreferenceStore store = getPreferenceStore();
        store.setDefault(LimyWebConstants.P_COLOR_COMMENT, "74,175,187");
        store.setDefault(LimyWebConstants.P_COLOR_KEYWORD, "0,0,255");
        store.setDefault(LimyWebConstants.P_COLOR_INNER, "216,153,39");
        store.setDefault(LimyWebConstants.P_COLOR_PROPERTY, "216,48,15"); 
        store.setDefault(LimyWebConstants.P_COLOR_TAG, "56,155,240"); 
   }

}
