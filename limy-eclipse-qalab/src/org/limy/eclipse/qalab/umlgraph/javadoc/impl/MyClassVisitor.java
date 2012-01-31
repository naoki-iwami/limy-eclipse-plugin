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

import java.util.ArrayList;
import java.util.Collection;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Attribute;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;

/**
 *
 * @author Naoki Iwami
 */
public class MyClassVisitor implements ClassVisitor {

    /** ASM解析情報 */
    private final ClassAsmInfo info;
    
    /**
     * MyClassVisitorインスタンスを構築します。
     * @param info
     */
    public MyClassVisitor(ClassAsmInfo info) {
        this.info = info;
    }

    public void visit(int version, int access, String name, String signature,
            String superName, String[] interfaces) {
        
        info.setSuperclassName(superName.replace('/', '.'));
        
        String[] names = new String[interfaces.length];
        for (int i = 0; i < names.length; i++) {
            names[i] = interfaces[i].replace('/', '.');
        }
        info.setSuperInterfaceNames(names);

    }

    public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
        // not support
        return null;
    }

    public void visitAttribute(Attribute attr) {
        // not support
    }

    public void visitEnd() {
        // not support
    }

    public FieldVisitor visitField(int access, String name, String desc,
            String signature, Object value) {
        
        if (signature != null) {
            // <T>形式の場合
            // signature : Ljava/util/Collection<Lb/LimyClassObject;>;
            info.addField(name, desc, signature);
        } else {
            info.addField(name, desc);
        }
        return null;
    }

    public void visitInnerClass(String name, String outerName,
            String innerName, int access) {
        // not support
    }

    public MethodVisitor visitMethod(int access, String name, String desc,
            String signature, String[] exceptions) {
        
        int index = desc.indexOf(')');
        Collection<String> types = new ArrayList<String>();
        
        int pos = 1;
        while (desc.charAt(pos) != ')') {
            char c = desc.charAt(pos++);
            if (c == '[') {
                char nextC = desc.charAt(pos++);
                if (nextC == 'L') {
                    int lastPos = desc.indexOf(';', pos);
                    types.add(desc.substring(pos - 1, lastPos).replace('/', '.'));
                    pos = lastPos + 1;
                } else {
                    types.add(new String(new char[] { nextC }));
                }
            } else if (c == 'L') {
                int lastPos = desc.indexOf(';', pos);
                types.add(desc.substring(pos - 1, lastPos).replace('/', '.'));
                pos = lastPos + 1;
            } else {
                types.add(new String(new char[] { c }));
            }
        }
        
        info.addMethod(name, desc.substring(index + 1), types.toArray(new String[types.size()]));
        return null;
    }

    public void visitOuterClass(String owner, String name, String desc) {
        // not support
    }

    public void visitSource(String source, String debug) {
        // not support
    }

}
