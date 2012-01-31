/*
 * Created 2007/12/14
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

/**
 * チェック付のテーブルセルを表します。
 * @author Naoki Iwami
 */
public class CheckedTableObject implements ITableObject {

    /** 表示用文字列 */
    private String viewStr;

    /** 内部値 */
    private Object value;
    
    /** 選択状況 */
    private boolean checked;
    
    // ------------------------ Constructors

    /**
     * CheckedTableObject インスタンスを構築します。
     * @param viewStr 表示用文字列
     * @param value 内部値
     */
    public CheckedTableObject(String viewStr, Object value) {
        super();
        this.viewStr = viewStr;
        this.value = value;
    }
    
    // ------------------------ Implement Methods

    public String getViewString(int index) {
        return viewStr;
    }

    public Object getValue(int index) {
        return value;
    }

    public void setValue(int index, Object value) {
        this.value = value;
    }

    // ------------------------ Getter/Setter Methods

    /**
     * 選択状況を取得します。
     * @return 選択状況
     */
    public boolean isChecked() {
        return checked;
    }

    /**
     * 選択状況を設定します。
     * @param checked 選択状況
     */
    public void setChecked(boolean checked) {
        this.checked = checked;
    }

}
