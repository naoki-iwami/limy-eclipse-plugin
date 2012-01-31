/*
 * Created 2007/01/23
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

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.widgets.Button;

/**
 * ストアと関連付けられた複数のButtonコントロール用クラスです。
 * @author Naoki Iwami
 */
public class StoredMultiButton extends AbstractStoredControl {

    // ------------------------ Fields

    /** 複数のButtonコントロール */
    private final Button[] controls;

    // ------------------------ Constructors

    /**
     * StoredMultiButtonインスタンスを構築します。
     * @param storeKey ストアキー
     * @param controls 複数のButtonコントロール
     */
    public StoredMultiButton(String storeKey, Button[] controls) {
        super(storeKey);
        this.controls = controls;
    }

    // ------------------------ Implement Methods

    @Override
    public void doStore(IPreferenceStore store) {
        int index = 0;
        for (Button button : controls) {
            if (button.getSelection()) {
                store.setValue(getStoreKey(), index);
                break;
            }
            ++index;
        }
    }

}
