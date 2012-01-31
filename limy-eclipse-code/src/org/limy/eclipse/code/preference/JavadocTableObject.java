/*
 * Created 2005/07/21
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
package org.limy.eclipse.code.preference;


import org.limy.eclipse.common.jface.ITableObject;

/**
 * Javadoc生成支援情報を表すテーブルオブジェクトクラスです。
 * @author Naoki Iwami
 */
public class JavadocTableObject implements ITableObject, Comparable<JavadocTableObject> {
    
    // ------------------------ Classes
    
    /**
     * 種別
     */
    private Type type;
    
    /**
     * クラス名
     */
    private String className;
    
    /**
     * フィールド名
     */
    private String fieldName;
    
    /**
     * Javadocコメント
     */
    private String javadocComment;
    
    // ------------------------ Constructors

    /**
     * JavadocTableObjectインスタンスを構築します。
     * @param type 種別
     * @param className クラス名
     * @param fieldName フィールド名
     * @param javadocComment Javadocコメント
     */
    public JavadocTableObject(
            Type type,
            String className,
            String fieldName,
            String javadocComment) {
        this.type = type;
        this.className = className;
        this.fieldName = fieldName;
        this.javadocComment = javadocComment;
    }
    
   
    // ------------------------ Implement Methods
    
    public Object getValue(int index) {
        Object r = null;
        switch (index) {
        case 0:
            r = Integer.valueOf(getType().getValue());
            break;
        case 1:
            r = getClassName();
            break;
        case 2:
            r = getFieldName();
            break;
        case 3:
            r = getJavadocComment();
            break;
        default:
            break;
        }
        return r;
    }

    public String getViewString(int index) {
        String r = null;
        switch (index) {
        case 0:
            r = getType().getString();
            break;
        case 1:
            r = getClassName();
            break;
        case 2:
            r = getFieldName();
            break;
        case 3:
            r = getJavadocComment();
            break;
        default:
            break;
        }
        return r;
    }
    
    public int getColumnSize() {
        return 10;
    }

    public void setValue(int index, Object value) {
        switch (index) {
        case 0:
            int typeNumber = ((Integer)value).intValue();
            setType(Type.getType(typeNumber));
            if (typeNumber >= 2/*Primitive*/) {
                setClassName("");
            }
            break;
        case 1:
            setClassName((String)value);
            break;
        case 2:
            setFieldName((String)value);
            break;
        case 3:
            setJavadocComment((String)value);
            break;
        default:
            break;
        }
    }

    public int compareTo(JavadocTableObject o) {
        int c1 = getType().getValue() - o.getType().getValue();
        if (c1 != 0) {
            return c1;
        }
        return getClassName().compareTo(o.getClassName());
    }
    
    // ------------------------ Getter/Setter Methods

    /**
     * 種別を取得します。
     * @return 種別
     */
    public Type getType() {
        return type;
    }

    /**
     * 種別を設定します。
     * @param type 種別
     */
    public void setType(Type type) {
        this.type = type;
    }

    /**
     * クラス名を取得します。
     * @return クラス名
     */
    public String getClassName() {
        return className;
    }

    /**
     * クラス名を設定します。
     * @param className クラス名
     */
    public void setClassName(String className) {
        this.className = className;
    }

    /**
     * フィールド名を取得します。
     * @return フィールド名
     */
    public String getFieldName() {
        return fieldName;
    }

    /**
     * フィールド名を設定します。
     * @param fieldName フィールド名
     */
    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    /**
     * Javadocコメントを取得します。
     * @return Javadocコメント
     */
    public String getJavadocComment() {
        return javadocComment;
    }

    /**
     * Javadocコメントを設定します。
     * @param javadocComment Javadocコメント
     */
    public void setJavadocComment(String javadocComment) {
        this.javadocComment = javadocComment;
    }

}
