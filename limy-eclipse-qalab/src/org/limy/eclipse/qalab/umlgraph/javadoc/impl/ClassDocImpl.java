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

import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.jdt.core.Flags;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
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
public class ClassDocImpl extends ProgramElementDocImpl implements ClassDoc {
    
    // ------------------------ Fields

    /** ASM解析情報 */
    private final ClassAsmInfo info;
    
    // ------------------------ Constructors

    public ClassDocImpl(RootDocImpl root, IType type, ClassAsmInfo info) {
        super(root, type);
        this.info = info;
    }
    
    // ------------------------ Override Methods

    @Override
    public String toString() {
        return qualifiedName();
    }

    @Override
    public boolean isAbstract() {
        try {
            return Flags.isAbstract(getType().getFlags());
        } catch (JavaModelException e) {
            throw new JavaRuntimeException(e);
        }
    }

    // ------------------------ Implement Methods


    public String qualifiedName() {
        return getType().getFullyQualifiedName();
    }

    public Type superclassType() {
        // TODO
        String superclassName = info.getSuperclassName();
        if (superclassName != null) {
            return getRoot().classNamed(superclassName);
        }
        return null;
    }

    public Type[] interfaceTypes() {
        Collection<Type> results = new ArrayList<Type>();
        String[] names = info.getSuperInterfaceNames();
        for (String name : names) {
            ClassDoc searchedClass = getRoot().classNamed(name);
            if (searchedClass != null) {
                results.add(searchedClass);
            }
        }
        return results.toArray(new Type[results.size()]);
    }

    public ClassDoc findClass(String className) {
        return getRoot().classNamed(className);
    }

    public FieldDoc[] fields() {
        try {
            IField[] fields = getType().getFields();
            Collection<FieldDoc> results = new ArrayList<FieldDoc>();
            for (int i = 0; i < fields.length; i++) {
                if (Flags.isEnum(fields[i].getFlags())) {
                    continue;
                }
                results.add(new FieldDocImpl(getRoot(), fields[i], createExtField(fields[i])));
            }
            return results.toArray(new FieldDoc[results.size()]);
        } catch (JavaModelException e) {
            throw new JavaRuntimeException(e);
        }
    }

    public FieldDoc[] fields(boolean filter) {
        return fields(); // TODO modifier を考慮する
    }

    public MethodDoc[] methods() {
        try {
            IMethod[] methods = getType().getMethods();
            MethodDoc[] results = new MethodDoc[methods.length];
            for (int i = 0; i < methods.length; i++) {
                results[i] = new MethodDocImpl(getRoot(), methods[i], info);
            }
            return results;
        } catch (JavaModelException e) {
            throw new JavaRuntimeException(e);
        }
    }

    public MethodDoc[] methods(boolean filter) {
        return methods(); // TODO modifier を考慮する
    }

    public ParameterizedType asParameterizedType() {
        return null; // TODO 汎用インタフェース （List<E>など）に対応。今のところは全てnull
    }

    public TypeVariable[] typeParameters() {
        return new TypeVariable[0]; // TODO 仮型パラメータ （C<R extends Remote>など）に対応。今のところは全てサイズ0配列
    }

    public ClassDoc asClassDoc() {
        return this;
    }
    
    // ------------------------ Not Supported Methods

    public ConstructorDoc[] constructors() {
        // not support
        return null;
    }

    public ConstructorDoc[] constructors(boolean filter) {
        // not support
        return null;
    }

    public boolean definesSerializableFields() {
        // not support
        return false;
    }

    public FieldDoc[] enumConstants() {
        // not support
        return null;
    }

    public ClassDoc[] importedClasses() {
        // not support
        return null;
    }

    public PackageDoc[] importedPackages() {
        // not support
        return null;
    }

    public ClassDoc[] innerClasses() {
        // not support
        return null;
    }

    public ClassDoc[] innerClasses(boolean filter) {
        // not support
        return null;
    }

    public ClassDoc[] interfaces() {
        // not support
        return null;
    }

    public boolean isExternalizable() {
        // not support
        return false;
    }

    public boolean isSerializable() {
        // not support
        return false;
    }

    public FieldDoc[] serializableFields() {
        // not support
        return null;
    }

    public MethodDoc[] serializationMethods() {
        // not support
        return null;
    }

    public boolean subclassOf(ClassDoc arg0) {
        // not support
        return false;
    }

    public ClassDoc superclass() {
        // not support
        return null;
    }

    public ParamTag[] typeParamTags() {
        // not support
        return null;
    }

    public AnnotationTypeDoc asAnnotationTypeDoc() {
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

    public boolean isPrimitive() {
        // not support
        return false;
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


    // ------------------------ Private Methods

    /**
     * @return
     */
    private IType getType() {
        if (getJdtElement() instanceof IType) {
            return (IType)getJdtElement();
        }
        return null;
    }

    /**
     * @param field
     * @return
     */
    private FieldExt createExtField(IField field) {
        
        for (FieldExt ext : info.getFields()) {
            if (ext.getName().equals(field.getElementName())) {
                return ext;
            }
        }
        return null;
    }    

}
