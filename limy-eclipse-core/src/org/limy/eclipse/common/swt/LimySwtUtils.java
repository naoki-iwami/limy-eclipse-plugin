/*
 * Created 2007/08/21
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
package org.limy.eclipse.common.swt;

import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.PreferencesUtil;
import org.limy.eclipse.core.LimyEclipseResourceConstants;

/**
 * @author Naoki Iwami
 */
public final class LimySwtUtils {
    
    /**
     * private constructor
     */
    private LimySwtUtils() { }

    /**
     * 確認ダイアログを表示します。
     * @param message 表示メッセージ
     */
    public static void showAlertDialog(String message) {
        MessageBox dialog = new MessageBox(new Shell(), SWT.OK);
        dialog.setText(LimyEclipseResourceConstants.CONFIRM);
        dialog.setMessage(message);
        dialog.open();
    }

    /**
     * 確認ダイアログ（Yes / Cancel）を表示します。
     * @param message 表示メッセージ
     * @return OKが押されたらtrue
     */
    public static boolean showConfirmDialog(String message) {
        MessageBox dialog = new MessageBox(new Shell(), SWT.OK | SWT.CANCEL);
        dialog.setText(LimyEclipseResourceConstants.CONFIRM);
        dialog.setMessage(message);
        return dialog.open() == SWT.OK;
    }
    
    /**
     * 指定したIDの PreferencePage を開きます。
     * @param shell シェル
     * @param preferencePageId PreferencePageのID
     * @return PreferencePage でOKが押されたら true
     */
    public static boolean openPreferencePage(Shell shell, String preferencePageId) {

        PreferenceDialog dialog = PreferencesUtil.createPreferenceDialogOn(
                shell, preferencePageId, new String[] { preferencePageId }, null);
        return dialog.open() == Window.OK;
    }

}
