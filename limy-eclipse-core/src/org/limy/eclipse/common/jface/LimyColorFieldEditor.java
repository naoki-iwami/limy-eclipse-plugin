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

import org.eclipse.jface.preference.ColorFieldEditor;
import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.swt.widgets.Composite;

/**
 * ダイアログ上の色選択フィールドを表すクラスです。
 * @author Naoki Iwami
 */
public class LimyColorFieldEditor extends ColorFieldEditor {
    
    /** 関連付けられたソースビューア */
    private SourceViewer sourceViewer;
    
    /**
     * @param prefKey
     * @param labelText
     * @param comp
     */
    public LimyColorFieldEditor(String prefKey, String labelText, Composite comp) {
        super(prefKey, labelText, comp);
    }
    
    /**
     * 関連付けられたソースビューアを取得します。
     * @return 関連付けられたソースビューア
     */
    public SourceViewer getSourceViewer() {
        return sourceViewer;
    }

    /**
     * 関連付けられたソースビューアを設定します。
     * @param sourceViewer 関連付けられたソースビューア
     */
    public void setSourceViewer(SourceViewer sourceViewer) {
        this.sourceViewer = sourceViewer;
    }

}
