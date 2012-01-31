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

import java.io.File;
import java.io.FilenameFilter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.ant.core.AntCorePlugin;
import org.eclipse.ant.core.AntRunner;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.ui.progress.UIJob;
import org.limy.eclipse.common.LimyEclipsePluginUtils;
import org.limy.eclipse.core.LimyEclipsePlugin;
import org.limy.eclipse.qalab.LimyQalabPluginUtils;

/**
 * Qalabレポートを作成するJobクラスです。
 * @depend - - - QalabBuildListener
 * @author Naoki Iwami
 */
public class CreateReportJob extends Job {

    /**
     * 拡張子がjarのファイルを検索するフィルタクラスです。
     * @author Naoki Iwami
     */
    private static final class JarFileFilter implements FilenameFilter {
        public boolean accept(File dir, String name) {
            return name.endsWith(".jar");
        }
    }

    /**
     * 実行するbuild.xmlリソース
     */
    private IResource buildXml;
    
    /**
     * 
     */
    private UIJob afterProgress;
    
    // ------------------------ Constructors

    public CreateReportJob(IResource buildXml, String name) {
        super(name);
        this.buildXml = buildXml;
    }

    public CreateReportJob(IResource buildXml, String name, UIJob afterProgress) {
        super(name);
        this.buildXml = buildXml;
        this.afterProgress = afterProgress;
    }

    // ------------------------ Override Methods

    @Override
    protected IStatus run(IProgressMonitor monitor) {

        try {
            execTargetWithRunner(monitor);
        } catch (CoreException e) {
            LimyEclipsePluginUtils.log(e);
        }
        
        if (afterProgress != null) {
            afterProgress.run(monitor);
        }
        return Status.OK_STATUS;
    }

    // ------------------------ Private Methods

    /**
     * Antターゲットを実行します（AntRunner経由）。
     * @param monitor 遷移モニタ
     * @throws CoreException コア例外
     */
    private void execTargetWithRunner(IProgressMonitor monitor) throws CoreException {
        AntRunner runner = new AntRunner();
        runner.setBuildFileLocation(buildXml.getLocation().toString());
        
        FilenameFilter filter = new JarFileFilter();
        
        List<File> files = new ArrayList<File>();
        files.add(new File(LimyQalabPluginUtils.getPath(""))); // 配布用
        files.add(new File(LimyQalabPluginUtils.getPath("bin"))); // 開発用
        
        File root = LimyEclipsePlugin.getDefault().getPluginRoot();
        files.add(root); // 配布用
        files.add(new File(root, "bin")); // 開発用
        files.add(new File(root, "resource/lib/velocity-dep-1.5.jar"));

        files.addAll(Arrays.asList(
                new File(LimyQalabPluginUtils.getResourcePath("lib")).listFiles(filter)));
        files.addAll(Arrays.asList(
                new File(LimyQalabPluginUtils.getResourcePath("external-lib")).listFiles(filter)));
        
        List<URL> urls = new ArrayList<URL>();
        for (File file : files) {
            try {
                urls.add(file.toURI().toURL());
            } catch (MalformedURLException e) {
                LimyEclipsePluginUtils.log(e);
            }
        }
        urls.addAll(Arrays.asList(AntCorePlugin.getPlugin().getPreferences().getURLs()));
        runner.setCustomClasspath(urls.toArray(new URL[urls.size()]));
        runner.addBuildListener(QalabBuildListener.class.getName());
        runner.run(monitor);
        
    }

//    /**
//     * Antターゲットを実行します（コマンドプロンプト経由）。
//     * @param targetName ターゲット名
//     * @return 標準／エラー出力内容
//     * @throws IOException I/O例外
//     */
//    private ExecResult execTargetWithCmd(String targetName) throws IOException {
//        String sep = System.getProperty("path.separator");
//        
//        FilenameFilter filter = new FilenameFilter() {
//            public boolean accept(File dir, String name) {
//                return name.endsWith(".jar");
//            }
//        };
//        List<File> files = new ArrayList<File>();
//        files.addAll(Arrays.asList(
//                new File(LimyQalabUtils.getResourcePath("lib")).listFiles(filter)));
//        files.addAll(Arrays.asList(
//                new File(LimyQalabUtils.getResourcePath("external-lib")).listFiles(filter)));
//        
//        StringBuilder classpath = new StringBuilder();
//        for (File file : files) {
//            classpath.append(file.getAbsolutePath());
//            classpath.append(sep);
//        }
//        
//        String antExec = Platform.getOS().startsWith("win") ? "ant.bat" : "ant";
//        
////        Writer out = new CharArrayWriter();
//        OutputStreamWriter out = new OutputStreamWriter(System.out);
//        
//        int value = ProcessUtils.execProgram(buildXml.getParent().getLocation().toFile(),
//                out,
//                antExec,
//                "-f",
//                buildXml.getName(),
//                targetName);
//        
//        ExecResult result = new ExecResult();
//        result.setExitValue(value);
//        result.setDisp(out.toString());
//        return result;
//    }
    
}
