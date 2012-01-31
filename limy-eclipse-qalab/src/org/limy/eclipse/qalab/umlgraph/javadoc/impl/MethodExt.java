/*
 * Created 2007/02/16
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
package org.limy.eclipse.qalab.umlgraph.javadoc.impl;


/**
 *
 * @author Naoki Iwami
 */
public class MethodExt {
    
    private final String returnTypeSignature;
    
    private final String name;

    private final String[] paramQualifiedNames;

    /**
     * IFieldExtインスタンスを構築します。
     * @param signature
     * @param name
     */
    public MethodExt(String signature, String name, String[] paramQualifiedNames) {
        super();
        // Lpack1/b;
        this.returnTypeSignature = signature;
        this.name = name;
        this.paramQualifiedNames = paramQualifiedNames;
    }

    /**
     * qualifiedClassNameを取得します。
     * @return qualifiedClassName
     */
    public String getQualifiedTypeName() {
        if (returnTypeSignature.startsWith("L")) {
            return returnTypeSignature.substring(1, returnTypeSignature.length() - 1).replace('/', '.');
        }
        return returnTypeSignature;
    }

    /**
     * nameを取得します。
     * @return name
     */
    public String getName() {
        return name;
    }

    /**
     * paramQualifiedNamesを取得します。
     * @return paramQualifiedNames
     */
    public String[] getParamQualifiedNames() {
        return paramQualifiedNames;
    }

}
