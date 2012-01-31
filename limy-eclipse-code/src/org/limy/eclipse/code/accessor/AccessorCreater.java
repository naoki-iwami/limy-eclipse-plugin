/*
 * Created 2004/11/15
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
package org.limy.eclipse.code.accessor;

import static org.limy.eclipse.code.LimyCodeConstants.INDENT_STR;
import static org.limy.eclipse.code.LimyCodeConstants.JAVADOC_CONT;
import static org.limy.eclipse.code.LimyCodeConstants.JAVADOC_END;
import static org.limy.eclipse.code.LimyCodeConstants.JAVADOC_START;
import static org.limy.eclipse.code.LimyCodeConstants.RIGHT_BRACKET;
import static org.limy.eclipse.common.LimyEclipseConstants.NL;

import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.compiler.env.ISourceField;
import org.eclipse.jdt.internal.core.JavaElement;
import org.eclipse.jdt.internal.corext.codemanipulation.GetterSetterUtil;
import org.limy.eclipse.code.LimyCodeConstants;
import org.limy.eclipse.code.LimyCodePlugin;

/**
 * メソッド定義文を生成するユーティリティクラスです。
 * @author Naoki Iwami
 */
public final class AccessorCreater {
    
    // ------------------------ Constructors

    /**
     * private constructor
     */
    private AccessorCreater() { }

    // ------------------------ Public Methods

    /**
     * getterメソッド定義文（Javadoc含）を生成します。
     * @param field フィールドオブジェクト
     * @param fieldComment フィールドコメント
     * @param fieldAnnotation フィールドアノテーションヒント
     * @param flagName アクセス識別子文字列
     * @param method メソッドオブジェクト
     * @return getterメソッド定義文
     * @throws JavaModelException
     */
    public static String createGetterContents(
            IField field, String fieldComment, String fieldAnnotation,
            String flagName, IMethod method)
            throws JavaModelException {
        
        // フィールド情報を取得
        ISourceField elementInfo =
            (ISourceField)((JavaElement)field).getElementInfo();
        // フィールド型名を取得
        String typeName = new String(elementInfo.getTypeName());
        // フィールド名を取得
        String fieldName = field.getElementName();
        String getFieldName = GetterSetterUtil.getGetterName(field, null);
        
        String tmpFieldComment = fieldComment;
        if (fieldComment == null || fieldComment.length() == 0) {
            tmpFieldComment = fieldName;
        }
        
        StringBuilder buff = new StringBuilder(256);
        appendJavadocGetter(buff, tmpFieldComment);
        
        if (method != null && method.exists()) {
            appendMethodSourceAlready(method, buff, flagName);
        } else {
            if (fieldAnnotation != null && fieldAnnotation.length() > 0) {
                buff.append(INDENT_STR).append(fieldAnnotation).append(NL);
            }
            buff.append(INDENT_STR).append(flagName).append(" ").append(typeName);
            buff.append(' ').append(getFieldName).append("() {").append(NL);
            buff.append("        return ").append(fieldName).append(';').append(NL);
            buff.append(INDENT_STR).append(RIGHT_BRACKET).append(NL).append(NL);
        }
        return buff.toString();
    }

    /**
     * setterメソッド定義文（Javadoc含）を生成します。
     * @param field フィールドオブジェクト
     * @param fieldComment フィールドコメント
     * @param flagName アクセス識別子文字列
     * @param method メソッドオブジェクト
     * @return setterメソッド定義文
     * @throws JavaModelException
     */
    public static String createSetterContents(
            IField field, String fieldComment, String flagName, IMethod method)
            throws JavaModelException {
            
        ISourceField elementInfo =
            (ISourceField)((JavaElement)field).getElementInfo(); // フィールド情報を取得
        String typeName = new String(elementInfo.getTypeName()); // フィールド型名を取得
        String fieldName = field.getElementName(); // フィールド名を取得
        String myFieldName = fieldName;
        if (myFieldName.charAt(0) == '_') {
            myFieldName = myFieldName.substring(1);
        }
        if (method != null) {
            // 既存メソッドのコメントを生成するときは、メソッド仮引数名を使用する
            myFieldName = method.getParameterNames()[0];
        }
        
        String setFieldName = GetterSetterUtil.getSetterName(field, null);

        String tmpFieldComment = fieldComment;
        if (fieldComment == null || fieldComment.length() == 0) {
            tmpFieldComment = myFieldName;
        }

        StringBuilder buff = new StringBuilder(256);
        appendJavadocSetter(buff, tmpFieldComment, myFieldName);
        
        if (method != null && method.exists()) {
            appendMethodSourceAlready(method, buff, flagName);
        } else {
            buff.append(INDENT_STR).append(flagName).append(" void ");
            buff.append(setFieldName).append('(').append(typeName).append(' ');
            buff.append(myFieldName).append(") {").append(NL);
            buff.append("        this.").append(fieldName).append(" = ");
            buff.append(myFieldName).append(';').append(NL);
            buff.append(INDENT_STR).append(RIGHT_BRACKET).append(NL).append(NL);
        }

        return buff.toString();
    }
    
    // ------------------------ Private Methods

    /**
     * 文字列バッファにGetterメソッドのJavadocコメントを追加します。
     * @param buff 文字列バッファ
     * @param fieldComment フィールドコメント
     */
    private static void appendJavadocGetter(StringBuilder buff,
            String fieldComment) {
        
        buff.append(INDENT_STR).append(JAVADOC_START).append(NL);
        buff.append(INDENT_STR).append(JAVADOC_CONT).append(fieldComment);
        buff.append(LimyCodePlugin.getDefault().getPreferenceStore().getString(
                LimyCodeConstants.PREF_GETTER_DESC)).append(NL);
        buff.append(INDENT_STR).append(" * @return ").append(fieldComment).append(NL);
        buff.append(INDENT_STR).append(JAVADOC_END).append(NL);
    }

    /**
     * 文字列バッファにSetterメソッドのJavadocコメントを追加します。
     * @param buff 文字列バッファ
     * @param fieldComment フィールドコメント
     * @param fieldName フィールド名（メソッドパラメータの仮引数名）
     */
    private static void appendJavadocSetter(
            StringBuilder buff, String fieldComment, String fieldName) {
        buff.append(INDENT_STR).append(JAVADOC_START).append(NL);
        buff.append(INDENT_STR).append(JAVADOC_CONT).append(fieldComment);
        buff.append(LimyCodePlugin.getDefault().getPreferenceStore().getString(
                LimyCodeConstants.PREF_SETTER_DESC)).append(NL);
        buff.append(INDENT_STR).append(" * @param ").append(fieldName).append(' ');
        buff.append(fieldComment).append(NL);
        buff.append(INDENT_STR).append(JAVADOC_END).append(NL);
    }

    /**
     * 既存メソッドからメソッド定義文（Javadoc部を除く）を生成します。
     * @param method メソッドオブジェクト
     * @param buff 文字バッファ
     * @param flagName アクセス識別子文字列
     * @throws JavaModelException
     */
    private static void appendMethodSourceAlready(
            IMethod method, StringBuilder buff, String flagName) throws JavaModelException {
        String source = method.getSource();
        String flagString = AccessorUtils.getFlagString(method.getFlags());
        if (source.startsWith(JAVADOC_START)) {
            // "/**"で始まっている場合
            
            // Javadocの終わりを検出
            int index = source.indexOf("*/") + 2;
            while (Character.isWhitespace(source.charAt(index))) {
                ++index;
            }
            
            // アクセス識別子を検索
            int startIndex = source.indexOf(flagString);

            // アクセス識別子以前をappend
            String line = source.substring(index, startIndex);
            // 終端のスペース（改行は含まない）を除いてappend
            int lastPos = line.length() - 1;
            for (; lastPos >= 0; --lastPos) {
                char c = line.charAt(lastPos);
                if (c == 9 || c == ' ') {
                    continue;
                }
                break;
            }
            if (lastPos >= 0) {
                buff.append(INDENT_STR).append(line.substring(0, lastPos + 1));
            }
            
            // public などのアクセス識別子をappend（今回既存のものと変わる可能性があるので）
            buff.append(INDENT_STR).append(flagName);
            
            // メソッド定義文をappend
            buff.append(source.substring(startIndex + flagString.length())).append(NL).append(NL);
            
        } else {

            // アクセス識別子を検索
            int startIndex = source.indexOf(flagString);
            // アクセス識別子以前をappend
            buff.append(INDENT_STR).append(source.substring(0, startIndex));
            // アクセス識別子をappend
            buff.append(flagName);
            
            buff.append(source.substring(startIndex + flagString.length())).append(NL).append(NL);
        }
    }

}
