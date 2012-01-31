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
package org.limy.eclipse.qalab;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;

import org.eclipse.core.resources.ICommand;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IProjectNature;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.limy.eclipse.qalab.builder.LimyQalabBuilder;

/**
 * QALab用のNatureを定義します。
 * @depend - - - LimyQalabBuilder
 * @author Naoki Iwami
 */
public class LimyQalabNature implements IProjectNature {

    /** Nature-ID */
    public static final String NATURE_ID = LimyQalabPlugin.PLUGIN_ID + ".LimyQalabNature";

    /** プロジェクト */
    private IProject project;
    
    public void configure() throws CoreException {
        IProjectDescription desc = project.getDescription();
        List<ICommand> commands = new ArrayList<ICommand>(Arrays.asList(desc.getBuildSpec()));
        if (!commands.contains(LimyQalabBuilder.BUILDER_ID)) {
            ICommand command = desc.newCommand();
            command.setBuilderName(LimyQalabBuilder.BUILDER_ID);
            commands.add(command);
            desc.setBuildSpec(commands.toArray(new ICommand[commands.size()]));
            project.setDescription(desc, null);
        }
    }

    public void deconfigure() throws CoreException {
        IProjectDescription desc = project.getDescription();
        List<ICommand> commands = new ArrayList<ICommand>(Arrays.asList(desc.getBuildSpec()));
        for (ListIterator<ICommand> it = commands.listIterator(); it.hasNext();) {
            ICommand command = it.next();
            if (command.getBuilderName().equals(LimyQalabBuilder.BUILDER_ID)) {
                it.remove();
            }
        }
        desc.setBuildSpec(commands.toArray(new ICommand[commands.size()]));
        project.setDescription(desc, null);
        
        project.deleteMarkers(LimyQalabMarker.DEFAULT_ID, true, IResource.DEPTH_INFINITE);

    }

    public IProject getProject() {
        return project;
    }

    public void setProject(IProject project) {
        this.project = project;
    }

}
