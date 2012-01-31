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

import org.eclipse.jdt.core.Flags;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.JavaModelException;
import org.limy.eclipse.qalab.umlgraph.javadoc.FieldDoc;
import org.limy.eclipse.qalab.umlgraph.javadoc.Type;

/**
 *
 * @author Naoki Iwami
 */
public class FieldDocImpl extends MemberDocImpl implements FieldDoc {

    private final FieldExt ext;
    
    /**
     * FieldDocImplインスタンスを構築します。
     * @param root
     * @param element
     */
    public FieldDocImpl(RootDocImpl root, IField element, FieldExt ext) {
        super(root, element);
        this.ext = ext;
    }

    // ------------------------ Override Methods

    @Override
    public boolean isStatic() {
        IField field = (IField)getElement();
        try {
            return Flags.isStatic(field.getFlags());
        } catch (JavaModelException e) {
            throw new JavaRuntimeException(e);
        }
    }

    // ------------------------ Implement Methods

    public Type type() {
        try {
            if (ext.getParameterClass() != null) {
                // Map<S,T>など
                
                IField field = (IField)getElement();
                
                return new ParameterizedTypeImpl(getRoot(),
                        ext.getParameterClass(),
                        field.getTypeSignature());
            }
            return new FieldType(getRoot(), (IField)getJdtElement(), ext);
        } catch (JavaModelException e) {
            throw new JavaRuntimeException(e);
        }
    }

}
