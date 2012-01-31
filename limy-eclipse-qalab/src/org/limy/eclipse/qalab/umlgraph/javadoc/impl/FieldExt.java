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
public class FieldExt {
    
    private final String name;

    // [Lgr/spinellis/umlgraph/doclet/WrappedClassDoc;
    // Lgr/spinellis/umlgraph/doclet/WrappedClassDoc;
    // [I
    // I
    private final String signature;
    
    /** パッケージ情報も含めたシグネチャ */
    private String parameterClass;

    /**
     * IFieldExtインスタンスを構築します。
     * @param name
     * @param signature
     */
    public FieldExt(String name, String signature) {
        super();
        // Lpack1/b;
        this.signature = signature;
        this.name = name;
    }

    /**
     * IFieldExtインスタンスを構築します。
     * @param name
     * @param signature
     */
    public FieldExt(String name, String signature, String parameterClass) {
        super();
        this.signature = signature;
        this.name = name;
        this.parameterClass = parameterClass;
    }

    /**
     * qualifiedClassNameを取得します。
     * @return qualifiedClassName
     */
    public String getQualifiedTypeName() {
        if (signature.charAt(0) == 'L') {
            return signature.substring(1, signature.length() - 1).replace('/', '.');
        }
        if (signature.charAt(0) == '[' && signature.charAt(1) == 'L') {
            return signature.substring(2, signature.length() - 1).replace('/', '.');
        }
        return signature;
    }

    /**
     * nameを取得します。
     * @return name
     */
    public String getName() {
        return name;
    }

    /**
     * @return
     */
    public boolean isPrimitive() {
        if (signature.charAt(0) == '[') {
            return signature.charAt(1) != 'L';
        }
        return signature.charAt(0) != 'L';
    }

    /**
     * パッケージ情報も含めたシグネチャを取得します。
     * <p>
     * ex. Ljava/util/Collection<Lb/LimyClassObject;>;<br />
     * ex. Ljava/util/Map<Ljava/lang/String;Lb/LimyClassObject;>;<br />
     * </p>
     * @return パッケージ情報も含めたシグネチャ
     */
    public String getParameterClass() {
        return parameterClass;
//        if (parameterClass == null) {
//            return null;
//        }
//        // signature : Ljava/util/Collection<Lb/LimyClassObject;>;
//        int index = parameterClass.indexOf('<');
//        int lastPos = parameterClass.indexOf(';', index);
//        return parameterClass.substring(index + 2, lastPos).replace('/', '.');
//        // Ljava/util/Collection<Lb/LimyClassObject;>; -> b/LimyClassObject
//        // Ljava/util/Map<Ljava/lang/String;Lb/LimyClassObject;>; -> b/LimyClassObject
    }
    
}
