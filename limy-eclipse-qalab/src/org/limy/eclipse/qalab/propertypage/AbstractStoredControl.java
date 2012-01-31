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

import org.eclipse.jface.preference.IPreferenceStore;

/**
 * ストアと関連付けられたコントロールの基底クラスです。
 * @author Naoki Iwami
 */
public abstract class AbstractStoredControl {

    // ------------------------ Fields

    /** ストアキー */
    private final String storeKey;
    
    // ------------------------ Constructors

    /**
     * StoredControlインスタンスを構築します。
     * @param storeKey
     */
    public AbstractStoredControl(String storeKey) {
        super();
        this.storeKey = storeKey;
    }
    
    // ------------------------ Public Methods

    /**
     * ストアにコントロールの内容を書き込みます。
     * @param store ストア
     */
    public abstract void doStore(IPreferenceStore store);

    // ------------------------ Protected Methods

    /**
     * ストアキーを取得します。
     * @return ストアキー
     */
    protected String getStoreKey() {
        return storeKey;
    }
    
}
