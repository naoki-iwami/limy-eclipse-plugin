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

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.StatusDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.limy.eclipse.common.swt.FormDataCreater;

/**
 * キーと値の入力処理を実装したクラスです。
 * @author Naoki Iwami
 */
public class ValueInputSelector extends SelectionAdapter {

    // ------------------------ Classes
    
    /**
     * @author Naoki Iwami
     */
    static class NameValueDialog extends StatusDialog {

        /** nameテキスト */
        private Text nameText;
        /** valueテキスト */
        private Text valueText;
        /** 返却値 */
        private NameValue nameValue;

        public NameValueDialog(Shell parent, String title) {
            super(parent);
            setTitle(title);
            setHelpAvailable(false);
        }

        @Override
        protected Control createDialogArea(Composite parent) {
            
            Composite comp = new Composite(parent, SWT.NONE);
            comp.setLayout(FormDataCreater.createLayout(8, 8));
            
            Label nameLabel = new Label(comp, SWT.NONE);
            nameLabel.setLayoutData(FormDataCreater.controlDownWithWidth(null, 0, 0, 100));
            nameLabel.setText("Name:");
            
            nameText = new Text(comp, SWT.BORDER);
            nameText.setLayoutData(FormDataCreater.controlDownWithWidth(null, 0, 108, 200));

            Label valueLabel = new Label(comp, SWT.NONE);
            valueLabel.setLayoutData(FormDataCreater.controlDownWithWidth(nameLabel, 8, 0, 100));
            valueLabel.setText("Value:");

            valueText = new Text(comp, SWT.BORDER);
            valueText.setLayoutData(FormDataCreater.controlDownWithWidth(nameLabel, 8, 108, 200));

            return comp;
        }
        
        @Override
        protected void okPressed() {
            nameValue = new NameValue(nameText.getText(), valueText.getText());
            super.okPressed();
        }

        public NameValue getNameValue() {
            return nameValue;
        }
        
    }

    // ------------------------ Fields

    /** 紐付けられたコントロール */
    private List control;

    /** 文字入力決定時に呼び出されるリスナー */
    private PropertyChangeListener listener;

    // ------------------------ Constructors

    /**
     * ValueInputSelectorインスタンスを構築します。
     * @param control 紐付けられたコントロール
     * @param listener OK選択時に呼び出されるリスナー(not null)
     */
    public ValueInputSelector(List control, PropertyChangeListener listener) {
        super();
        Assert.isNotNull(listener);
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
        
        NameValueDialog dialog = new NameValueDialog(new Shell(), "Name and Value");
        if (dialog.open() == Dialog.OK) {
            PropertyChangeEvent evt = new PropertyChangeEvent(
                    control, "nameValue", null, dialog.getNameValue()
            );
            listener.propertyChange(evt);
        }
        
    }

}
