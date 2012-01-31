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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;

/**
 *
 * @author Naoki Iwami
 */
public class ClassAsmInfo {

    private String superclassName;

    private Collection<FieldExt> fields = new ArrayList<FieldExt>();

    private Collection<MethodExt> methods = new ArrayList<MethodExt>();
    
    private String[] superInterfaceNames;
    
    public static void main(String[] args) throws IOException {
        ClassAsmInfo info = new ClassAsmInfo(
                new File("C:\\var\\workspace\\runtime-EclipseApplication\\javatest1\\bin\\b\\TestB.class"));
        
    }
    
    public ClassAsmInfo(File file) throws IOException {
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(file);
            ClassReader cr = new ClassReader(inputStream);
            ClassVisitor cv = new MyClassVisitor(this);
            cr.accept(cv, 0);
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
        }
    }

    /**
     * @param name
     * @param signature
     */
    public void addField(String name, String signature) {
        fields.add(new FieldExt(name, signature));
    }

    /**
     * @param name
     * @param signature
     */
    public void addField(String name, String signature, String parameterClass) {
        fields.add(new FieldExt(name, signature, parameterClass));
    }

    /**
     * @param name
     * @param returnTypeSignature
     */
    public void addMethod(String name, String returnTypeSignature, String[] paramQualifiedNames) {
        methods.add(new MethodExt(returnTypeSignature, name, paramQualifiedNames));
    }

    /**
     * fieldsÇéÊìæÇµÇ‹Ç∑ÅB
     * @return fields
     */
    public Collection<FieldExt> getFields() {
        return fields;
    }

    /**
     * @return
     */
    public String getSuperclassName() {
        return superclassName;
    }

    /**
     * superclassNameÇê›íËÇµÇ‹Ç∑ÅB
     * @param superclassName superclassName
     */
    public void setSuperclassName(String superclassName) {
        this.superclassName = superclassName;
    }

    /**
     * @return
     */
    public Collection<MethodExt> getMethods() {
        return methods;
    }

    /**
     * @return
     */
    public String[] getSuperInterfaceNames() {
        return superInterfaceNames;
    }
    
    /**
     * superInterfaceNamesÇê›íËÇµÇ‹Ç∑ÅB
     * @param superInterfaceNames superInterfaceNames
     */
    public void setSuperInterfaceNames(String[] superInterfaceNames) {
        this.superInterfaceNames = superInterfaceNames;
    }

}
