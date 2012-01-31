/*
 * Created 2007/01/16
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.IJavaElement;
import org.limy.eclipse.common.LimyEclipsePluginUtils;
import org.limy.eclipse.common.jdt.LimyJavaUtils;
import org.limy.eclipse.common.ui.AbstractJavaElementAction;
import org.limy.eclipse.qalab.LimyQalabMarker;
import org.limy.eclipse.qalab.LimyQalabPluginUtils;
import org.limy.eclipse.qalab.ant.LimyCreatorUtils;
import org.limy.eclipse.qalab.common.AntCreator;
import org.limy.eclipse.qalab.common.LimyQalabEnvironment;
import org.limy.eclipse.qalab.common.LimyQalabUtils;
import org.limy.eclipse.qalab.common.QalabResourceUtils;
import org.limy.eclipse.qalab.mark.MarkCreator;
import org.limy.eclipse.qalab.mark.QalabMarkerUtils;

/**
 * 選択された全ファイルにマーカーを付けるアクションです。
 * @author Naoki Iwami
 */
public class MakeMarkerAction extends AbstractJavaElementAction {

    @Override
    protected void doAction(IJavaElement javaElement, IProgressMonitor monitor)
            throws CoreException {
        
        LimyQalabEnvironment env = LimyQalabPluginUtils.createEnv(
                javaElement.getResource().getProject());
        doMark(env, javaElement, monitor);
    }
    
    // ------------------------ Private Methods

    /**
     * @param env 
     * @param javaElement
     * @param monitor
     * @throws CoreException
     */
    private void doMark(LimyQalabEnvironment env,
            IJavaElement javaElement, IProgressMonitor monitor)
            throws CoreException {
        
        IResource resource = javaElement.getResource();
        AntCreator[] creators = LimyCreatorUtils.decideCreators(env);

        monitor.beginTask("Marking...", 2 + creators.length);
        
        monitor.subTask("Delete markers...");
        monitor.worked(1);

        resource.deleteMarkers(LimyQalabMarker.DEFAULT_ID, true, IResource.DEPTH_INFINITE);
//        resource.deleteMarkers(LimyQalabMarker.TEMPORARY_ID, true, IResource.DEPTH_INFINITE);

        monitor.subTask("Decide target files...");
        monitor.worked(1);

        List<IJavaElement> javaElements = new ArrayList<IJavaElement>();
        LimyJavaUtils.appendAllJavas(javaElements, javaElement);

        try {
            Collection<IFile> autoFiles = QalabResourceUtils.getAutoCreatedFiles(env);
            
            for (ListIterator<IJavaElement> it = javaElements.listIterator(); it.hasNext();) {
                IResource sourceFile = it.next().getResource();
                if (autoFiles.contains(sourceFile)) {
                    it.remove();
                }
            }
        } catch (IOException e) {
            LimyEclipsePluginUtils.log(e);
        }
        
        // 重複があるファイルをまとめる
        Collection<IResource> targetSourceFiles = new HashSet<IResource>();
        for (IJavaElement el : javaElements) {
            IResource sourceFile = el.getResource();
            if (!LimyQalabUtils.isIgnoreSource(env, sourceFile.getFullPath())) {
                targetSourceFiles.add(sourceFile);
            }
        }
        
        monitor.subTask("Marking files...");

        for (AntCreator creator : creators) {
            
            MarkCreator markCreator = QalabMarkerUtils.getMarkCreator(creator);
            
            if (markCreator != null) {
                monitor.subTask("Marking files (" + markCreator.getName() + ")...");
                markCreator.markResources(env, targetSourceFiles, monitor);
            }
            monitor.worked(1);
        }
        
        monitor.done();
    }

}
