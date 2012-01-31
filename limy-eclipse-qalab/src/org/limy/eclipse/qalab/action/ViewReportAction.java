/*
 * Created 2006/08/15
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

import java.net.MalformedURLException;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jface.preference.IPreferenceStore;
import org.limy.eclipse.common.LimyEclipsePluginUtils;
import org.limy.eclipse.common.ui.AbstractJavaElementAction;
import org.limy.eclipse.common.ui.LimyUIUtils;
import org.limy.eclipse.qalab.LimyQalabPluginUtils;
import org.limy.eclipse.qalab.common.LimyQalabConstants;
import org.limy.eclipse.qalab.common.LimyQalabEnvironment;

/**
 * QAレポート閲覧アクションクラスです。
 * @author Naoki Iwami
 */
public class ViewReportAction extends AbstractJavaElementAction {


    @Override
    protected void doAction(IJavaElement javaElement, IProgressMonitor monitor)
            throws CoreException {
        
        IJavaProject project = javaElement.getJavaProject();
        LimyQalabEnvironment env = LimyQalabPluginUtils.createEnv(project.getProject());
        IPreferenceStore store = env.getStore();

        String destDir = store.getString(LimyQalabConstants.KEY_DEST_DIR);

        IPath projectRootPath = project.getResource().getLocation();
        IPath targetFile = projectRootPath.append(destDir).append("qalab/index.html");
        try {
            LimyUIUtils.openBrowser(targetFile.toFile().toURI().toURL());
        } catch (MalformedURLException e) {
            LimyEclipsePluginUtils.log(e);
        }
        
    }


}
