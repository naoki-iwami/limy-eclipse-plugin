/*
 * Created 2007/01/11
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

import java.util.Collection;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jface.viewers.ISelection;
import org.limy.eclipse.common.jdt.AbstractUIAction;
import org.limy.eclipse.qalab.LimyQalabMarker;
import org.limy.eclipse.qalab.LimyQalabPluginUtils;
import org.limy.eclipse.qalab.ant.LimyCreatorUtils;
import org.limy.eclipse.qalab.common.AntCreator;
import org.limy.eclipse.qalab.common.LimyQalabEnvironment;
import org.limy.eclipse.qalab.mark.MarkCreator;
import org.limy.eclipse.qalab.mark.QalabMarkerUtils;

/**
 * 一時的にアクティブエディタ内のQAチェックをしてマーカーを表示するアクションです。
 * @author Naoki Iwami
 */
public class MakeTemporaryMarkerAction extends AbstractUIAction {

    @Override
    public void doRun(ISelection selection, IProgressMonitor monitor)
            throws CoreException {
        
        Collection<IJavaElement> javaElements = getSelectedJavaElements();
        for (IJavaElement javaElement : javaElements) {
            createMarker(javaElement, monitor);
        }
        
    }

    /**
     * @param javaElement
     * @param monitor 
     * @throws CoreException 
     */
    private void createMarker(IJavaElement javaElement, IProgressMonitor monitor)
            throws CoreException {

        IResource resource = javaElement.getResource();
        resource.deleteMarkers(LimyQalabMarker.DEFAULT_ID, false, IResource.DEPTH_ZERO);
        LimyQalabEnvironment env = LimyQalabPluginUtils.createEnv(resource.getProject());

        AntCreator[] creators = LimyCreatorUtils.decideCreators(env);
        for (AntCreator creator : creators) {
            
            MarkCreator markCreator = QalabMarkerUtils.getMarkCreator(creator);
            if (markCreator != null) {
                markCreator.markResourceTemporary(env, resource, monitor);
            }
            
        }
        
    }

}
