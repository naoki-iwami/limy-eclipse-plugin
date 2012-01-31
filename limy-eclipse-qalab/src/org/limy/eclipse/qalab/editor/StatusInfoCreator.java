/*
 * Created 2007/02/05
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
package org.limy.eclipse.qalab.editor;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jface.text.ITextSelection;

/**
 * ステータスバーに表示する文字列を作成するインターフェイスです。
 * @author Naoki Iwami
 */
public interface StatusInfoCreator {

    /**
     * エディタ内で選択された要素に対してのステータス文字列を作成します。
     * @param cunit Javaクラス
     * @param textSelection 選択範囲
     * @return ステータス文字列
     */
    String create(ICompilationUnit cunit, ITextSelection textSelection);

}
