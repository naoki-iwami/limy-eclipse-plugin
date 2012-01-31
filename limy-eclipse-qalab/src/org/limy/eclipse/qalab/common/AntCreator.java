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

import java.io.IOException;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.preference.IPreferenceStore;
import org.limy.xml.XmlElement;


/**
 * Ant生成可能なインターフェイスです。
 * @author Naoki Iwami
 */
public interface AntCreator {

    // ------------------------ Abstract Methods

    /**
     * Ant要素を作成します。
     * @param root XMLルート要素
     * @param env 
     * @throws IOException I/O例外
     * @throws CoreException コア例外
     */
    void exec(XmlElement root, LimyQalabEnvironment env)
            throws IOException, CoreException;

    /**
     * レポート出力先のXMLファイル名を取得します。
     * @return レポート出力先のXMLファイル名
     */
    String[] getReportXmlNames();
    
    /**
     * QalabのStatMergeクラス名一覧を取得します。
     * @return QalabのStatMergeクラス名一覧
     */
    String[] getQalabClassNames();
    
    /**
     * Qalabサマリ用グループ番号を取得します。
     * <p>
     * -1 : 特殊
     * 0 : QALab計測あり
     * 1 : QALab計測あり(Covertura)
     * 2 : QALab計測なし。ノーマル&レポート
     * 3 : QALab計測なし。レポートonly（ターゲット名はノーマル）
     * 4 : QALab計測なし。レポートonly（ターゲット名はレポート） QALab専用
     * </p>
     * @return Qalabサマリ用グループ番号
     */
    int getSummaryGroup(); // TODO Enum化すべし
    
    /**
     * Qalabサマリ名称一覧を取得します。
     * @return Qalabサマリ名称一覧
     */
    String[] getSummaryTypes();
    
    /**
     * Antターゲット名を取得します。
     * @return Antターゲット名
     */
    String getTargetName();
    
    /**
     * 自身がプロジェクト内で有効かどうかを返します。
     * @param store プロジェクトストア
     * @return プロジェクト内で有効ならばtrue
     */
    boolean isEnable(IPreferenceStore store);

    /**
     * 自身の初期化されたインスタンスを作成します。
     * @return 自身の初期化されたインスタンス
     */
    AntCreator newInstance();

}
