/*
 * Created 2004/12/17
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
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

/**
 * Preferenceページの基底クラスです。
 * @author Naoki Iwami
 */
public abstract class AbstractLimyPreferencePage extends FieldEditorPreferencePage
        implements IWorkbenchPreferencePage {
    
    // ------------------------ Fields

    /** 関連付けられたソースビューア */
    private SourceViewer sourceViewer;

    /** フィールド一覧 */
    private List<FieldEditor> fields = new ArrayList<FieldEditor>();

    // ------------------------ Constructors

    /**
     * BasePreferencePageインスタンスを構築します。
     * @param store 
     */
    public AbstractLimyPreferencePage(IPreferenceStore store) {
        super(GRID);
        setPreferenceStore(store);
    }
    
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

    @Override
    protected void createFieldEditors() {
        // empty
    }

    public void init(IWorkbench workbench) {
        // empty
    }

    // ------------------------ Protected Methods

    /**
     * フィールドを初期化します。
     * @param field フィールド
     */
    protected void initField(FieldEditor field) {
        field.setPreferenceStore(getPreferenceStore());
        setPreferencePage(this, field);
        field.load();
        fields.add(field);
    }

    /**
     * 色フィールドを初期化します。
     * @param field 色フィールド
     */
    protected void initColorField(LimyColorFieldEditor field) {
        field.setPreferenceStore(getPreferenceStore());
        setPreferencePage(this, field);
        field.load();
        field.setSourceViewer(sourceViewer);
        fields.add(field);
    }

    /**
     * 色フィールドの配列を取得します。
     * @return 色フィールドの配列
     */
    protected LimyColorFieldEditor[] getColorFields() {
        
        List<LimyColorFieldEditor> results = new ArrayList<LimyColorFieldEditor>();
        for (Iterator<FieldEditor> it = fields.iterator(); it.hasNext();) {
            Object field = it.next();
            if (field instanceof LimyColorFieldEditor) {
                results.add((LimyColorFieldEditor)field);
            }
        }
        return results.toArray(new LimyColorFieldEditor[results.size()]);
    }

    // ------------------------ Getter/Setter Methods
    
    /**
     * 関連付けられたソースビューアを取得します。
     * @return 関連付けられたソースビューア
     */
    protected SourceViewer getSourceViewer() {
        return sourceViewer;
    }

    /**
     * 関連付けられたソースビューアを設定します。
     * @param sourceViewer 関連付けられたソースビューア
     */
    protected void setSourceViewer(SourceViewer sourceViewer) {
        this.sourceViewer = sourceViewer;
    }

    
    // ------------------------ Private Methods

    /**
     * FieldEditor を PreferencePage に関連付けます。
     * @param page PreferencePage
     * @param field FieldEditor
     */
    private void setPreferencePage(PreferencePage page, FieldEditor field) {
        field.setPage(page);
    }

}
