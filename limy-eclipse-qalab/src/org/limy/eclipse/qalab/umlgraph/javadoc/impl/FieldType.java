/*
 * Created 2007/02/15
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

import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.JavaModelException;
import org.limy.eclipse.qalab.umlgraph.javadoc.ClassDoc;

/**
 *
 * @author Naoki Iwami
 */
public class FieldType extends TypeImpl {
    
    private final RootDocImpl root;
    
    private final IField field;
    
    private final FieldExt fieldExt;

    /**
     * FieldTypeインスタンスを構築します。
     * @param fieldExt
     * @throws JavaModelException 
     */
    public FieldType(RootDocImpl root, IField field, FieldExt fieldExt) throws JavaModelException {
        super(root, field.getTypeSignature(), fieldExt.getQualifiedTypeName());
        this.root = root;
        this.field = field;
        this.fieldExt = fieldExt;
    }

    // ------------------------ Implement Methods

    public boolean isPrimitive() {
        return fieldExt.isPrimitive();
        
//        try {
//            String signature = field.getTypeSignature();
//            return !signature.startsWith("Q");
//        } catch (JavaModelException e) {
//            throw new JavaRuntimeException(e);
//        }
    }

    public String dimension() {
        try {
            String signature = field.getTypeSignature();
            int pos = 0;
            StringBuilder buff = new StringBuilder();
            while (signature.charAt(pos) == '[') {
                buff.append("[]");
                ++pos;
            }
            return buff.toString();
        } catch (JavaModelException e) {
            throw new JavaRuntimeException(e);
        }
    }

    public ClassDoc asClassDoc() {
        return root.classNamed(qualifiedTypeName());
    }

    public String qualifiedTypeName() {
        return fieldExt.getQualifiedTypeName();
    }

}
