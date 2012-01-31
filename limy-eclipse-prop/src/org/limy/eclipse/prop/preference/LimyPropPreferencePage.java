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
package org.limy.eclipse.prop.preference;

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
import org.limy.eclipse.common.LimyEclipseConstants;
import org.limy.eclipse.common.LimyEclipsePluginUtils;
import org.limy.eclipse.common.jface.AbstractLimyPreferencePage;
import org.limy.eclipse.common.jface.LimyColorFieldEditor;
import org.limy.eclipse.common.swt.GridDataCreator;
import org.limy.eclipse.core.LimyEclipsePlugin;
import org.limy.eclipse.prop.LimyPropConstants;
import org.limy.eclipse.prop.LimyPropPlugin;
import org.limy.eclipse.prop.editor.PropertyEditor;
import org.limy.eclipse.prop.editor.PropertySourceViewerConfiguration;
import org.limy.eclipse.prop.editor.ProviderManager;

/**
 * プロパティファイル用PreferencePageを表すクラスです。
 * @author Naoki Iwami
 */
public class LimyPropPreferencePage extends AbstractLimyPreferencePage {
    
    /**
     * LimyPropPreferencePageインスタンスを構築します。
     */
    public LimyPropPreferencePage() {
        super(LimyEclipsePlugin.getDefault().getPreferenceStore());
        setDescription("property-file setting page");
        initializeDefaults();
    }

    // methods in BasePreferencePage
    
    protected Control createContents(Composite parent) {
        Composite comp = new Composite(parent, SWT.NULL);
        comp.setLayout(new GridLayout());
        
        LimyColorFieldEditor colorNameField = new LimyColorFieldEditor(
                LimyPropConstants.P_COLOR_NAME, "Property name : ", comp);
        LimyColorFieldEditor colorValueField = new LimyColorFieldEditor(
                LimyPropConstants.P_COLOR_VALUE, "Property value : ", comp);
        LimyColorFieldEditor colorCommentField = new LimyColorFieldEditor(
                LimyPropConstants.P_COLOR_COMMENT, "Comment : ", comp);

        
        Label previewLabel = new Label(comp, SWT.NONE);
        GridData gridData = GridDataCreator.createHorizontalSpan(2);
        previewLabel.setLayoutData(gridData);
        previewLabel.setText("Preview:");
        
        createPreviewViewer(comp);
        
        initColorField(colorNameField);
        initColorField(colorValueField);
        initColorField(colorCommentField);
        
        return comp;
    }

    protected void performDefaults() {
        super.performDefaults();
        
//        LimyEclipsePlugin.getDefault().updateProviders();
        ProviderManager.getInstance().getPropertyScanner()
                .refreshViewer(getSourceViewer(), getColorFields());

    }

    protected void initColorField(LimyColorFieldEditor field) {
        super.initColorField(field);
        field.setPropertyChangeListener(ProviderManager.getInstance().getPropertyScanner());
    }
    
    // -------------------------------- Private Methods

    /**
     * デフォルト値を初期化します。
     */
    private void initializeDefaults() {
        IPreferenceStore store = getPreferenceStore();
        store.setDefault(LimyPropConstants.P_COLOR_NAME, "215,69,66");
        store.setDefault(LimyPropConstants.P_COLOR_VALUE, "55,104,47");
        store.setDefault(LimyPropConstants.P_COLOR_COMMENT, "151,54,2");
    }

    /**
     * プレビュー用コンポーネントを作成します。
     * @param comp 親コンポーネント
     */
    private void createPreviewViewer(Composite comp) {
        SourceViewer previewViewer = new SourceViewer(
                comp, null, SWT.V_SCROLL | SWT.H_SCROLL | SWT.BORDER);
        previewViewer.configure(new PropertySourceViewerConfiguration((PropertyEditor)null));
        String content = LimyEclipsePluginUtils.loadContent(LimyPropPlugin.getDefault(),
                LimyPropConstants.CONTENT_PREVIEW);
        IDocument document = new Document(content);
        previewViewer.setDocument(document);
        previewViewer.setEditable(false);
        Font font = JFaceResources.getFont(PreferenceConstants.EDITOR_TEXT_FONT);
        previewViewer.getTextWidget().setFont(font);
        previewViewer.getTextWidget().setBackground(
                LimyEclipsePlugin.getDefault().getColorProvider().getColor(
                        LimyEclipseConstants.P_BGCOLOR));

        Control preview = previewViewer.getControl();
        GridData gridData = new GridData(GridData.FILL_BOTH);
        gridData.horizontalSpan = 2;
        preview.setLayoutData(gridData);
        setSourceViewer(previewViewer);
    }
}
