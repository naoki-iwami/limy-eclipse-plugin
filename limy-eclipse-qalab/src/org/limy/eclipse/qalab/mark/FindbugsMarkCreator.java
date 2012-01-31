/*
 * Created 2007/01/05
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
package org.limy.eclipse.qalab.mark;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.jdt.core.IJavaElement;
import org.limy.eclipse.common.LimyEclipsePluginUtils;
import org.limy.eclipse.common.resource.ResourceWithBasedir;
import org.limy.eclipse.qalab.LimyQalabPluginUtils;
import org.limy.eclipse.qalab.common.LimyQalabEnvironment;
import org.limy.eclipse.qalab.common.QalabResourceUtils;

import edu.umd.cs.findbugs.Detector;
import edu.umd.cs.findbugs.DetectorFactoryCollection;
import edu.umd.cs.findbugs.FindBugs2;
import edu.umd.cs.findbugs.IFindBugsEngine;
import edu.umd.cs.findbugs.Project;
import edu.umd.cs.findbugs.config.UserPreferences;

/**
 * Finsbugs用のマーカー作成クラスです。
 * @author Naoki Iwami
 */
public final class FindbugsMarkCreator implements MarkCreator {
    
    /** 唯一のインスタンス */
    private static FindbugsMarkCreator instance = new FindbugsMarkCreator();
    
    /** Findbugsエンジンのキャッシュ */
    private Map<IProject, IFindBugsEngine> engines = new HashMap<IProject, IFindBugsEngine>();
    
    /**
     * private constructor
     */
    private FindbugsMarkCreator() {
        System.setProperty("findbugs.home",
                LimyQalabPluginUtils.getResourcePath("external-lib/findbugs"));
    }
    
    /**
     * 唯一のインスタンスを返します。
     * @return 唯一のインスタンス
     */
    public static FindbugsMarkCreator getInstance() {
        return instance;
    }
    
    // ------------------------ Implement Methods

    public String getName() {
        return "findbugs";
    }
    
    public boolean markJavaElement(LimyQalabEnvironment env,
            Collection<IJavaElement> elements, IProgressMonitor monitor) {
        
        return markResources(env, QalabResourceUtils.getResources(elements), monitor);
    }

    public boolean markResource(LimyQalabEnvironment env,
            IResource resource, IProgressMonitor monitor) {
        
        try {
            ResourceWithBasedir classResource = QalabResourceUtils.getClassResource(
                    env, resource);
            if (classResource == null) {
                LimyEclipsePluginUtils.log("Not found class file for " + resource);
            } else {
                String file = classResource.getResource().getLocation().toString();
                return markAll(env, new String[] { file }, monitor);
            }
        } catch (CoreException e) {
            LimyEclipsePluginUtils.log(e);
        }
        return false;
    }

    public boolean markResourceTemporary(LimyQalabEnvironment env,
            IResource resource, IProgressMonitor monitor) {

        try {
            String file = QalabResourceUtils.getClassResource(env, resource)
                    .getResource().getLocation().toString();
            return markAll(env, new String[] { file }, monitor);
        } catch (CoreException e) {
            LimyEclipsePluginUtils.log(e);
        }
        return false;
    }

    public boolean markResources(LimyQalabEnvironment env,
            Collection<IResource> resources, IProgressMonitor monitor) {

        try {
            
            IProject project = env.getProject();
            
            File binDir = new File(project.getLocation().toFile(), ".bin");
            FileUtils.deleteDirectory(binDir);
            for (IResource resource : resources) {
                ResourceWithBasedir classResource = QalabResourceUtils.getClassResource(
                        env, resource);
                
                // /javatest/bin/AdBean.class
                String fullPath = classResource.getResource().getFullPath().toString();

                // /javatest/bin
                String baseDir = classResource.getBaseDir().toString();
                
                // /AdBean.class
                String relativePath = fullPath.substring(baseDir.length() + 1);
                
                File classFile = new File(binDir, relativePath);
                classFile.getParentFile().mkdirs();
                FileUtils.copyFile(
                        classResource.getResource().getLocation().toFile(),
                        classFile);
            }
            
            return markAll(env,
                    new String[] { binDir.getAbsolutePath() },
                    monitor);
            
        } catch (CoreException e) {
            LimyEclipsePluginUtils.log(e);
        } catch (IOException e) {
            LimyEclipsePluginUtils.log(e);
        }
        return false;
    }

    // ------------------------ Private Methods

    /**
     * ファイル一覧をチェックしてマーカーを作成します。
     * @param env 
     * @param files ファイル一覧
     * @param monitor 遷移モニタ
     * @return 処理に成功したらtrue
     */
    private boolean markAll(LimyQalabEnvironment env, String[] files, IProgressMonitor monitor) {
        
        IFindBugsEngine engine = getEngine(env.getProject(), monitor);
        
        try {
            
            LimyBugReporter reporter = new LimyBugReporter(env);
            reporter.setPriorityThreshold(Detector.LOW_PRIORITY);
            engine.setBugReporter(reporter);

            Project findbugsProject = new Project();
            for (String file : files) {
                findbugsProject.addFile(file);
            }
            engine.setProject(findbugsProject);

            engine.execute();
            return true;
        } catch (IOException e) {
            LimyEclipsePluginUtils.log(e);
        } catch (InterruptedException e) {
            LimyEclipsePluginUtils.log(e);
        }
        return false;
    }

    /**
     * @param resource
     * @param monitor 
     * @return
     */
    private IFindBugsEngine getEngine(IResource resource, IProgressMonitor monitor) {
        IFindBugsEngine engine = engines.get(resource.getProject());
        if (engine != null) {
            return engine;
        }
        
        IProgressMonitor subMonitor = new SubProgressMonitor(monitor, 1);
        subMonitor.subTask("Creating Findbugs-engine for "
                + resource.getProject().getName());

        engine = new FindBugs2();
        engine.setDetectorFactoryCollection(DetectorFactoryCollection.instance());
        engine.setUserPreferences(UserPreferences.createDefaultUserPreferences());
        engines.put(resource.getProject(), engine);
        
        subMonitor.done();
        return engine;
    }
    
}
