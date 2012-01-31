/*
 * Created 2007/02/07
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

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Shell;

/**
 * 文字入力処理を実装したクラスです。
 * @author Naoki Iwami
 */
public class TextInputSelector extends SelectionAdapter {

    // ------------------------ Fields

    /** 紐付けられたコントロール */
    private List control;

    /** 文字入力決定時に呼び出されるリスナー */
    private PropertyChangeListener listener;

    // ------------------------ Constructors

    /**
     * FolderSelectorインスタンスを構築します。
     * @param control 紐付けられたコントロール
     * @param listener フォルダ選択時に呼び出されるリスナー
     */
    public TextInputSelector(List control, PropertyChangeListener listener) {
        super();
        this.control = control;
        this.listener = listener;
    }

    // ------------------------ Override Methods
    
    public void widgetSelected(SelectionEvent e) {
        select();
    }
    
    // ------------------------ Private Methods

    /**
     * 
     */
    private void select() {
        
        InputDialog dialog = new InputDialog(new Shell(), "dialog", "Input text", "", null);
        if (dialog.open() == Dialog.OK) {
            control.add(dialog.getValue());
            if (listener != null) {
                PropertyChangeEvent evt = new PropertyChangeEvent(
                        control, "value", null, dialog.getValue()
                );
                listener.propertyChange(evt);
            }
        }
        
    }

}
