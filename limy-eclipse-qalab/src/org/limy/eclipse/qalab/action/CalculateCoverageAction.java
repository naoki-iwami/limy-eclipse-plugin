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
package org.limy.eclipse.qalab.action;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.limy.eclipse.common.ui.AbstractJavaElementAction;
import org.limy.eclipse.qalab.LimyQalabMarker;
import org.limy.eclipse.qalab.LimyQalabPluginUtils;
import org.limy.eclipse.qalab.common.LimyQalabEnvironment;
import org.limy.eclipse.qalab.common.QalabResourceUtils;
import org.limy.eclipse.qalab.mark.CoberturaMarkCreator;

/**
 * カバレッジ計測アクションクラスです。
 * @author Naoki Iwami
 */
public class CalculateCoverageAction extends AbstractJavaElementAction {

    @Override
    protected void doAction(IJavaElement javaElement, IProgressMonitor monitor)
            throws CoreException {
        
        if (javaElement instanceof IJavaProject) {
            IProject project = ((IJavaProject)javaElement).getProject();
            
            // result, coverage, coverage(brief)
            project.deleteMarkers(LimyQalabMarker.COVERAGE_RESULT,
                    true, IResource.DEPTH_INFINITE);
            
            // failure, error
            project.deleteMarkers(LimyQalabMarker.FAILURE_ID,
                    true, IResource.DEPTH_INFINITE);
            
            LimyQalabEnvironment env = LimyQalabPluginUtils.createEnv(project);
            List<IResource> allSourceFiles = QalabResourceUtils.getAllSourceFiles(
                    env, false, true);
            CoberturaMarkCreator.getInstance().markResources(
                    env, allSourceFiles, null);
        } else {
            
            IResource resource = javaElement.getResource();
            
            // result, coverage, non-coverage
            resource.deleteMarkers(LimyQalabMarker.COVERAGE_RESULT,
                    true, IResource.DEPTH_INFINITE);
            
            // failure, error, success
            resource.deleteMarkers(LimyQalabMarker.TEST_ID,
                    true, IResource.DEPTH_INFINITE);

            LimyQalabEnvironment env = LimyQalabPluginUtils.createEnv(resource.getProject());
            
//            CoberturaMarkCreator.getInstance().markResources(env,
//                    LimyJavaUtils.getAllResources(javaElement), null);
            
            Collection<IJavaElement> elements = new ArrayList<IJavaElement>();
            elements.add(javaElement);
            CoberturaMarkCreator.getInstance().markJavaElement(env, elements, null);

        }
    }

}
