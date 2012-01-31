/*
 * Created 2007/02/06
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
import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jface.preference.IPreferenceStore;
import org.limy.eclipse.qalab.common.AntCreator;
import org.limy.eclipse.qalab.common.LimyQalabConstants;
import org.limy.eclipse.qalab.common.LimyQalabEnvironment;
import org.limy.eclipse.qalab.common.LimyQalabUtils;
import org.limy.xml.XmlElement;
import org.limy.xml.XmlUtils;

/**
 * JDepend用のAnt要素を生成するクラスです。
 * @author Naoki Iwami
 */
public class JDependCreator extends AbstractAntCreator {

    // ------------------------ Implement Methods

    public void exec(XmlElement root, LimyQalabEnvironment env)
            throws CoreException, FileNotFoundException {
        
        appendAllElements(root, env);
    }
    
    public String[] getQalabClassNames() {
        return new String[0];
    }

    public String[] getReportXmlNames() {
        return new String[0];
    }

    public int getSummaryGroup() {
        return 2;
    }

    public String[] getSummaryTypes() {
        return new String[0];
    }

    public String getTargetName() {
        return "jdepend";
    }
    
    public boolean isEnable(IPreferenceStore store) {
        return store.getBoolean(LimyQalabConstants.ENABLE_JDEPEND);
    }

    public AntCreator newInstance() {
        return new JDependCreator();
    }

    // ------------------------ Private Methods
    
    private void appendAllElements(XmlElement root, LimyQalabEnvironment env)
            throws CoreException, FileNotFoundException {
        
        createExtendTaskdef(root);
        createMainTarget(root, env);
        createReportTarget(root, env);
    }
    
    /**
     * @param root
     * @throws FileNotFoundException 
     */
    private void createExtendTaskdef(XmlElement root) throws FileNotFoundException {
        
        XmlElement taskEl = createTaskdefElement(root, "jdepend-distance-graph",
                "org.limy.eclipse.qalab.task.DistanceGraphTask", null);
        
        XmlElement classEl = XmlUtils.createElement(taskEl, "classpath");
        addPathElementMyPackage(classEl);
        addPathElementCorePackage(classEl);
        addPathElementPrefix(classEl, "commons-logging");
        addPathElementPrefix(classEl, "jfreechart");
        addPathElementPrefix(classEl, "jcommon");
        
    }

    /**
     * 計測ターゲットを作成します。
     * @param root ルート要素
     * @param env 
     * @throws CoreException 
     */
    private void createMainTarget(XmlElement root, LimyQalabEnvironment env)
            throws CoreException {
        
        XmlElement targetEl = createTargetElement(root, getTargetName(), "init");
        
        // Antを手動で実行するときには、jdepend.jar を$ANT_HOME/libにコピーしておく必要がある
        XmlElement mainEl = XmlUtils.createElement(targetEl, "jdepend");
        mainEl.setAttribute("format", "xml");
        mainEl.setAttribute("outputfile", "${dest.dir}/jdepend_report.xml");
        
        XmlElement classEl = XmlUtils.createElement(mainEl, "classespath");
        for (IPath path : env.getBinPaths(true)) {
            createPathelement(classEl,
                    LimyQalabUtils.createFullPath(env.getJavaProject(), path));
        }

        createGraphTasks(env, targetEl);
        
    }

    /**
     * @param env
     * @param targetEl
     * @param root 
     * @throws CoreException 
     */
    private void createGraphTasks(LimyQalabEnvironment env,
            XmlElement targetEl) throws CoreException {
        
        createGraphTask(env.getStore(), targetEl, env.getProject());
        
        for (IProject refProject : env.getEnableReferencedProjects()) {
            createGraphTask(env.getStore(), targetEl, refProject);
        }
        
    }
    
    /**
     * @param store 
     * @param targetEl 
     * @param refProject
     * @throws CoreException 
     */
    private void createGraphTask(IPreferenceStore store,
            XmlElement targetEl, IProject refProject) throws CoreException {

        IJavaProject javaProject = JavaCore.create(refProject);
        String projectName = refProject.getName();

        XmlElement mainEl = XmlUtils.createElement(targetEl, "jdepend");
        mainEl.setAttribute("format", "xml");
        mainEl.setAttribute("outputfile",
                "${dest.dir}/jdepend_report_for_graph_" + projectName + ".xml");
        
        XmlElement classEl = XmlUtils.createElement(mainEl, "classespath");
        Collection<IPath> results = new ArrayList<IPath>();
        LimyQalabUtils.appendProjectBinPaths(javaProject, results);
        for (IPath path : results) {
            createPathelement(classEl,
                    LimyQalabUtils.createFullPath(javaProject, path));
        }

        String resources = store.getString(LimyQalabConstants.EXCLUDE_JDEPENDS);
        String[] excludes = resources.split("\n");
        for (String exclude : excludes) {
            createElement(mainEl, "exclude", "name", exclude);
        }

        XmlElement vmElement = createVmstyleElement(targetEl,
                "${dest.dir}/jdepend_report_for_graph_" + projectName + ".xml",
                "${dest.dir}/jdepend_report_" + projectName + ".dot",
                "jdepend_graph/index.vm");
        
        addVmParam(vmElement, "packagePrefix",
                store.getString(LimyQalabConstants.KEY_JDEPEND_BASE));
        
//        XmlElement styleEl = XmlUtils.createElement(targetEl, "style");
//        styleEl.setAttribute("in", "${dest.dir}/jdepend_report_for_graph_" + projectName + ".xml");
//        styleEl.setAttribute("out", "${dest.dir}/jdepend_report_" + projectName + ".dot");
//        styleEl.setAttribute("style", LimyQalabUtils.getResourcePath("jdepend2dot.xsl"));

    }

    /**
     * レポート出力ターゲットを作成します。
     * @param root ルート要素
     * @param env 
     * @throws CoreException 
     */
    private void createReportTarget(XmlElement root, LimyQalabEnvironment env)
            throws CoreException {
        
        XmlElement targetEl = createTargetElement(root,
                getTargetName() + "-report-only", "init");
        
        XmlElement supportEl = createElement(targetEl, "project-support");
        supportEl.setAttribute("out", "${dest.dir}/project-all.xml");
        createElement(supportEl, "project", "value", env.getProject().getName());
        for (IProject project : env.getEnableReferencedProjects()) {
            createElement(supportEl, "project", "value", project.getName());
        }

        createVmstyleElement(targetEl,
                new String[] { "${dest.dir}/jdepend_report.xml", "${dest.dir}/project-all.xml" },
                "${dest.dir}/jdepend_report.html",
                "jdepend/index.vm");

        createGraphReportTask(targetEl, env.getProject());
        for (IProject refProject : env.getEnableReferencedProjects()) {
            createGraphReportTask(targetEl, refProject);
        }

        XmlElement graphEl = XmlUtils.createElement(targetEl, "jdepend-distance-graph");
        graphEl.setAttribute("in", "${dest.dir}/jdepend_report.xml");
        graphEl.setAttribute("out", "${dest.dir}/jdepend_distance_report.png");

    }

    /**
     * @param targetEl
     * @param project
     */
    private void createGraphReportTask(XmlElement targetEl,
            IProject project) {
                
        XmlElement execEl = XmlUtils.createElement(targetEl, "exec");
        execEl.setAttribute("executable", "${dot.exec}");
        
        createElement(execEl, "arg", "value", "-Tpng");
        createElement(execEl, "arg", "value",
                "-o${dest.dir}/jdepend_report_" + project.getName() + ".png");
        createElement(execEl, "arg", "value",
                "${dest.dir}/jdepend_report_" + project.getName() + ".dot");
        
    }
    
}