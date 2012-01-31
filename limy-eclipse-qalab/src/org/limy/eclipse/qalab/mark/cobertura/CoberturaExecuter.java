/*
 * Created 2007/08/29
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
package org.limy.eclipse.qalab.mark.cobertura;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IJavaProject;
import org.junit.internal.runners.TestClassRunner;
import org.junit.runner.Result;
import org.junit.runner.notification.RunNotifier;
import org.limy.eclipse.common.LimyEclipsePluginUtils;
import org.limy.eclipse.qalab.QalabJarFileFinder;
import org.limy.eclipse.qalab.common.LimyQalabEnvironment;
import org.limy.eclipse.qalab.common.LimyQalabJavaUtils;
import org.limy.eclipse.qalab.common.QalabResourceUtils;

/**
 * Cobertura実行を担当します。
 * @author Naoki Iwami
 */
public class CoberturaExecuter {

    /** 環境設定 */
    private final LimyQalabEnvironment env;

    /** テスト結果格納先 */
    private Collection<Result> testResults = new ArrayList<Result>();

    // ------------------------ Constructors

    public CoberturaExecuter(LimyQalabEnvironment env) {
        super();
        this.env = env;
    }

    /**
     * Javaクラスのテストを実行してカバレッジを計測します。
     * @param javaResources Javaクラス一覧
     * @return 
     * @throws IOException I/O例外
     * @throws CoreException コア例外
     */
    public Collection<Result> calculateCoverage(IResource... javaResources)
            throws IOException, CoreException {
        
        IProject project = env.getProject();
        IJavaProject javaProject = env.getJavaProject();
        
        // クラスパスにinstrumentを追加
        Collection<URL> priorityUrls = new ArrayList<URL>();
        priorityUrls.add(new File(project.getLocation().toFile(), ".instrument").toURI().toURL());
        priorityUrls.add(new File(
                new QalabJarFileFinder().getFileLocation("cobertura.jar")).toURI().toURL());

        Collection<URL> urls = new ArrayList<URL>();
        Collection<String> libraries = LimyQalabJavaUtils.getJavaLibraries(javaProject);
        for (String libraryPath : libraries) {
            urls.add(new File(libraryPath).toURI().toURL());
        }
        
        // EclipseのClassLoader(ContextFinder)は使わない
//        ClassLoader orgClassLoader = Thread.currentThread().getContextClassLoader().getParent();      
//        ClassLoader loader = new LimyClassLoader(
//                priorityUrls.toArray(new URL[priorityUrls.size()]),
//                urls.toArray(new URL[urls.size()]),
//                orgClassLoader);
        
        // 既存のクラスローダを退避
        ClassLoader orgClassLoader = Thread.currentThread().getContextClassLoader();
        
        Collection<URL> combineUrls = new ArrayList<URL>();
        combineUrls.addAll(priorityUrls);
        combineUrls.addAll(urls);
        ClassLoader loader = new URLClassLoader(
                combineUrls.toArray(new URL[combineUrls.size()]),
                orgClassLoader);
        
        // 現スレッドのクラスローダを上書き
        Thread.currentThread().setContextClassLoader(loader);

        // テストを実行（カバレッジ計測）
        boolean isExecuteTest = false;
        for (IResource javaResource : javaResources) {
            String testClassName = QalabResourceUtils.getQualifiedTestClassName(
                    env, javaResource);
//            System.out.println(testClassName + " exec...");

            try {
                
                TestClassRunner runner = new TestClassRunner(loader.loadClass(testClassName));
                Result result = new Result();
                RunNotifier notifier = new RunNotifier();
                notifier.addListener(result.createListener());
                runner.run(notifier);
//                Class<? extends TestCase> testClass
//                        = (Class<? extends TestCase>)loader.loadClass(testClassName);
//                
//                TestResult testResult = TestRunner.run(new TestSuite(testClass));
                testResults.add(result);
                isExecuteTest = true;
            } catch (ClassNotFoundException e) {
                // テストケースが存在しない場合
            } catch (Throwable e) {
                // テスト中にエラーが発生した場合
                LimyEclipsePluginUtils.log(e);
            }
        }
        
        // 既存のクラスローダを復帰
        Thread.currentThread().setContextClassLoader(orgClassLoader);
        
        if (isExecuteTest) {
            saveProjectData();
        }
        
        return testResults;
    }


    /**
     * Coberturaに終了の合図を出し、cobertura.ser にカバレッジ結果を出力させます。
     */
    private void saveProjectData() {

        try {
            String className = "net.sourceforge.cobertura.coveragedata.ProjectData";
            Class<?> saveClass = Class.forName(className);
            Method saveMethod = saveClass.getDeclaredMethod("saveGlobalProjectData", new Class[0]);
            saveMethod.invoke(null, new Object[0]);
            
        } catch (Exception e) {
            LimyEclipsePluginUtils.log(e);
        }
    }

}
