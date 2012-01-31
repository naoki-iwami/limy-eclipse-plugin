/*
 * Created 2006/11/22
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
import java.util.Collection;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jface.preference.IPreferenceStore;
import org.limy.eclipse.common.resource.LimyResourceUtils;
import org.limy.eclipse.qalab.LimyQalabPluginUtils;
import org.limy.eclipse.qalab.common.AntCreator;
import org.limy.eclipse.qalab.common.LimyQalabConstants;
import org.limy.eclipse.qalab.common.LimyQalabEnvironment;
import org.limy.eclipse.qalab.common.LimyQalabJavaUtils;
import org.limy.eclipse.qalab.common.LimyQalabUtils;
import org.limy.xml.XmlElement;
import org.limy.xml.XmlUtils;


/**
 * デフォルトで使用するCreatorクラスです。
 * @author Naoki Iwami
 */
public class DefaultCreator extends AbstractAntCreator {

    public void exec(XmlElement root, LimyQalabEnvironment env)
            throws FileNotFoundException, CoreException {
        
        createAllClasspath(root, env.getJavaProject());
        createTestAllClasspath(root, env);
        createVmstyleTaskdef(root);
        createSupportTaskdef(root);

    }

    public String[] getQalabClassNames() {
        return new String[0];
    }

    public String[] getReportXmlNames() {
        return new String[0];
    }

    public int getSummaryGroup() {
        return -1;
    }

    public String[] getSummaryTypes() {
        return new String[0];
    }

    public String getTargetName() {
        return null;
    }

    public boolean isEnable(IPreferenceStore store) {
        return true;
    }

    public AntCreator newInstance() {
        return new DefaultCreator();
    }

    // ------------------------ Private Methods

    /**
     * path要素（all.classpath）を作成します。
     * @param root ルート要素
     * @param project Javaプロジェクト
     * @throws FileNotFoundException 必要なファイルが存在しない場合
     * @throws CoreException コア例外
     */
    private void createAllClasspath(XmlElement root, IJavaProject project)
            throws FileNotFoundException, CoreException {
        
        XmlElement pathEl = XmlUtils.createElement(root, "path");
        pathEl.setAttribute("id", "all.classpath");
        for (String location : LimyQalabJavaUtils.getJavaLibraries(project)) {
            createPathelement(pathEl, location);
        }
    }
    
    /**
     * path要素（test.all.classpath）を作成します。
     * @param root ルート要素
     * @param env 
     * @throws FileNotFoundException 必要なファイルが存在しない場合
     * @throws CoreException コア例外
     */
    private void createTestAllClasspath(XmlElement root, LimyQalabEnvironment env)
            throws FileNotFoundException, CoreException {

        IJavaProject project = env.getJavaProject();
        IPreferenceStore store = env.getStore();

        XmlElement pathEl = XmlUtils.createElement(root, "path");
        pathEl.setAttribute("id", "test.all.classpath");

        createPathelement(pathEl, "${dest.dir}/instrumented");

        Collection<IPath> binPaths = env.getBinPaths(true);
        for (IPath path : binPaths) {
            createPathelement(pathEl, LimyQalabUtils.createFullPath(project, path));
        }

        String libDir = store.getString(LimyQalabConstants.TEST_LIBDIR);
        if (libDir.length() > 0) {
            IPath path = project.getPath().append(libDir);
            IFolder folder = (IFolder)LimyResourceUtils.newFolder(path);
            for (IResource resource : folder.members()) {
                if (resource.getType() == IFile.FILE) {
                    createPathelement(pathEl, resource.getLocation().toString());
                }
            }
        }
    }

    /**
     * vmstyleタスクのtaskdef要素を作成します。
     * @param root ルート要素
     * @throws FileNotFoundException 必要なファイルが存在しない場合
     */
    private void createVmstyleTaskdef(XmlElement root) throws FileNotFoundException {
        XmlElement taskEl = XmlUtils.createElement(root, "taskdef");
        taskEl.setAttribute("name", "vmstyle");
        taskEl.setAttribute("classname", "org.limy.velocity.task.VmStyleTask");

        XmlElement classEl = XmlUtils.createElement(taskEl, "classpath");

        addPathElementMyPackage(classEl);
        addPathElementPrefix(classEl, "commons-logging");
        addPathElementCorePackage(classEl);

        XmlElement initEl = createTargetElement(root, "init", "mergesrc");
        
        XmlElement mkdirEl1 = XmlUtils.createElement(initEl, "mkdir");
        mkdirEl1.setAttribute("dir", "${dest.dir}");

        XmlElement copyEl = XmlUtils.createElement(initEl, "copy");
        copyEl.setAttribute("todir", "${dest.dir}");
        XmlElement fileEl = XmlUtils.createElement(copyEl, "fileset");
        fileEl.setAttribute("dir", LimyQalabPluginUtils.getResourcePath(""));
        fileEl.setAttribute("includes", "css/**,images/**,js/**");

    }

    /**
     * @param root
     * @throws FileNotFoundException 
     */
    private void createSupportTaskdef(XmlElement root) throws FileNotFoundException {
        
        XmlElement taskEl = createTaskdefElement(root, "project-support",
                "org.limy.eclipse.qalab.task.ProjectSupportTask", null);
        
        XmlElement classEl = XmlUtils.createElement(taskEl, "classpath");
        addPathElementMyPackage(classEl);
        addPathElementCorePackage(classEl);
        addPathElementPrefix(classEl, "commons-logging");
        
    }

}
