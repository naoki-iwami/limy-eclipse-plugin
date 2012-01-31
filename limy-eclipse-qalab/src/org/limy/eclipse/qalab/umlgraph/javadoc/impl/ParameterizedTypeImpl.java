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

import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.limy.eclipse.qalab.umlgraph.javadoc.ClassDoc;
import org.limy.eclipse.qalab.umlgraph.javadoc.ParameterizedType;
import org.limy.eclipse.qalab.umlgraph.javadoc.Type;

/**
 *
 * @author Naoki Iwami
 */
public class ParameterizedTypeImpl extends TypeImpl implements ParameterizedType {
    
    /** 全シグネチャ */
    private String allSignature;
    
    /**
     * ParameterizedTypeImplインスタンスを構築します。
     * <p>
     * 例えば java.util.Map<java.lang.String, java.net.URI> など
     * </p>
     * @param root
     * @param allSignature 全シグネチャ（ex. Ljava/util/Map<Ljava/lang/String;Lb/LimyClassObject;>;）
     * @param mainSignature メインクラスのシグネチャ（ex. QMap;）
     */
    public ParameterizedTypeImpl(RootDocImpl root,
            String allSignature,
            String mainSignature) {
        super(root, mainSignature, null);
        this.allSignature = allSignature;
    }

    // ------------------------ Override Methods

    @Override
    public String dimension() {
        return ""; // TODO これで良いか？
    }
    
    @Override
    public String qualifiedTypeName() {
        return allSignature.substring(1, allSignature.indexOf('<')).replace('/', '.');
    }
    
    @Override
    public ClassDoc asClassDoc() {
        return getRoot().classNamed(qualifiedTypeName());
    }

    // ------------------------ Implement Methods

    public Type[] interfaceTypes() {
        String name = qualifiedTypeName(); // java.util.List
        try {
            IType type = getRoot().getEnv().getJavaProject().findType(name);
            String[] names = type.getSuperInterfaceNames(); // [ java.util.Collection ]
            String[] signatures = type.getSuperInterfaceTypeSignatures();
            Type[] types = new Type[names.length];
            for (int i = 0; i < names.length; i++) {
                types[i] = new ParameterizedTypeImpl(getRoot(),
                        "L" + names[i].replace('.', '/')
                        + allSignature.substring(allSignature.indexOf('<')),
                        signatures[i]);
            }
            return types;
        } catch (JavaModelException e) {
            throw new JavaRuntimeException(e);
        }
    }

    public Type superclassType() {
        return null; // not support
    }

    public Type[] typeArguments() {
        // Ljava/util/Map<Ljava/lang/String;Lb/LimyClassObject;>;
        String target = allSignature.substring(allSignature.indexOf('<') + 1);
        
        Collection<Type> types = new ArrayList<Type>();
        while (true) {
            StrAndPos spos = getFirstQualifiedName(target);
            if (spos == null) {
                break;
            }
            types.add(new ClassType(getRoot(), "", spos.getStr()));
            target = target.substring(spos.getPos());
        }
        
        return types.toArray(new Type[types.size()]);
    }

    // ------------------------ Private Methods

    /**
     * 与えられたasmシグネチャのうち、先頭に見つかったクラスとその終了位置を返します。
     * @param all asmシグネチャ
     * @return 先頭のクラス名および位置
     */
    private StrAndPos getFirstQualifiedName(String all) {
        if (all.charAt(0) == 'L') {
            int lastPos = all.indexOf(';', 1);
            return new StrAndPos(all.substring(1, lastPos).replace('/', '.'), lastPos + 1);
            // Ljava/lang/String;Lb/LimyClassObject;>; -> java.lang.String
        }
        if (all.charAt(0) == '[' && all.charAt(1) == 'L') {
            int lastPos = all.indexOf(';', 1);
            return new StrAndPos(all.substring(2, lastPos).replace('/', '.'), lastPos + 1);
            // [Ljava/lang/String;Lb/LimyClassObject;>; -> java.lang.String
        }
        return null;
    }
    

}
