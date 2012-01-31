/*
 * Created 2007/02/22
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
package org.limy.eclipse.qalab.propertypage;

import java.util.Collection;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.limy.eclipse.core.LimyEclipsePlugin;
import org.limy.eclipse.qalab.common.LimyQalabConstants;

/**
 * dotバイナリが指定されていない警告を処理するクラスです。
 * @author Naoki Iwami
 */
/* package */ class DotRequiredListener extends SelectionAdapter implements ModifyListener {

    /** Preferenceページ */
    private final PreferencePage page;

    /** 関連するチェックボックス */
    private final Collection<Button> dotRequiredChecks;

    // ------------------------ Constructors

    /**
     * DotRequiredListenerインスタンスを構築します。
     * @param page 
     * @param dotRequiredChecks 
     */
    /* package */ DotRequiredListener(PreferencePage page, Collection<Button> dotRequiredChecks) {
        super();
        this.page = page;
        this.dotRequiredChecks = dotRequiredChecks;
    }

    // ------------------------ Override Methods

    @Override
    public void widgetSelected(SelectionEvent e) {
        checkValid();
    }

    public void modifyText(ModifyEvent e) {
        checkValid();
    }
 
    // ------------------------ Private Methods

    protected void checkValid() {
        
        IPreferenceStore store = LimyEclipsePlugin.getDefault().getPreferenceStore();
        String dotExe = store.getString(LimyQalabConstants.KEY_DOT_EXE);

        page.setValid(true);
        page.setErrorMessage(null);
        if (dotExe.length() == 0) {
            for (Button button : dotRequiredChecks) {
                if (button.getSelection()) {
                    page.setValid(false);
                    page.setErrorMessage("dotバイナリが指定されていません。");
                    break;
                }
            }
        }
    }

}