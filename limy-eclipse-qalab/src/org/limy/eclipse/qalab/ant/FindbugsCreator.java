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
package org.limy.eclipse.qalab.ant;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jface.preference.IPreferenceStore;
import org.limy.eclipse.qalab.common.AntCreator;
import org.limy.eclipse.qalab.common.LimyQalabConstants;
import org.limy.eclipse.qalab.common.LimyQalabEnvironment;
import org.limy.eclipse.qalab.common.LimyQalabUtils;
import org.limy.eclipse.qalab.common.QalabResourceUtils;
import org.limy.xml.XmlElement;
import org.limy.xml.XmlUtils;
import org.limy.xml.XmlWriteUtils;


/**
 * Findbugs用のAnt要素を生成するクラスです。
 * @author Naoki Iwami
 */
public class FindbugsCreator extends AbstractAntCreator {

    // ------------------------ Implement Methods
    
    public void exec(XmlElement root, LimyQalabEnvironment env)
            throws IOException, CoreException {
        
        appendFindbugsElement(root, env);
    }
    
    public String[] getQalabClassNames() {
        return new String[] { "net.objectlab.qalab.parser.FindBugsStatMerge" };
    }

    public String[] getReportXmlNames() {
        return new String[] { "findbugs_report.xml" };
    }

    public int getSummaryGroup() {
        return 0;
    }

    public String[] getSummaryTypes() {
        return new String[] { "findbugs" };
    }
    
    public String getTargetName() {
        return "findbugs";
    }

    public boolean isEnable(IPreferenceStore store) {
        return store.getBoolean(LimyQalabConstants.ENABLE_FINDBUGS);
    }

    public AntCreator newInstance() {
        return new FindbugsCreator();
    }

    // ------------------------ Private Methods
    
    private void appendFindbugsElement(XmlElement root, LimyQalabEnvironment env)
            throws IOException, CoreException {
        
        createTaskdef(root);
        createMainTarget(root, env);
        createReportTarget(root);
        createFilterFile(env);
        
    }

    /**
     * taskdef要素を作成します。
     * @param root ルート要素
     * @throws FileNotFoundException 必要なファイルが存在しない場合
     */
    private void createTaskdef(XmlElement root) throws FileNotFoundException {
        XmlElement taskEl = XmlUtils.createElement(root, "taskdef");
        taskEl.setAttribute("name", "findbugs");
        taskEl.setAttribute("classname", "edu.umd.cs.findbugs.anttask.FindBugsTask");
        
        XmlElement classEl = XmlUtils.createElement(taskEl, "classpath");
        addPathElement(classEl, "findbugs.jar");
        addPathElement(classEl, "findbugs-ant.jar");
        addPathElement(classEl, "jsr305.jar");
        addPathElement(classEl, "coreplugin.jar");
        addPathElement(classEl, "asm-commons-3.0.jar");

    }
    
    /**
     * Findbugs計測ターゲットを作成します。
     * @param root ルート要素
     * @param env 
     * @throws CoreException コア例外
     * @throws IOException I/O例外
     */
    private void createMainTarget(XmlElement root, LimyQalabEnvironment env)
            throws CoreException, IOException {

        IJavaProject project = env.getJavaProject();
        XmlElement targetEl = createTargetElement(root, "findbugs", "init");
        
        XmlElement mainEl = XmlUtils.createElement(targetEl, "findbugs");
        mainEl.setAttribute("home", "${findbugs.home}");
        mainEl.setAttribute("output", "xdocs");
        mainEl.setAttribute("outputFile", "${dest.dir}/findbugs_report.xml");
        mainEl.setAttribute("jvmargs", "-Xmx256M");
        if (!QalabResourceUtils.getAutoCreatedFiles(env).isEmpty()) {
            mainEl.setAttribute("excludeFilter", "findbugs-filter.xml");
        }

        XmlElement sourceEl = XmlUtils.createElement(mainEl, "sourcePath");
        sourceEl.setAttribute("path", "${all.src.dir}");
        
        for (IPath location : env.getBinPaths(true)) {
            XmlElement classEl = XmlUtils.createElement(mainEl, "class");
            classEl.setAttribute("location", LimyQalabUtils.createFullPath(project, location));
        }
        
    }

    /**
     * Findbugsレポート出力ターゲットを作成します。
     * @param root ルート要素
     */
    private void createReportTarget(XmlElement root) {
        XmlElement targetEl = createTargetElement(root, "findbugs-report-only", "init");
        
        XmlElement styleEl = createVmstyleElement(targetEl,
                "${dest.dir}/findbugs_report.xml",
                "${dest.dir}/findbugs_report.html",
                "findbugs/index.vm");
        
        addVmParam(styleEl, "version", "${findbugs.version}");
    }

    /**
     * exclude用のフィルタファイルを作成します。
     * @param env 
     * @throws IOException I/O例外
     * @throws CoreException コア例外
     */
    private void createFilterFile(LimyQalabEnvironment env) throws IOException, CoreException {
        
        XmlElement root = XmlUtils.createElement("FindBugsFilter");
        XmlElement matchEl = createElement(root, "Match");
        XmlElement mainEl = createElement(matchEl, "Or");
        
        Collection<IFile> autoCreatedFiles = QalabResourceUtils.getAutoCreatedFiles(env);
        for (IFile file : autoCreatedFiles) {
            XmlElement element = createElement(mainEl, "Class");
            String qualifiedClassName = LimyQalabUtils.getQualifiedClassName(env, file);
            element.setAttribute("name", qualifiedClassName);
        }
        
        String packages = env.getStore().getString(LimyQalabConstants.IGNORE_PACKAGES);
        String[] ignorePackages = packages.split("\n");
        
        for (String ignorePackage : ignorePackages) {
            XmlElement element = createElement(mainEl, "Package");
            // サブパッケージもフィルタ対象とする
            element.setAttribute("name",
                    "~" + ignorePackage.replaceAll("\\.", "\\\\.") + ".*");
        }

        File outFile = new File(env.getProject().getLocation().toFile(), "findbugs-filter.xml");
        FileWriter writer = new FileWriter(outFile);
        try {
            XmlWriteUtils.writeXml(writer, root);
        } finally {
            writer.close();
        }
    }
}
