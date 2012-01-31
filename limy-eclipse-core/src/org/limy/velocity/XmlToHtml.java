/*
 * Created 2007/08/14
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
package org.limy.velocity;

import java.io.File;
import java.io.IOException;
import java.util.Collection;

import org.apache.velocity.context.Context;


/**
 * XML -> HTML の変換を担当します。
 * @author Naoki Iwami
 */
public interface XmlToHtml {

    /**
     * XMLファイルおよびパラメータを入力としてHTMLを出力します。
     * @param inXmls 入力XMLファイル
     * @param vmTemplate VMテンプレートファイル
     * @param out 出力先ファイル
     * @param params パラメータ一覧
     * @throws IOException I/O例外
     */
    void createHtml(Collection<File> inXmls, File vmTemplate, File out,
            Collection<VmParam> params) throws IOException;

    /**
     * Contextおよびパラメータを入力としてHTMLを出力します。
     * @param context コンテキスト
     * @param vmTemplate VMテンプレートファイル
     * @param out 出力先ファイル
     * @param params パラメータ一覧
     * @throws IOException I/O例外
     */
    void createHtml(Context context,
            File vmTemplate, File out,
            Collection<VmParam> params) throws IOException;
    

}
