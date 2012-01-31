/*
 * Created 2006/08/19
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
package org.limy.eclipse.qalab.common;

/**
 * Limy Qalab用の定数クラスです。
 * @author Naoki Iwami
 * @version 1.0.0
 */
public final class LimyQalabConstants {

    // ------------------------ Preference Names
    
    /**
     * Preferenceキー : Checkstyleの設定ファイル種別
     */
    public static final String KEY_CHK_TYPE = "checkstyleConfigType";

    /**
     * Preferenceキー : Checkstyleの設定ファイル
     */
    public static final String KEY_CHK_CFG = "checkstyleConfig";

    /**
     * Preferenceキー : PMDの設定ファイル種別
     */
    public static final String KEY_PMD_TYPE = "pmdConfigType";

    /**
     * Preferenceキー : PMDの設定ファイル
     */
    public static final String KEY_PMD_CFG = "pmdConfig";

    /**
     * 設定ファイル種別値 : デフォルト
     */
    public static final int FILE_TYPE_DEFAULT = 0;

    /**
     * 設定ファイル種別値 : プロジェクト内部ファイル
     */
    public static final int FILE_TYPE_INTERNAL = 1;

    /**
     * 設定ファイル種別値 : 外部ファイル
     */
    public static final int FILE_TYPE_EXTERNAL = 2;


    /**
     * Preferenceキー : qalab.xml格納先
     */
    public static final String KEY_QALAB_XML = "qalabXml";

    /**
     * Preferenceキー : 出力先ディレクトリ
     */
    public static final String KEY_DEST_DIR = "destDir";

    /**
     * Preferenceキー : build.xmlファイル名
     */
    public static final String KEY_BUILD_XML = "buildXml";

    /**
     * Preferenceキー : build.propertiesファイル名
     */
    public static final String KEY_BUILD_PROP = "buildProperties";

    /**
     * Preferenceキー : JDepend基準パッケージ名
     */
    public static final String KEY_JDEPEND_BASE = "jdependBasePackage";

    /**
     * Preferenceキー : dotバイナリパス
     */
    public static final String KEY_DOT_EXE = "dotExe";

//    /**
//     * Preferenceキー : QA対象外ソースディレクトリ
//     */
//    public static final String IGNORE_SOURCE = "ignoreSource";
//
//    /**
//     * Preferenceキー : QA対象外パッケージ名
//     */
//    public static final String IGNORE_PACKAGE = "ignorePackage";

    /**
     * Preferenceキー : QA対象外リソース一覧
     */
    public static final String IGNORE_RESOURCES = "ignoreResources";

    /**
     * Preferenceキー : QA対象外パッケージ一覧
     */
    public static final String IGNORE_PACKAGES = "ignorePackages";

    /**
     * Preferenceキー : 有効とするサブプロジェクト名一覧
     */
    public static final String SUB_PROJECT_NAMES = "subProjectNames";

    /**
     * Preferenceキー : JDepend対象外リソース一覧
     */
    public static final String EXCLUDE_JDEPENDS = "excludeJdepends";

    /**
     * Preferenceキー : テスト時環境変数一覧
     */
    public static final String TEST_ENVS = "testEnvs";

    /**
     * Preferenceキー : テスト時除外ソースディレクトリ一覧
     */
    public static final String IGNORE_SOURCE_DIRS = "ignoreSourceDirs";

//    /**
//     * Preferenceキー : テスト対象クラス完全限定名
//     */
//    public static final String TEST_INCLUDE_NAME = "testIncludeName";
//
//    /**
//     * Preferenceキー : テスト除外クラス完全限定名
//     */
//    public static final String TEST_EXCLUDE_NAME = "testExcludeName";

//    /**
//     * Preferenceキー : Antバージョン
//     */
//    public static final String KEY_ANT_VERSION = "antVersion";

    /**
     * Antバージョン : 1.6
     */
    public static final int ANT_VERSION_16 = 0;

    /**
     * Antバージョン : 1.7
     */
    public static final int ANT_VERSION_17 = 1;

    /**
     * Preferenceキー : Checkstyle有効フラグ
     */
    public static final String ENABLE_CHECKSTYLE = "enableCheckstyle";

    /**
     * Preferenceキー : PMD有効フラグ
     */
    public static final String ENABLE_PMD = "enablePmd";

    /**
     * Preferenceキー : Findbugs有効フラグ
     */
    public static final String ENABLE_FINDBUGS = "enableFindbugs";

    /**
     * Preferenceキー : JUnit有効フラグ
     */
    public static final String ENABLE_JUNIT = "enableJUnit";

    /**
     * Preferenceキー : Ncss有効フラグ
     */
    public static final String ENABLE_NCSS = "enableNcss";

    /**
     * Preferenceキー : TO-DOレポート有効フラグ
     */
    public static final String ENABLE_TODO = "enableTodo";

    /**
     * Preferenceキー : JDependレポート有効フラグ
     */
    public static final String ENABLE_JDEPEND = "enableJdepend";

    /**
     * Preferenceキー : UmlGraphレポート有効フラグ
     */
    public static final String ENABLE_UMLGRAPH = "enableUmlgraph";

    /**
     * Preferenceキー : ファイル別QALabレポート有効フラグ
     */
    public static final String ENABLE_INDIVISUAL = "enableIndivisual";

    /**
     * Preferenceキー : テスト用ライブラリ格納ディレクトリ
     */
    public static final String TEST_LIBDIR = "testLibDir";

    /**
     * Preferenceキー : 参照プロジェクト有効フラグ
     */
    public static final String ENABLE_REFPROJECT = "enableRelatedProject";

    /**
     * Preferenceキー : Nature有効フラグ
     */
    public static final String ENABLE_NATURE = "enableNature";
    
    /** QALabエディタ用ステータスCategory名 : CC */
    public static final String QALAB_CATEGORY_CC = "qalabInfoCc";

    /** QALabエディタ用ステータスCategory名 : Coverage */
    public static final String QALAB_CATEGORY_COVERAGE = "qalabInfoCoverage";

    /**
     * Preferenceキー : UmlGraphのグラフ表示方向
     */
    public static final String UMLGRAPH_HORIZONTAL = "graphHorizontal";

    /**
     * Preferenceキー : UmlGraphでフィールド情報の連結線を描くか
     */
    public static final String UMLGRAPH_INFERREL = "graphInferrel";

    /**
     * Preferenceキー : Javadoc出力にUmlGraphを使用するか
     */
    public static final String UMLGRAPH_JAVADOC = "graphJavadoc";
    
    /**
     * Preferenceキー : ポップアップ時のイメージスケールを調整するか
     */
    public static final String ADJUST_SCALING = "adjustScaling";

    /**
     * private constructor
     */
    private LimyQalabConstants() { }
    
}
