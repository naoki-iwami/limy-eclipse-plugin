/*
 * Created 2007/02/10
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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/**
 * ファイル（External）選択処理を実装したクラスです。
 * @author Naoki Iwami
 */
public class ExternalFileSelector extends SelectionAdapter {

    // ------------------------ Fields
    
    /** 初期値の値が格納されたコントロール */
    private Text control;
      
    /** 対象となるファイル拡張子 */
    private String targetExt;

    /** 選択時リスナ */
    private PropertyChangeListener listener;

    // ------------------------ Constructors

    /**
     * ExternalFileSelectorインスタンスを構築します。
     * @param control 初期値の値が格納されたコントロール
     * @param targetExt 対象となるファイル拡張子
     * @param listener ファイル選択時に呼び出されるリスナー
     */
    public ExternalFileSelector(
            Text control, String targetExt,
            PropertyChangeListener listener) {
        
        super();
        this.control = control;
        this.targetExt = targetExt;
        this.listener = listener;
    }

    // ------------------------ Override Methods

    @Override
    public void widgetSelected(SelectionEvent e) {

        setFromExternal();
    }

    // ------------------------ Private Methods
    
    /**
     * 外部リソースからファイルを選択します。
     */
    private void setFromExternal() {
        
        FileDialog dialog = new FileDialog(new Shell(), SWT.OPEN);
        dialog.setFilterExtensions(new String[] { "*." + targetExt });
        String file = dialog.open();
        if (file != null) {
            control.setText(file);
            if (listener != null) {
                PropertyChangeEvent evt = new PropertyChangeEvent(
                        control, "file", null, file
                );
                listener.propertyChange(evt);
            }
        }
    }

}
