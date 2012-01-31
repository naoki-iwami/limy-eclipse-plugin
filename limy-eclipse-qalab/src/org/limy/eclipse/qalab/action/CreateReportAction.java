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

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.progress.WorkbenchJob;
import org.limy.eclipse.common.ui.AbstractJavaElementAction;
import org.limy.eclipse.qalab.LimyQalabPluginUtils;
import org.limy.eclipse.qalab.ant.BuildWarning;
import org.limy.eclipse.qalab.ant.CreateBuildXml;
import org.limy.eclipse.qalab.common.LimyQalabConstants;
import org.limy.eclipse.qalab.common.LimyQalabEnvironment;

/**
 * QAレポート作成アクションクラスです。
 * @depend - - - CreateReportJob
 * @depend - - - CreateBuildXml
 * @author Naoki Iwami
 */
public class CreateReportAction extends AbstractJavaElementAction {

    @Override
    protected void doAction(IJavaElement javaElement, IProgressMonitor monitor)
            throws CoreException {
        
        IProject project = javaElement.getJavaProject().getProject();
        LimyQalabEnvironment env = LimyQalabPluginUtils.createEnv(project);
        CreateBuildXml builder = new CreateBuildXml();
        
        final BuildWarning warningInfo = builder.prepareBuildFiles(env);
        builder.createFiles(env);
        
        doReport(env);
        
        new WorkbenchJob("info") {
            @Override
            public IStatus runInUIThread(IProgressMonitor monitor) {
                QalabActionUtils.showConfirmDialog(warningInfo,
                        "Antを使用してレポートを作成します。\n"
                        + "Ant出力結果はプロジェクトルートの limy.log ファイルに出力されます。\n"
                        + "プロセスはバックグラウンドで実行されます。実行経過はProgressビューで確認できます。\n"
                        + "\n"
                        + "プロジェクトの規模によってはかなりのメモリを消費してしまい、Eclipseが固まることがあります。\n"
                        + "この場合、このコマンドは使用せずに同メニューの 'build.xml作成' を利用して\n"
                        + "コマンドプロンプトなどからAntを実行してレポートを作成して下さい。");
                return Status.OK_STATUS;
            }
        }
        .schedule();
        
    }
    
    // ------------------------ Private Methods

    /**
     * レポート作成を実行します。
     * @param env 
     * @param project プロジェクト
     */
    private void doReport(LimyQalabEnvironment env) {
        IPreferenceStore store = env.getStore();
        String buildXml = store.getString(LimyQalabConstants.KEY_BUILD_XML);
        CreateReportJob job = new CreateReportJob(
                        env.getProject().getFile(buildXml),
                        "Creating Qalab Reports...");
        job.schedule();
    }
    
}
