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

import org.limy.eclipse.qalab.umlgraph.javadoc.AnnotationTypeDoc;
import org.limy.eclipse.qalab.umlgraph.javadoc.ClassDoc;
import org.limy.eclipse.qalab.umlgraph.javadoc.ParameterizedType;
import org.limy.eclipse.qalab.umlgraph.javadoc.Type;
import org.limy.eclipse.qalab.umlgraph.javadoc.TypeVariable;
import org.limy.eclipse.qalab.umlgraph.javadoc.WildcardType;

/**
 * 
 * @author Naoki Iwami
 */
public class TypeImpl implements Type {

    /** RootDoc */
    private final RootDocImpl root;

    /** タイプ名 */
    private final String localTypeName;

    /** 完全限定名 */
    private final String localQualifiedTypeName;

    public TypeImpl(RootDocImpl root, String typeName, String qualifiedTypeName) {
        super();
        this.root = root;
        this.localTypeName = typeName;
        this.localQualifiedTypeName = qualifiedTypeName;
    }

    public boolean isPrimitive() {
        if (localTypeName.startsWith("Q")) {
            return false;
        }
        return true;
    }

    public ClassDoc asClassDoc() {
        return root.classNamed(localQualifiedTypeName);
    }

    public AnnotationTypeDoc asAnnotationTypeDoc() {
        // not support
        return null;
    }

    public ParameterizedType asParameterizedType() {
        // not support
        return null;
    }

    public TypeVariable asTypeVariable() {
        // not support
        return null;
    }

    public WildcardType asWildcardType() {
        // not support
        return null;
    }

    public String dimension() {
        // not support
        return null;
    }

    public String qualifiedTypeName() {
        // not support
        return null;
    }

    public String simpleTypeName() {
        // not support
        return null;
    }

    public String typeName() {
        // not support
        return null;
    }

    // ------------------------ Protected Methods

    /**
     * rootを取得します。
     * 
     * @return root
     */
    protected RootDocImpl getRoot() {
        return root;
    }

}
