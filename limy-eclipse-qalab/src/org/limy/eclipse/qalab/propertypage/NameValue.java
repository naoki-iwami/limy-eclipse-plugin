/*
 * Created 2007/02/25
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

/**
 * キーと値を持つBeanクラスです。
 * @author Naoki Iwami
 */
public class NameValue {
    
    // ------------------------ Fields

    /** name */
    private String name;
    
    /** value */
    private String value;

    // ------------------------ Constructors

    /**
     * NameValue インスタンスを構築します。
     * @param name name
     * @param value value
     */
    public NameValue(String name, String value) {
        super();
        this.name = name;
        this.value = value;
    }
    
    // ------------------------ Implement Methods
    
    @Override
    public String toString() {
        return name + " - " + value;
    }

    // ------------------------ Getter/Setter Methods

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
    
}