/*
 * Created 2007/08/15
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
package org.limy.eclipse.code.header;

import java.io.FileInputStream;
import java.io.IOException;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.IJavaElement;
import org.limy.eclipse.code.LimyCodeConstants;
import org.limy.eclipse.code.LimyCodePlugin;
import org.limy.eclipse.common.LimyEclipsePluginUtils;
import org.limy.eclipse.common.io.LimyIOUtils;
import org.limy.eclipse.common.swt.LimySwtUtils;
import org.limy.eclipse.common.ui.AbstractJavaElementAction;

/**
 * Javaファイルにヘッダ文字列を追加するアクションクラスです。
 * @author Naoki Iwami
 */
public class AddJavaHeaderAction extends AbstractJavaElementAction {

    // ------------------------ Fields

    /** ヘッダ文字列 */
    private String header;

    // ------------------------ Override Methods

    @Override
    protected void doAction(IJavaElement javaElement, IProgressMonitor monitor)
            throws CoreException {
        
        header = getHeader();
        if (header != null) {
            AddHeaderCore core = new AddHeaderCore(header);
            core.changeResource(javaElement, monitor);
        }
    }

    @Override
    protected void execAfter(IProgressMonitor monitor) throws CoreException {
        if (header == null) {
            if (LimySwtUtils.showConfirmDialog(LimyCodePlugin.getResourceString(
            "not.specified.header.file"))) {
                LimySwtUtils.openPreferencePage(getWindow().getShell(),
                "org.limy.eclipse.code.preference.LimyCodePreferencePage");
            }
        }
    }

    /**
     * ヘッダ文字列を返します。
     * @return ヘッダ文字列
     */
    private String getHeader() {
        String headerFile = LimyCodePlugin.getDefault()
                .getPreferenceStore().getString(LimyCodeConstants.PREF_JAVA_HEADER);
        if (headerFile.length() == 0) {
            return null;
        }
        
        FileInputStream in = null;
        try {
            in = new FileInputStream(headerFile);
            return LimyIOUtils.getContent(in);
        } catch (IOException e) {
            LimyEclipsePluginUtils.log(e);
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    LimyEclipsePluginUtils.log(e);
                }
            }
        }
        return null;
    }

}
