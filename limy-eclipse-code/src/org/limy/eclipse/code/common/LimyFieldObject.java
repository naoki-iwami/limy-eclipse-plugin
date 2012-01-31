/*
 * Created 2005/09/13
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
package org.limy.eclipse.code.common;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.JavaModelException;
import org.limy.eclipse.common.LimyEclipseConstants;

/**
 * フィールドオブジェクトを表します。
 * @author Naoki Iwami
 */
public class LimyFieldObject {
    
    // ------------------------ Constants

    /**
     * 注釈(Annotation)文取得パターン
     */
    private static final Pattern PAT_ANNOTATION = Pattern.compile("//\\s*(@\\w+)$");
    
    /**
     * Javadocコメント取得パターン
     */
    private static final Pattern PAT_COMMENT = Pattern.compile("/\\*\\*(.*)\\*/");
    

    /**
     * Javadocコメント取得パターン(複数行対応)
     */
    private static final Pattern PAT_COMMENT_MULTI
            = Pattern.compile("/\\*\\*\n\\s*\\*\\s*([^\n]*)", Pattern.MULTILINE);
    
    // ------------------------ Fields

    /**
     * JDTフィールド
     */
    private IField field;
    
    /**
     * フィールドコメント
     */
    private String comment;

    /**
     * 注釈(Annotation)文
     */
    private String annotationHint;

    // ------------------------ Constructors

    /**
     * LimyFieldObjectインスタンスを構築します。
     * @param field JDTフィールド情報
     * @throws JavaModelException Javaモデル例外
     */
    public LimyFieldObject(IField field) throws JavaModelException {
        
        this.field = field;
        
        // フィールドコメントを取得
        this.comment = getFieldComment(field);
        
        // 注釈(Annotation)文を取得
        this.annotationHint = getFieldAnnotationHint(field);

    }
    
    // ------------------------ Private Methods

    /**
     * フィールドコメントを取得します。
     * @param field フィールドオブジェクト
     * @return フィールドコメント
     * @throws JavaModelException
     */
    private String getFieldComment(IField field) throws JavaModelException {
        String fieldComment = LimyEclipseConstants.EMPTY_STR;
        Matcher matcher = PAT_COMMENT.matcher(field.getSource());
        if (matcher.find()) {
            fieldComment = matcher.group(1).trim();
        } else {
            matcher = PAT_COMMENT_MULTI.matcher(
                    field.getSource().replaceAll("\r\n", "\n"));
            if (matcher.find()) {
                fieldComment = matcher.group(1).trim();
            }
        }
        return fieldComment;
    }

    /**
     * 注釈(Annotation)文を取得します。
     * @param field フィールドオブジェクト
     * @return 注釈(Annotation)文
     * @throws JavaModelException Javaモデル例外
     */
    private String getFieldAnnotationHint(IField field) throws JavaModelException {
        
        // type name = ...; // @fdsaf

        String result = LimyEclipseConstants.EMPTY_STR;
        Matcher matcher = PAT_ANNOTATION.matcher(field.getSource());
        if (matcher.find()) {
            result = matcher.group(1).trim();
        }
        return result;
    }

    // ------------------------ Getter/Setter Methods

    /**
     * JDTフィールドを取得します。
     * @return JDTフィールド
     */
    public IField getField() {
        return field;
    }

    /**
     * JDTフィールドを設定します。
     * @param field JDTフィールド
     */
    public void setField(IField field) {
        this.field = field;
    }

    /**
     * フィールドコメントを取得します。
     * @return フィールドコメント
     */
    public String getComment() {
        return comment;
    }

    /**
     * フィールドコメントを設定します。
     * @param comment フィールドコメント
     */
    public void setComment(String comment) {
        this.comment = comment;
    }

    /**
     * 注釈(Annotation)文を取得します。
     * @return 注釈(Annotation)文
     */
    public String getAnnotationHint() {
        return annotationHint;
    }

    /**
     * 注釈(Annotation)文を設定します。
     * @param annotationHint 注釈(Annotation)文
     */
    public void setAnnotationHint(String annotationHint) {
        this.annotationHint = annotationHint;
    }

}
