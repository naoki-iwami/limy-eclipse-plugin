/*
 * Created 2007/01/05
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
package org.limy.eclipse.qalab;

/**
 * マーカー用の定数クラスです。
 * @author Naoki Iwami
 */
public final class LimyQalabMarker {

    /** 全マーカーの親マーカーID */
    public static final String DEFAULT_ID = LimyQalabPlugin.PLUGIN_ID + ".LimyQalabMarker";

    /** ProblemマーカーID */
    public static final String PROBLEM_ID = LimyQalabPlugin.PLUGIN_ID + ".LimyQalabProblemMarker";

    /** テスト関連親マーカーID */
    public static final String TEST_ID = LimyQalabPlugin.PLUGIN_ID + ".LimyQalabTestMarker";
    

    /** Nonカバレッジ結果マーカーID */
    public static final String NO_COVERAGE_ID = LimyQalabPlugin.PLUGIN_ID
            + ".LimyQalabNoCoverageMarker";

    /** カバレッジ結果マーカーID */
    public static final String COVERAGE_ID = LimyQalabPlugin.PLUGIN_ID
            + ".LimyQalabCoverageMarker";

    /** テスト失敗結果マーカーID */
    public static final String FAILURE_ID = LimyQalabPlugin.PLUGIN_ID + ".LimyQalabFailureMarker";

    /** テストエラー結果マーカーID */
    public static final String ERROR_ID = LimyQalabPlugin.PLUGIN_ID + ".LimyQalabErrorMarker";

    /** テスト成功結果マーカーID */
    public static final String SUCCESS_ID = LimyQalabPlugin.PLUGIN_ID + ".LimyQalabSuccessMarker";

    /** カバレッジ結果（non-visible）マーカーID */
    public static final String COVERAGE_RESULT = LimyQalabPlugin.PLUGIN_ID
            + ".LimyQalabCoverageResult";
    
    
    /** 属性 : テスト名 */
    public static final String TEST_NAME = "testName";
    
    /** 属性 : 全Line数 */
    public static final String ALL_LINE_NUMBER = "allLineNumber";

    /** 属性 : カバレッジされたLine数 */
    public static final String COVERAGE_LINE = "coverageLineNumber";

    /** 属性 : 全Branch数 */
    public static final String ALL_BRANCH_NUMBER = "allBranchNumber";

    /** 属性 : カバレッジされたBranch数 */
    public static final String COVERAGE_BRANCH = "coverageBranchNumber";

    /** 属性 : complexity値（未サポート） */
    public static final String COMPLEXITY = "complexity";

    /** 属性 : URL */
    public static final String URL = "url";

    /**
     * private constructor
     */
    private LimyQalabMarker() { }

}
