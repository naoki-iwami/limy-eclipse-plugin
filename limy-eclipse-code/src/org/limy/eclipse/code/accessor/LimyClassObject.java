/*
 * Created 2005/09/13
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
package org.limy.eclipse.code.accessor;

import java.util.ArrayList;
import java.util.List;

import org.limy.eclipse.code.common.LimyFieldObject;

/**
 *　クラスオブジェクトを表します。
 * @opt inferrel
 * @opt collpackages java.util.*
 * @opt inferdep
 * @author Naoki Iwami
 */
public class LimyClassObject {
    
    // ------------------------ Fields

    /**
     * フィールド一覧
     */
    private List<LimyFieldObject> fields = new ArrayList<LimyFieldObject>();
    
//    // ------------------------ Constructors
//
//    /**
//     * LimyClassObjectインスタンスを構築します。
//     */
//    public LimyClassObject() {
//        
//    }
    
    // ------------------------ Public Methods

    /**
     * フィールドを追加します。
     * @param field 追加するフィールド
     */
    public void addField(LimyFieldObject field) {
        fields.add(field);
    }
    
    /**
     * フィールド反復子を返します。
     * @return フィールド反復子
     */
    public List<LimyFieldObject> getFields() {
        return fields;
    }
    
    // ------------------------ Getter/Setter Methods

    

}
