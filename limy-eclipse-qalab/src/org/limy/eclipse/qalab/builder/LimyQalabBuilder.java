/*
 * Created 2007/01/15
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
package org.limy.eclipse.qalab.builder;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.ui.progress.UIJob;
import org.limy.eclipse.common.ui.LimyUIUtils;
import org.limy.eclipse.qalab.LimyQalabMarker;
import org.limy.eclipse.qalab.LimyQalabPlugin;
import org.limy.eclipse.qalab.LimyQalabPluginUtils;
import org.limy.eclipse.qalab.ant.LimyCreatorUtils;
import org.limy.eclipse.qalab.common.AntCreator;
import org.limy.eclipse.qalab.common.LimyQalabEnvironment;
import org.limy.eclipse.qalab.common.QalabResourceUtils;
import org.limy.eclipse.qalab.mark.CoberturaMarkCreator;
import org.limy.eclipse.qalab.mark.QalabMarkerUtils;
import org.limy.eclipse.qalab.mark.MarkCreator;
import org.limy.eclipse.qalab.tester.ProjectTestResult;
import org.limy.eclipse.qalab.ui.TestResultView;

/**
 * QALab用のBuilderクラスです。
 * @author Naoki Iwami
 */
public class LimyQalabBuilder extends IncrementalProjectBuilder {

    /**
     *
     * @author Naoki Iwami
     */
    private static final class TestResultJob extends UIJob {
        /**
         * 
         */
        private final IProject project;

        /**
         * TestResultJobインスタンスを構築します。
         * @param name
         * @param project
         */
        /*package*/ TestResultJob(String name, IProject project) {
            super(name);
            this.project = project;
        }

        @Override
        public IStatus runInUIThread(IProgressMonitor monitor) {
            TestResultView view = (TestResultView)
            LimyUIUtils.findView("org.limy.eclipse.qalab.ui.TestResultView");
            if (view != null) {
                TableViewer viewer = view.getTableViewer();
                ProjectTestResult projectResult = new ProjectTestResult(project);
                viewer.setInput(projectResult);
            }
            return Status.OK_STATUS;
        }
    }

    /** Builder ID */
    public static final String BUILDER_ID = LimyQalabPlugin.PLUGIN_ID + ".LimyQalabBuilder"; 

    @Override
    protected IProject[] build(int kind, Map args, IProgressMonitor monitor)
            throws CoreException {

        IProject project = getProject();

        if (kind == FULL_BUILD) {
            // TODO 確認ダイアログを表示するようにしたい。現在ではフルビルドはOFFとする
            //taskAll(project, monitor);
        } else {
            taskDelta(LimyQalabPluginUtils.createEnv(project), getDelta(project), monitor);
        }
        
        return new IProject[] { project };
    }

    /**
     * @param env 
     * @param delta
     * @param monitor 
     * @throws CoreException 
     */
    private void taskDelta(LimyQalabEnvironment env,
            IResourceDelta delta, IProgressMonitor monitor) throws CoreException {
        if (delta == null) {
            return;
        }
        IResourceDelta[] children = delta.getAffectedChildren();
        for (IResourceDelta child : children) {
            IResource resource = child.getResource();
            if (resource.exists()
                    && resource.getType() == IResource.FILE
                    && "java".equals(resource.getFileExtension())) {
                
//                long time = System.currentTimeMillis();
                markJavaFile(env, resource, monitor);
//                System.out.println(resource + " : "
//                        + (System.currentTimeMillis() - time));
            }
            taskDelta(env, child, monitor);
        }
    }

    /**
     * @param project 
     * @param monitor 
     * @throws CoreException 
     */
    private void taskAll(LimyQalabEnvironment env,
            IProject project, IProgressMonitor monitor) throws CoreException {
        
        project.deleteMarkers(LimyQalabMarker.DEFAULT_ID, true, IResource.DEPTH_INFINITE);
        
        List<IResource> files = QalabResourceUtils.getAllSourceFiles(env, false, true);
        markJavaFiles(env, files, monitor);
        
//
//        IJavaProject javaProject = JavaCore.create(project);
//        Collection<IPath> sourcePaths = LimyQalabUtils.getSourcePaths(javaProject, false);
//        LimyQalabResourceVisitor visitor = new LimyQalabResourceVisitor();
//        for (IPath path : sourcePaths) {
//            IResource resource = LimyCompatibleUtils.newResource(
//                    path, IResource.FOLDER);
//            resource.accept(visitor);
//        }
//        markJavaFiles(project, visitor.getAllResources(), monitor);
        
    }

    /**
     * @param env 
     * @param resource
     * @param monitor 
     * @throws CoreException 
     */
    private void markJavaFile(LimyQalabEnvironment env,
            IResource resource, IProgressMonitor monitor) throws CoreException {
        
        resource.deleteMarkers(LimyQalabMarker.DEFAULT_ID, true, IResource.DEPTH_ZERO);
        
        AntCreator[] creators = LimyCreatorUtils.decideCreators(env);
        for (AntCreator creator : creators) {
            
            if ("cobertura".equals(creator.getTargetName())) {
                continue; // coberturaはメモリ消費が激しいのでとりあえず無効に
            }

            if ("findbugs".equals(creator.getTargetName())) {
                continue; // findbugs はメモリリークが存在するので無効に（本家のPluginでも同じ）
            }

            MarkCreator markCreator = QalabMarkerUtils.getMarkCreator(creator);
            if (markCreator != null) {
                if (markCreator instanceof CoberturaMarkCreator
                        || !QalabResourceUtils.isIgnoreResource(env, resource)) {
                    markCreator.markResource(env, resource, monitor);

                }
            }
        }
        
    }

    /**
     * @param env 
     * @param resources
     * @param monitor 
     * @throws CoreException 
     */
    private void markJavaFiles(LimyQalabEnvironment env,
            Collection<IResource> resources, IProgressMonitor monitor)
            throws CoreException {
        
        if (resources.isEmpty()) {
            return;
        }
        
        IProject project = env.getProject();
        
        AntCreator[] creators = LimyCreatorUtils.decideCreators(env);
        
        for (AntCreator creator : creators) {
            MarkCreator markCreator = QalabMarkerUtils.getMarkCreator(creator);
            if (markCreator != null) {
                SubProgressMonitor subMonitor = new SubProgressMonitor(monitor, 1);
                subMonitor.setTaskName(
                        "Creating " + creator.getTargetName().substring(0, 1).toUpperCase()
                        + creator.getTargetName().substring(1) + " Marker"
                        + " in " + project.getName());
                markCreator.markResources(env, resources, subMonitor);
                subMonitor.done();
            }
        }
        
        // クリーン時のCobertura全実行はとりあえず行わないことにする（重いので）
//        for (AntCreator creator : creators) {
//            String name = creator.getTargetName();
//            if ("cobertura".equals(name)) {
//                CoberturaMarkCreator.getInstance().markResourceWithAllTest(project);
//            }
//        }
        
        new TestResultJob("", project).schedule();

    }

}
