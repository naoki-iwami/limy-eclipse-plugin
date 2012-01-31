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
package org.limy.eclipse.code;

/**
 * Codeプラグイン内で使用される定数クラスです。
 * @author Naoki Iwami
 */
public final class LimyCodeConstants {
    
    // ------------------------ Constants
    
    /**
     * public識別子文字列
     */
    public static final String FLAGSTR_PUBLIC = "public";

    /**
     * Javadoc開始文字列
     */
    public static final String JAVADOC_START = "/**";

    /**
     * Javadoc継続文字列
     */
    public static final String JAVADOC_CONT = " * ";

    /**
     * Javadoc終了文字列
     */
    public static final String JAVADOC_END = " */";

    /**
     * 右中括弧
     */
    public static final String RIGHT_BRACKET = "}";

    /**
     * インデント文字列
     */
    public static final String INDENT_STR = "    ";

    // ------------------------ Preference Names

    /**
     * Preferenceキー : Javaファイルヘッダ文字列
     */
    public static final String PREF_JAVA_HEADER = "javaHeader";

    /**
     * Preferenceキー : Javadocカスタマイズ設定ファイルパス
     */
    public static final String PREF_PROP_PATH = "JavadocProp";

    /** Preferenceキー : Getterメソッドに記述する文字 */
    public static final String PREF_GETTER_DESC = "GetterDesc";

    /** Preferenceキー : Setterメソッドに記述する文字 */
    public static final String PREF_SETTER_DESC = "SetterDesc";

    /**
     * Preferenceキー : GNUプロジェクト名
     */
    public static final String PREF_PROJECT_NAME = "GnuProjectName";

    // ------------------------ Resource Keys

//    /**
//     * GetterメソッドのJavadocコメント
//     */
//    public static final String MES_GETTER = "getter";
//
//    /**
//     * SetterメソッドのJavadocコメント
//     */
//    public static final String MES_SETTER = "setter";

    /**
     * Add Java Headerコマンド実行時の確認メッセージ
     */
    public static final String MES_JAVA_HEADER = "add.java.header";
    
    /**
     * 文字コード変換アクション実行時のダイアログタイトル
     */
    public static final String MES_CONV_TITLE = "convert.charset.title";
    
    /**
     * 文字コード変換アクション実行時のダイアログメッセージ
     */
    public static final String MES_CONV_DIALOG = "convert.charset.dialog";

    /**
     * 文字コード変換アクション実行時の確認メッセージ
     */
    public static final String MES_CONV_CONFIRM = "convert.charset.confirm";

    // ------------------------ Constructors

    /**
     * private constructor
     */
    private LimyCodeConstants() { }

}
