/*
 * Created 2007/02/09
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
package org.limy.eclipse.qalab.action;

import org.limy.eclipse.common.swt.LimySwtUtils;
import org.limy.eclipse.qalab.ant.BuildWarning;

/**
 * アクション関連のユーティリティクラスです。
 * @author Naoki Iwami
 */
public final class QalabActionUtils {

    /**
     * private constructor
     */
    private QalabActionUtils() { }

    /**
     * 確認ダイアログを表示します。
     * @param warningInfo 警告内容
     * @param message 表示メッセージ
     */
    public static void showConfirmDialog(BuildWarning warningInfo, String message) {
        
        String dialogMessage;
        if (warningInfo != null && warningInfo.getWarnings().size() > 0) {
            StringBuilder buff = new StringBuilder();
            for (Object info : warningInfo.getWarnings()) {
                buff.append(info.toString());
            }
            dialogMessage = buff.toString();
        } else {
            dialogMessage = message;
        }
        LimySwtUtils.showAlertDialog(dialogMessage);
    }
    
}
