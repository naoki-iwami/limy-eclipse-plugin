/*
 * Created 2007/01/06
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
package org.limy.eclipse.qalab.tester;

import java.util.ArrayList;
import java.util.Collection;



/**
 *
 * @author Naoki Iwami
 */
public class ClassTestResult {


    // ------------------------ Fields

    /** 対象クラス */
    private final String targetClassName;
    
    /** 結果一覧 */
    private Collection<FailureItem> items;

    // ------------------------ Constructors

    /**
     * ClassTestResultインスタンスを構築します。
     * @param targetClassName
     */
    public ClassTestResult(String targetClassName) {
        super();
        this.targetClassName = targetClassName;
        items = new ArrayList<FailureItem>();
    }
    
    // ------------------------ Public Methods

    public void addItem(FailureItem item) {
        items.add(item);
    }
    
    // ------------------------ Getter/Setter Methods

    /**
     * 対象クラスを取得します。
     * @return 対象クラス
     */
    public String getTargetClassName() {
        return targetClassName;
    }

    /**
     * 結果一覧を取得します。
     * @return 結果一覧
     */
    public Collection<FailureItem> getItems() {
        return items;
    }

}
