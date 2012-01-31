/*
 * Created 2007/01/04
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
package org.limy.eclipse.qalab.propertypage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.WorkspaceJob;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.limy.eclipse.qalab.LimyQalabNature;

/**
 * QALab Nature をON/OFFするJobクラスです。
 * @author Naoki Iwami
 */
public class LimyQalabNatureConfigureJob extends WorkspaceJob {

    /** プロジェクト */
    private final IProject project;
    
    // ------------------------ Constructors

    /**
     * SampleNatureConfigureJobインスタンスを構築します。
     * @param project 
     * @param name
     */
    public LimyQalabNatureConfigureJob(IProject project, String name) {
        super(name);
        this.project = project;
    }

    // ------------------------ Override Methods

    @Override
    public IStatus runInWorkspace(IProgressMonitor monitor)
            throws CoreException {
        
        if (project.hasNature(LimyQalabNature.NATURE_ID)) {
            disableNature(monitor);
        } else {
            enableNature(monitor);
        }
        return Status.OK_STATUS;
    }

    // ------------------------ Private Methods

    /**
     * @param monitor 
     * @throws CoreException 
     */
    private void enableNature(IProgressMonitor monitor) throws CoreException {
        
        IProjectDescription desc = project.getDescription();
        List<String> natureIds = new ArrayList<String>(Arrays.asList(desc.getNatureIds()));
        natureIds.add(LimyQalabNature.NATURE_ID);
        desc.setNatureIds(natureIds.toArray(new String[natureIds.size()]));
        project.setDescription(desc, monitor);
        
    }

    /**
     * @param monitor 
     * @throws CoreException 
     */
    private void disableNature(IProgressMonitor monitor) throws CoreException {
        
        IProjectDescription desc = project.getDescription();
        List<String> natureIds = new ArrayList<String>(Arrays.asList(desc.getNatureIds()));
        while (natureIds.contains(LimyQalabNature.NATURE_ID)) {
            natureIds.remove(LimyQalabNature.NATURE_ID);
        }
        desc.setNatureIds(natureIds.toArray(new String[natureIds.size()]));
        project.setDescription(desc, monitor);
        
    }

}
