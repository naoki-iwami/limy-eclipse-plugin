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
package org.limy.eclipse.qalab.action;

import java.io.CharArrayWriter;
import java.io.File;
import java.io.IOException;
import java.io.Writer;

import org.apache.commons.io.filefilter.PrefixFileFilter;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.ui.progress.WorkbenchJob;
import org.limy.common.ProcessUtils;
import org.limy.eclipse.common.LimyEclipsePluginUtils;
import org.limy.eclipse.common.io.LimyIOUtils;
import org.limy.eclipse.common.ui.AbstractJavaElementAction;
import org.limy.eclipse.common.ui.LimyUIUtils;
import org.limy.eclipse.qalab.LimyQalabPluginUtils;
import org.limy.eclipse.qalab.ant.BuildWarning;
import org.limy.eclipse.qalab.ant.CreateBuildXml;
import org.limy.eclipse.qalab.common.LimyQalabConstants;
import org.limy.eclipse.qalab.common.LimyQalabEnvironment;

/**
 * build.xmlを作成するアクションです。
 * @depend - - - ProcessUtils
 * @depend - - - CreateBuildXml
 * @author Naoki Iwami
 */
public class CreateBuildXmlAction extends AbstractJavaElementAction {
    
    @Override
    protected void doAction(IJavaElement javaElement, IProgressMonitor monitor)
            throws CoreException {

        CreateBuildXml builder = new CreateBuildXml();
        
        final LimyQalabEnvironment env = LimyQalabPluginUtils.createEnv(
                javaElement.getJavaProject().getProject());
        
        final BuildWarning warningInfo = builder.prepareBuildFiles(env);
        builder.createFiles(env);
        
        new WorkbenchJob("info") {
            @Override
            public IStatus runInUIThread(IProgressMonitor monitor) {
                QalabActionUtils.showConfirmDialog(warningInfo,
                        "build.xmlを作成しました。\n"
                        + "Antの実行はコマンドプロンプトから行って下さい。\n"
                        + "Eclipse内からAntを実行すると、処理が固まったりする場合があります。");
                checkAntEnvironment(env);
                return Status.OK_STATUS;
            }
        }
        .schedule();
    }

    // ------------------------ Private Methods

    /**
     * Antのインストール環境をチェックします。
     * @param env 環境設定
     */
    private void checkAntEnvironment(LimyQalabEnvironment env) {

        String antExe = "ant";
        if (Platform.OS_WIN32.equals(Platform.getOS())) {
            antExe = "ant.bat";
        }

        Writer out = new CharArrayWriter();

        try {
            ProcessUtils.execProgram(new File("."), out, antExe, "-version");
        } catch (IOException e) {
            // antが見つからない
            LimyUIUtils.showConfirmDialog(
                    "実行パス内に ant.bat が見つかりません。\n"
                    + "おそらくこのままではコマンドプロンプトからAntを実行できません。\n"
                    + "ant.bat のあるディレクトリにパスを通して下さい。");
            return;
        }
        
        String antHome = System.getenv("ANT_HOME");
        if (antHome == null) {
            LimyUIUtils.showConfirmDialog(
                    "環境変数ANT_HOMEが指定されていません。\n"
                    + "指定しておくと、プラグイン側でAntの場所を判別して\n"
                    + "必要なjarファイルを自動でコピーする等の補助機能が有効になります。");
            return;
        }
        
        File antLibDir = new File(antHome, "lib");
        if (env.getStore().getBoolean(LimyQalabConstants.ENABLE_JDEPEND)) {
            if (antLibDir.list(new PrefixFileFilter("jdepend-")).length == 0) {
                // jdepend-XX.jar が見つからない場合、自動でANT_HOME/libへコピー
                String jdependFileName = "jdepend-2.9.jar";
                String path = LimyQalabPluginUtils.getResourcePath(
                        "external-lib/" + jdependFileName);
                try {
                    LimyIOUtils.copyFile(new File(path), new File(antLibDir, jdependFileName));
                    LimyUIUtils.showConfirmDialog(
                            jdependFileName + " を ANT_HOME/lib にコピーしました。");
                } catch (IOException e) {
                    LimyEclipsePluginUtils.log(e);
                }
            }
        }

        if (env.getStore().getBoolean(LimyQalabConstants.ENABLE_JUNIT)) {
            if (antLibDir.list(new PrefixFileFilter("junit-")).length == 0) {
                // junit-XX.jar が見つからない場合、自動でANT_HOME/libへコピー
                String junitFileName = "junit-4.1.jar";
                String path = LimyQalabPluginUtils.getResourcePath(
                        "external-lib/" + junitFileName);
                try {
                    LimyIOUtils.copyFile(new File(path), new File(antLibDir, junitFileName));
                    LimyUIUtils.showConfirmDialog(
                            junitFileName + " を ANT_HOME/lib にコピーしました。");
                } catch (IOException e) {
                    LimyEclipsePluginUtils.log(e);
                }
            }
        }

    }

}
