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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.jface.preference.IPersistentPreferenceStore;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Text;
import org.limy.eclipse.common.LimyEclipsePluginUtils;

/**
 * ストアに関連付けられたコントロール一覧を定義したクラスです。
 * @author Naoki Iwami
 */
public class StoredComposite extends Composite {

    // ------------------------ Fields

    /** ストア */
    private IPreferenceStore store;
    
    /** コントロール一覧 */
    private Collection<AbstractStoredControl> controls = new ArrayList<AbstractStoredControl>();

    // ------------------------ Constructors

    /**
     * StoredCompositeインスタンスを構築します。
     * @param parent
     * @param style
     * @param store 
     */
    public StoredComposite(Composite parent, int style, IPreferenceStore store) {
        super(parent, style);
        this.store = store;
    }
    
    // ------------------------ Public Methods
    
    public boolean performOk() {
        for (AbstractStoredControl control : controls) {
            control.doStore(store);
        }
        try {
            ((IPersistentPreferenceStore)store).save();
        } catch (IOException e) {
            LimyEclipsePluginUtils.log(e);
        }
        
        return true;
    }

    // ------------------------ Protected Methods
    
    /**
     * @param storeKey
     * @param text
     */
    protected void addField(String storeKey, Text text) {
        text.setText(store.getString(storeKey));
        controls.add(new StoredText(storeKey, text));
    }

    /**
     * @param storeKey
     * @param button
     */
    protected void addField(String storeKey, Button button) {
        button.setSelection(store.getBoolean(storeKey));
        controls.add(new StoredButton(storeKey, button));
    }

    /**
     * @param storeKey
     * @param list
     */
    protected void addField(String storeKey, List list) {
        String[] lines = store.getString(storeKey).split("\n");
        for (String line : lines) {
            if (line.length() > 0) {
                list.add(line);
            }
        }
        controls.add(new StoredList(storeKey, list));
    }

    /**
     * @param storeKey
     * @param buttons 
     */
    protected void addField(String storeKey, Button... buttons) {
        int index = store.getInt(storeKey);
        buttons[index].setSelection(true);
        controls.add(new StoredMultiButton(storeKey, buttons));
    }
    
    /**
     * @param storeKey
     * @param list
     */
    protected void addNameValueList(String storeKey, ListViewer listViewer) {
        String[] lines = store.getString(storeKey).split("\n");
        Collection<NameValue> inputs = new ArrayList<NameValue>();
        for (String line : lines) {
            if (line.length() > 0) {
                String[] split = line.split("\t");
                inputs.add(new NameValue(split[0], split[1]));
            }
        }
        listViewer.setInput(inputs);
        controls.add(new StoredNameValueList(storeKey, listViewer));
    }
    
    protected IPreferenceStore getStore() {
        return store;
    }

}
