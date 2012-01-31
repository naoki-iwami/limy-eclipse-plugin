/*
 * Created 2007/06/24
 * Copyright (C) 2003-2007  Naoki Iwami (naoki@limy.org)
 *
 * This file is part of Limy Eclipse Plugin.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package org.limy.eclipse.qalab.action.part;

import java.io.File;

import org.apache.tools.ant.Project;
import org.apache.tools.ant.types.FileSet;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.limy.eclipse.qalab.LimyQalabUtils;
import org.limy.eclipse.qalab.task.Java2HtmlTask;

/**
 * @author Naoki Iwami
 */
public class Java2HtmlAction extends AbstractPartAction {

    @Override
    protected String[] getTargetNames() {
        return new String[] { "java2html" };
    }

    @Override
    protected void makeReport() throws CoreException {
        
        Java2HtmlTask task = new Java2HtmlTask();
        task.setProject(new Project());
        task.setDestDir(getDestFile("javasrc"));
        task.setEnableLineAnchor(true);
        task.setInputCharset(getEncoding());
        
        for (IPath path : getEnv().getMainSourcePaths(true)) {
            FileSet fileSet = new FileSet();
            fileSet.setDir(new File(LimyQalabUtils.createFullPath(getJavaProject(), path)));
            fileSet.setIncludes("**/*.java");
            task.addFileset(fileSet);
        }
        
        task.execute();
    }

    @Override
    protected File getReportHtml() {
        return getDestFile("javasrc/index.html");
    }

    // ------------------------ Private Methods

}
