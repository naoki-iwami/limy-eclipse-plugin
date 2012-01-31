/*
 * Created 2006/08/11
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
package org.limy.eclipse.qalab.ant;

import java.io.FileNotFoundException;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jface.preference.IPreferenceStore;
import org.limy.eclipse.qalab.common.AntCreator;
import org.limy.eclipse.qalab.common.LimyQalabConstants;
import org.limy.eclipse.qalab.common.LimyQalabEnvironment;
import org.limy.eclipse.qalab.common.LimyQalabUtils;
import org.limy.xml.XmlElement;
import org.limy.xml.XmlUtils;


/**
 * Cobertura用のAnt要素を生成するクラスです。
 * @author Naoki Iwami
 */
public class CoberturaCreator extends AbstractAntCreator {

    // ------------------------ Implement Methods
    
    public void exec(XmlElement root, LimyQalabEnvironment env)
            throws FileNotFoundException, CoreException {
        
        appendCoberturaElement(root, env);
    }
    
    public String[] getQalabClassNames() {
        return new String[] {
                "net.objectlab.qalab.parser.CoberturaLineStatMerge",
                "net.objectlab.qalab.parser.CoberturaBranchStatMerge",
                "org.limy.eclipse.qalab.parser.JUnitStatMerge",
        };
    }

    public String[] getReportXmlNames() {
        return new String[] {
                "coverage.xml",
                "coverage.xml",
                "junit/TESTS-TestSuites.xml",
        };
    }

    public int getSummaryGroup() {
        return 1;
    }

    public String[] getSummaryTypes() {
        return new String[] { "cobertura-line", "cobertura-branch", "junit" };
    }

    public String getTargetName() {
        return "cobertura";
    }
    
    public boolean isEnable(IPreferenceStore store) {
        return store.getBoolean(LimyQalabConstants.ENABLE_JUNIT);
    }

    public AntCreator newInstance() {
        return new CoberturaCreator();
    }

    // ------------------------ Private Methods
    
    private void appendCoberturaElement(XmlElement root, LimyQalabEnvironment env)
            throws FileNotFoundException, CoreException {
        
        createTaskdef(root);
        createMainTarget(root, env);
        createReportTarget(root);
        
    }

    /**
     * taskdef要素を作成します。
     * @param root ルート要素
     * @throws FileNotFoundException 必要なファイルが見つからない場合
     */
    private void createTaskdef(XmlElement root) throws FileNotFoundException {
        
        XmlElement pathEl = XmlUtils.createElement(root, "path");
        pathEl.setAttribute("id", "cobertura.classpath");
        
        addPathElement(pathEl, "cobertura.jar");
        addPathElementPrefix(pathEl, "log4j");
        addPathElementPrefix(pathEl, "jakarta-oro");
        addPathElementPrefix(pathEl, "asm");
        addPathElementPrefix(pathEl, "asm-tree");
        
        XmlElement taskEl = XmlUtils.createElement(root, "taskdef");
        taskEl.setAttribute("resource", "tasks.properties");
        taskEl.setAttribute("classpathref", "cobertura.classpath");
        
    }
    
//    /**
//     * JUnit用のtaskdef要素を作成します。
//     * @param root ルート要素
//     * @throws FileNotFoundException 必要なファイルが見つからない場合
//     */
//    private void createJunitTaskdef(XmlElement root) throws FileNotFoundException {
//        
//        XmlElement taskEl = XmlUtils.createElement(root, "taskdef");
//        taskEl.addAttribute("name", "junit");
//        taskEl.addAttribute("class", "org.apache.tools.ant.taskdefs.optional.junit.JUnitTask");
//
//        XmlElement classEl = XmlUtils.createElement(taskEl, "classpath");
//        XmlElement pathEl = XmlUtils.createElement(classEl, "pathelement");
//        IPath location = BuildPathSupport.getBundleLocation(BuildPathSupport.JUNIT3_PLUGIN_ID);
//        pathEl.addAttribute("location", location.toString() + "/junit.jar");
//
//    }

    /**
     * Cobertura計測ターゲットを作成します。
     * @param root ルート要素
     * @param env 
     * @param project Javaプロジェクト
     * @throws CoreException コア例外
     */
    private void createMainTarget(XmlElement root, LimyQalabEnvironment env)
            throws CoreException {
        
        XmlElement targetEl = createTargetElement(root, "cobertura", "init");

        XmlElement delEl1 = XmlUtils.createElement(targetEl, "delete");
        delEl1.setAttribute("file", "${dest.dir}/instrumented");
        XmlElement delEl2 = XmlUtils.createElement(targetEl, "delete");
        delEl2.setAttribute("file", "${dest.dir}/cobertura.ser");

        createInstrumentElement(env, targetEl);

        XmlElement mkdirEl = XmlUtils.createElement(targetEl, "mkdir");
        mkdirEl.setAttribute("dir", "${dest.dir}/coverage/xml");

        createJunitElement(env, targetEl);
        
        createXmlReportElement(targetEl);
        
        createJunitReportElement(targetEl);

    }

    /**
     * JUnit要素を生成します。
     * @param env 
     * @param targetEl 親要素
     * @throws CoreException コア例外
     */
    private void createJunitElement(LimyQalabEnvironment env,
            XmlElement targetEl) throws CoreException {
        
        IJavaProject project = env.getJavaProject();

        XmlElement junitEl = XmlUtils.createElement(targetEl, "junit");
        junitEl.setAttribute("fork", "yes");
        junitEl.setAttribute("forkmode", "once");
        junitEl.setAttribute("dir", "${basedir}");
        junitEl.setAttribute("failureProperty", "test.failed");
        junitEl.setAttribute("showoutput", "yes");

        createSyspropertyElement(junitEl,
                "net.sourceforge.cobertura.datafile", "${dest.dir}/cobertura.ser");

        String testEnvs = env.getStore().getString(LimyQalabConstants.TEST_ENVS);
        for (String testEnv : testEnvs.split("\n")) {
            String[] split = testEnv.split("\t");
            if (split.length == 2) {
                createSyspropertyElement(junitEl, split[0], split[1]);
            }
        }
        
        createClasspathElement(junitEl, "test.all.classpath");
        
//        createClasspathElement(LimyQalabUtils.getAntVerion(store),
//                junitEl, paths);
        
        createClasspathElement(junitEl, "cobertura.classpath");
        createClasspathElement(junitEl, "all.classpath");

        XmlElement formatterEl = XmlUtils.createElement(junitEl, "formatter");
        formatterEl.setAttribute("type", "xml");
        
        XmlElement testEl = XmlUtils.createElement(junitEl, "batchtest");
        testEl.setAttribute("todir", "${dest.dir}/coverage/xml");

        // テストソースパスからテストクラス一覧を追加
        for (IPath path : env.getTestSourcePaths()) {
            XmlElement filesetEl = XmlUtils.createElement(testEl, "fileset");
            filesetEl.setAttribute("dir", LimyQalabUtils.createFullPath(project, path));
            
            XmlElement incEl = XmlUtils.createElement(filesetEl, "include");
            incEl.setAttribute("name", "**/*Test.java");
            XmlElement excEl = XmlUtils.createElement(filesetEl, "exclude");
            excEl.setAttribute("name", "**/Abstract*Test.java");
        }
        
//        // 通常ソースパス上から検索してテストクラス一覧を追加
//        String includeName = store.getString(LimyQalabConstants.TEST_INCLUDE_NAME);
//        String excludeName = store.getString(LimyQalabConstants.TEST_EXCLUDE_NAME);
//        if (!LimyUtils.isEmpty(includeName) || !LimyUtils.isEmpty(excludeName)) {
//            for (IPath path : LimyQalabUtils.getMainSourcePaths(project, false)) {
//                XmlElement filesetEl = XmlUtils.createElement(testEl, "fileset");
//                filesetEl.addAttribute("dir", path.toString());
//                if (!LimyUtils.isEmpty(includeName)) {
//                    XmlElement incEl = XmlUtils.createElement(filesetEl, "include");
//                    incEl.addAttribute("name", includeName.replace('.', '/') + ".java");
//                }
//                if (!LimyUtils.isEmpty(excludeName)) {
//                    XmlElement excEl = XmlUtils.createElement(filesetEl, "exclude");
//                    excEl.addAttribute("name", excludeName.replace('.', '/') + ".java");
//                }
//            }
//        }
    }

    /**
     * instrument要素を生成します。
     * @param env 
     * @param parent 親要素
     * @throws CoreException コア例外
     */
    private void createInstrumentElement(
            LimyQalabEnvironment env, XmlElement parent) throws CoreException {

        IJavaProject project = env.getJavaProject();
        
        XmlElement instEl = XmlUtils.createElement(parent, "cobertura-instrument");
        instEl.setAttribute("todir", "${dest.dir}/instrumented");
        instEl.setAttribute("datafile ", "${dest.dir}/cobertura.ser");
        
        for (IPath path : env.getBinPaths(true)) {
            XmlElement filesetEl = XmlUtils.createElement(instEl, "fileset");
            filesetEl.setAttribute("dir", LimyQalabUtils.createFullPath(project, path));
            filesetEl.setAttribute("includes", "**/*.class");
            
            // テスト対象クラスはinstrument対象から除外する
            filesetEl.setAttribute("excludes", "**/*Test.class");
//            String exclude = store.getString(LimyQalabConstants.TEST_INCLUDE_NAME)
//                    .replace('.', '/');
//            if (exclude.length() == 0) {
//                filesetEl.addAttribute("excludes", "**/*Test.class");
//            } else {
//                filesetEl.addAttribute("excludes", "**/*Test.class, " + exclude + ".class");
//            }
            
        }
    }

    /**
     * junitreport要素を作成します。
     * @param root ルート要素
     */
    private void createJunitReportElement(XmlElement root) {
        
        XmlElement mkdirEl = XmlUtils.createElement(root, "mkdir");
        mkdirEl.setAttribute("dir", "${dest.dir}/junit");

        XmlElement junitEl = XmlUtils.createElement(root, "junitreport");
        junitEl.setAttribute("todir", "${dest.dir}/junit");

        XmlElement jfilesetEl = XmlUtils.createElement(junitEl, "fileset");
        jfilesetEl.setAttribute("dir", "${dest.dir}/coverage/xml");
        jfilesetEl.setAttribute("includes", "TEST-*.xml");
        
        XmlElement formatEl = XmlUtils.createElement(junitEl, "report");
        formatEl.setAttribute("format", "frames");
        formatEl.setAttribute("todir", "${dest.dir}/junit/html");

    }
    
    /**
     * Coberturaレポート出力ターゲットを作成します。
     * @param root ルート要素
     */
    private void createReportTarget(XmlElement root) {
        
        XmlElement targetEl = createTargetElement(root, "cobertura-report-only", "init");
        
        XmlElement reportEl = XmlUtils.createElement(targetEl, "cobertura-report");
        reportEl.setAttribute("format", "html");
        reportEl.setAttribute("destdir", "${dest.dir}/coverage/html");
        reportEl.setAttribute("datafile", "${dest.dir}/cobertura.ser");
        reportEl.setAttribute("charset", "${src.encoding}"); // Coberturaパッチ
        
        XmlElement filesetEl = XmlUtils.createElement(reportEl, "fileset");
        filesetEl.setAttribute("dir", "${all.src.dir}");
        filesetEl.setAttribute("includes", "**/*.java");

    }

    /**
     * CoberturaXMLレポート出力要素を作成します。
     * @param targetEl ルート要素
     */
    private void createXmlReportElement(XmlElement targetEl) {
        
        XmlElement reportEl = XmlUtils.createElement(targetEl, "cobertura-report");
        reportEl.setAttribute("format", "xml");
        reportEl.setAttribute("destdir", "${dest.dir}");
        reportEl.setAttribute("datafile", "${dest.dir}/cobertura.ser");
        reportEl.setAttribute("charset", "${src.encoding}"); // Coberturaパッチ
        
        XmlElement filesetEl = XmlUtils.createElement(reportEl, "fileset");
        filesetEl.setAttribute("dir", "${all.src.dir}");
        filesetEl.setAttribute("includes", "**/*.java");
        
    }

}
