/*
 * Created 2007/06/23
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
package org.limy.eclipse.qalab.action.toolbar;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.limy.common.ProcessUtils;
import org.limy.eclipse.common.io.LimyIOUtils;
import org.limy.eclipse.common.jdt.LimyJavaUtils;
import org.limy.eclipse.common.ui.LimyUIUtils;
import org.limy.eclipse.qalab.LimyQalabPluginUtils;
import org.limy.eclipse.qalab.action.QalabActionUtils;
import org.limy.eclipse.qalab.ant.CreateBuildXml;
import org.limy.eclipse.qalab.common.LimyQalabConstants;
import org.limy.eclipse.qalab.common.LimyQalabEnvironment;
import org.limy.eclipse.qalab.common.LimyQalabUtils;

/**
 * 各種計測を実行する既定クラスです。
 * @author Naoki Iwami
 */
public abstract class AbstractPartAction
        implements IWorkbenchWindowActionDelegate, ToolbarAction {
    
    // ------------------------ Fields
    
    /** 設定 */
    private LimyQalabEnvironment env;
    
    /** 出力先 */
    private Writer writer;
    
    /** 現在のワークベンチウィンドウ */
    private IWorkbenchWindow window;
    
    /** 現在の選択範囲 */
    private ISelection selection;
    
    /** 遷移モニタ */
    private IProgressMonitor progressMonitor;

    // ------------------------ Abstract Methods

    /**
     * @return
     * @throws IOException 
     */
    public abstract String[] getTargetNames() throws IOException;
    
    /**
     * レポートファイルのURLを返します。
     * @return レポートファイルのURL
     */
    protected abstract File getReportHtml();

    // ------------------------ Implement Methods

    public void init(IWorkbenchWindow window) {
        this.window = window;
    }

    public void selectionChanged(IAction action, ISelection selection) {
        this.selection = selection;
    }

    public void run(final IAction action) {
        Job job = new Job(getClass().getSimpleName()) {

            @Override
            protected IStatus run(IProgressMonitor monitor) {
                try {
                    progressMonitor = monitor;
                    makeReport();
                    LimyUIUtils.openBrowser(getReportHtml().toURI().toURL());
//                    if (action instanceof ISelectionListener) {
//                        LimyCompatibleUtils.openBrowser(getReportHtml().toURI().toURL());
//                    }
                } catch (final CoreException e) {
                    window.getShell().getDisplay().syncExec(new Runnable() {
                        public void run() {
                            QalabActionUtils.showConfirmDialog(null, e.getMessage());
                        }
                    });
                    return Status.CANCEL_STATUS;
                } catch (final IOException e) {
                    window.getShell().getDisplay().syncExec(new Runnable() {
                        public void run() {
                            QalabActionUtils.showConfirmDialog(null, e.getMessage());
                        }
                    });
                    return Status.CANCEL_STATUS;
                } finally {
                    try {
                        FileUtils.writeByteArrayToFile(getDestFile(".eclipse.log"),
                                getWriter().toString().getBytes());
                    } catch (IOException e) {
                        return Status.CANCEL_STATUS;
                    }
                }
                return Status.OK_STATUS;
            }
        };
        job.schedule();
    }

    public void dispose() {
        // do nothing
    }
    
    // ------------------------ Protected Methods
    
    /**
     * このアクションが有効かどうかをチェックします。
     * <p>
     * 実装クラス内で、必要ならばこのメソッドをオーバーライドして下さい。
     * </p>
     * @return 有効ならばtrue
     */
    protected boolean checkActionEnabled() {
        return true;
    }

    // ------------------------ Protected Methods (final)

    /**
     * 設定を取得します。
     * @return 設定
     */
    protected final LimyQalabEnvironment getEnv() {
        return env;
    }
    
    /**
     * プロジェクトルートディレクトリを返します。
     * @return プロジェクトルートディレクトリ
     */
    protected final File getBaseDir() {
        return env.getProject().getLocation().toFile();
    }

    /**
     * ログ出力先を返します。
     * @return ログ出力先
     */
    protected final Writer getWriter() {
        return writer;
    }
    
    /**
     * DESTディレクトリ内ファイルを返します。
     * @param relativePath 相対パス
     * @return DESTディレクトリ内ファイル
     */
    protected final File getDestFile(String relativePath) {
        return new File(getDestDir(), relativePath);
    }

    /**
     * 遷移モニタを取得します。
     * @return 遷移モニタ
     */
    protected final IProgressMonitor getProgressMonitor() {
        return progressMonitor;
    }

    protected final String getAntPath() {
        
        String antExe = "ant";
        if (Platform.OS_WIN32.equals(Platform.getOS())) {
            antExe = "ant.bat";
        }

        String antHome = System.getenv("ANT_HOME");
        if (antHome != null) {
            antExe = new File(new File(antHome, "bin"), antExe).getAbsolutePath();
        }

        return antExe;
    }
    
    protected final String getBuildXml() {
        return getEnv().getStore().getString(LimyQalabConstants.KEY_BUILD_XML);
    }
    
    // ------------------------ Private Methods

    /**
     * 計測結果出力先ディレクトリを返します。
     * @return 計測結果出力先ディレクトリ
     */
    private File getDestDir() {
        IProject project = env.getProject();
        IPreferenceStore store = env.getStore();
        File projectDir = project.getLocation().toFile();
        return new File(projectDir, store.getString(LimyQalabConstants.KEY_DEST_DIR));
    }

    /**
     * レポート出力処理を実行します。
     * @throws CoreException
     * @throws IOException
     */
    private void makeReport() throws CoreException, IOException {
        
        // TODO envがdisableならば処理をしない
        writer = new StringWriter();
        List<IJavaElement> javaElements = getSelectedJavaElements();
        if (!javaElements.isEmpty()) {
            env = LimyQalabPluginUtils.createEnv(
                    javaElements.iterator().next().getJavaProject().getProject());
            createBuildXml();
            makeReportWithAnt();
            File file = LimyQalabUtils.createTempFile(env.getProject(), "report.log");
            LimyIOUtils.saveFile(file, writer.toString().getBytes());
        }
    }

    /**
     * 選択された全Java要素を返します。
     * @return 選択された全Java要素
     */
    private List<IJavaElement> getSelectedJavaElements() {
        final List<IJavaElement> javaElements = new ArrayList<IJavaElement>();
        window.getShell().getDisplay().syncExec(new Runnable() {
            public void run() {
                try {
                    javaElements.addAll(LimyJavaUtils.getSelectedJavaElements(window, selection));
                } catch (JavaModelException e) {
                    QalabActionUtils.showConfirmDialog(null, e.getMessage());
                }
            }
        });
        return javaElements;
    }
    
    /**
     * Ant実行用のbuild.xmlファイルを作成します。
     * @throws CoreException
     * @throws IOException
     */
    private void createBuildXml() throws CoreException, IOException {
        CreateBuildXml builder = new CreateBuildXml();
        builder.prepareBuildFiles(env);
        builder.createFiles(env);
    }
    
    /**
     * Antを実行します。build.xml は既に存在している必要があります。
     * <p>
     * ターゲット名は getTargetNames() メソッドから取得します。
     * </p>
     * @throws IOException
     * @see {@link AbstractPartAction#getTargetNames()}
     */
    private void makeReportWithAnt() throws IOException {
        Collection<String> args = new ArrayList<String>();
        args.add(getAntPath());
        args.add("-f");
        args.add(getBuildXml());

        args.addAll(Arrays.asList(getTargetNames()));
        
        ProcessUtils.execProgram(getBaseDir(), getWriter(),
                args.toArray(new String[args.size()]));
        
    }
    
}
