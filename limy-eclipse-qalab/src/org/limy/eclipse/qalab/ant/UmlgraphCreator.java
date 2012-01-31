/*
 * Created 2007/02/07
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

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jface.preference.IPreferenceStore;
import org.limy.eclipse.qalab.LimyQalabPluginUtils;
import org.limy.eclipse.qalab.common.AntCreator;
import org.limy.eclipse.qalab.common.LimyQalabConstants;
import org.limy.eclipse.qalab.common.LimyQalabEnvironment;
import org.limy.xml.XmlElement;

/**
 * UMLGraph用のAnt要素を生成するクラスです。
 * @author Naoki Iwami
 */
public class UmlgraphCreator extends AbstractAntCreator {

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
        return 3;
    }

    public String[] getSummaryTypes() {
        return new String[0];
    }

    public String getTargetName() {
        return "umlgraph";
    }


    public boolean isEnable(IPreferenceStore store) {
        return store.getBoolean(LimyQalabConstants.ENABLE_UMLGRAPH);
    }

    public AntCreator newInstance() {
        return new UmlgraphCreator();
    }
    // ------------------------ Private Methods
    
    private void appendAllElements(XmlElement root, LimyQalabEnvironment env)
            throws CoreException, FileNotFoundException {
        
        createReportTarget(root, env);
    }
    
    /**
     * レポート出力ターゲットを作成します。
     * @param root ルート要素
     * @param env 
     * @throws CoreException 
     */
    private void createReportTarget(XmlElement root, LimyQalabEnvironment env)
            throws CoreException {
        
        XmlElement targetEl = createTargetElement(root, getTargetName(), "init");
        createElement(targetEl, "mkdir", "dir", "${dest.dir}/javadoc");
        
        Collection<IProject> projects = new ArrayList<IProject>();
        
        if (createJavadocElement(targetEl, env, env.getProject())) {
            projects.add(env.getProject());
        }
        
        for (IProject refProject : env.getEnableReferencedProjects()) {
            if (createJavadocElement(targetEl, env, refProject)) {
                projects.add(refProject);
            }
        }
        
        createMakePngTarget(targetEl);
        
        createHtmlTarget(targetEl, projects);
        
    }

    /**
     * @param targetEl
     */
    private void createMakePngTarget(XmlElement targetEl) {
        XmlElement apply = createElement(targetEl, "apply");
        apply.setAttribute("executable", "${dot.exec}");
        apply.setAttribute("dest", "${dest.dir}/javadoc");
        apply.setAttribute("parallel", "false");
        
        createElement(apply, "arg", "value", "-Tpng");
        createElement(apply, "arg", "value", "-o");
        createElement(apply, "targetfile");
        createElement(apply, "srcfile");
        
        XmlElement filesetEl = createElement(apply, "fileset");
        filesetEl.setAttribute("dir", "${dest.dir}/javadoc");
        filesetEl.setAttribute("includes", "**/*.dot");
        
        XmlElement mapperEl = createElement(apply, "mapper");
        mapperEl.setAttribute("type", "glob");
        mapperEl.setAttribute("from", "*.dot");
        mapperEl.setAttribute("to", "*.png");
    }

    /**
     * @param targetEl
     * @param env
     * @param targetProject
     * @return Elementを作成した場合に true
     * @throws CoreException 
     */
    private boolean createJavadocElement(XmlElement targetEl,
            LimyQalabEnvironment env, IProject targetProject) throws CoreException {
        
        XmlElement javadocEl = createElement(targetEl, "javadoc");
        
        IJavaProject javaProject = JavaCore.create(targetProject);
        IPreferenceStore store = env.getStore();

        IPackageFragmentRoot[] roots = javaProject.getAllPackageFragmentRoots();
        
        // 自プロジェクトのソースディレクトリのみを取得
        boolean isEmpty = true;
        for (IPackageFragmentRoot root : roots) {
            if (root.getKind() == IPackageFragmentRoot.K_SOURCE
                    && root.getJavaProject().equals(javaProject)) {
                
                IResource rootDir = root.getResource();
                if (((IContainer)rootDir).members().length > 0) {
                    isEmpty = false;
                }
                XmlElement fileEl = createElement(
                        javadocEl, "fileset", "dir", rootDir.getLocation().toString());
                fileEl.setAttribute("includes", "**/*.java");
                fileEl.setAttribute("excludes", "**/*Test.java");
            }
        }

        // ソースファイルが一つも無い場合はjavadoc要素を作らない
        if (isEmpty) {
            targetEl.removeChild(javadocEl);
            return false;
        }

        javadocEl.setAttribute("destDir",
                "${dest.dir}/javadoc/" + targetProject.getName());
        
        javadocEl.setAttribute("Encoding", "${src.encoding}");
        javadocEl.setAttribute("classpathref", "all.classpath");
        javadocEl.setAttribute("private", "false");
        javadocEl.setAttribute("packagenames", "*");
        javadocEl.setAttribute("useexternalfile", "yes");
        
        XmlElement docletEl = createElement(javadocEl, "doclet");
        docletEl.setAttribute("name", "gr.spinellis.umlgraph.doclet.UmlGraph");
        docletEl.setAttribute("path", LimyQalabPluginUtils.getResourcePath("lib/UmlGraph.jar"));
        
        addParam(docletEl, "-collpackages", "java.util.*");
        if (store.getBoolean(LimyQalabConstants.UMLGRAPH_INFERREL)) {
            addParam(docletEl, "-inferrel", null);
        }
        if (store.getBoolean(LimyQalabConstants.UMLGRAPH_HORIZONTAL)) {
            addParam(docletEl, "-horizontal", null);
        }
        
        String resources = env.getStore().getString(LimyQalabConstants.EXCLUDE_JDEPENDS);
        String[] excludes = resources.split("\n");
        for (String exclude : excludes) {
            // 正規表現表記に変換 java.* => ^java\..*
            // org.apache.* => ^org\.apache\..*
            if (exclude.length() > 0) {
                addParam(docletEl, "-hide", "^"
                        + exclude.replaceAll("\\.", "\\\\.").replaceAll("\\*", ".*"));
            }
        }
        return true;
    }

    /**
     * @param targetEl
     * @param targetProjects 
     * @throws CoreException 
     */
    private void createHtmlTarget(XmlElement targetEl, Collection<IProject> targetProjects)
            throws CoreException {

        XmlElement supportEl = createElement(targetEl, "project-support");
        supportEl.setAttribute("out", "${dest.dir}/project.xml");
        for (IProject project : targetProjects) {
            createElement(supportEl, "project", "value", project.getName());
        }
        
        createVmstyleElement(targetEl,
                "${dest.dir}/project.xml",
                "${dest.dir}/umlgraph.html",
                "umlgraph/index.vm");
        
    }


}
