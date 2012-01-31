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
import org.limy.eclipse.qalab.umlgraph.javadoc.ConstructorDoc;
import org.limy.eclipse.qalab.umlgraph.javadoc.FieldDoc;
import org.limy.eclipse.qalab.umlgraph.javadoc.MethodDoc;
import org.limy.eclipse.qalab.umlgraph.javadoc.PackageDoc;
import org.limy.eclipse.qalab.umlgraph.javadoc.ParamTag;
import org.limy.eclipse.qalab.umlgraph.javadoc.ParameterizedType;
import org.limy.eclipse.qalab.umlgraph.javadoc.Type;
import org.limy.eclipse.qalab.umlgraph.javadoc.TypeVariable;
import org.limy.eclipse.qalab.umlgraph.javadoc.WildcardType;

/**
 *
 * @author Naoki Iwami
 */
public class RefClassDocImpl extends ProgramElementDocImpl implements ClassDoc {

    // ------------------------ Fields

    /** クラス名 */
    private final String className;

    // ------------------------ Constructors

    /**
     * RefClassDocImplインスタンスを構築します。
     * @param root
     * @param className クラス名
     */
    public RefClassDocImpl(RootDocImpl root, String className) {
        super(root, null);
        this.className = className;
    }
    
    // ------------------------ Override Methods

    @Override
    public String toString() {
        return className;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof RefClassDocImpl) {
            RefClassDocImpl other = (RefClassDocImpl)obj;
            return className.equals(other.className);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return className.hashCode();
    }
    
    // ------------------------ Implement Methods

    public ClassDoc asClassDoc() {
        return this;
    }

    @Override
    public PackageDoc containingPackage() {
        return new RefPackageDocImpl(getRoot());
    }

    @Override
    public boolean isEnum() {
        return false;
    }

    public String qualifiedName() {
        return className;
    }

    @Override
    public boolean isInterface() {
        return false;
    }
    
    @Override
    public String name() {
        return className;
    }

    public Type[] interfaceTypes() {
        return new Type[0];
    }

    public FieldDoc[] fields() {
        return new FieldDoc[0];
    }

    public FieldDoc[] fields(boolean filter) {
        return new FieldDoc[0];
    }
    
    public MethodDoc[] methods() {
        return new MethodDoc[0];
    }

    public MethodDoc[] methods(boolean filter) {
        return new MethodDoc[0];
    }
    
    public ConstructorDoc[] constructors() {
        return new ConstructorDoc[0];
    }

    public ConstructorDoc[] constructors(boolean filter) {
        return new ConstructorDoc[0];
    }

    public boolean definesSerializableFields() {
        return false;
    }

    public FieldDoc[] enumConstants() {
        return new FieldDoc[0];
    }

    public ClassDoc findClass(String className) {
        return null;
    }

    public ClassDoc[] importedClasses() {
        return new ClassDoc[0];
    }

    public PackageDoc[] importedPackages() {
        return new PackageDoc[0];
    }

    public ClassDoc[] innerClasses() {
        return new ClassDoc[0];
    }

    public ClassDoc[] innerClasses(boolean filter) {
        return new ClassDoc[0];
    }

    public ClassDoc[] interfaces() {
        return new ClassDoc[0];
    }

    public boolean isExternalizable() {
        return false;
    }

    public boolean isSerializable() {
        return false;
    }

    public FieldDoc[] serializableFields() {
        return new FieldDoc[0];
    }

    public MethodDoc[] serializationMethods() {
        return new MethodDoc[0];
    }

    public boolean subclassOf(ClassDoc arg0) {
        return false;
    }

    public ClassDoc superclass() {
        return null;
    }

    public Type superclassType() {
        return null;
    }

    public ParamTag[] typeParamTags() {
        return new ParamTag[0];
    }

    public TypeVariable[] typeParameters() {
        return new TypeVariable[0];
    }

    public AnnotationTypeDoc asAnnotationTypeDoc() {
        return null;
    }

    public ParameterizedType asParameterizedType() {
        return null;
    }

    public TypeVariable asTypeVariable() {
        return null;
    }

    public WildcardType asWildcardType() {
        return null;
    }

    public String dimension() {
        return null;
    }

    public boolean isPrimitive() {
        return false;
    }

    public String qualifiedTypeName() {
        return className;
    }

    public String simpleTypeName() {
        return null;
    }

    public String typeName() {
        return null;
    }

}
