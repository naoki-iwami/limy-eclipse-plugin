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
import org.apache.tools.ant.taskdefs.Javadoc;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.types.Reference;
import org.eclipse.core.runtime.CoreException;
import org.limy.eclipse.qalab.LimyQalabUtils;

/**
 * @author Naoki Iwami
 */
public class MakeJavadocAction extends AbstractPartAction {


    @Override
    protected String[] getTargetNames() {
        return new String[] { "javadoc" };
    }

    @Override
    protected void makeReport() throws CoreException {
        
        Javadoc task = new Javadoc();
        
        Project project = new Project();
        Path path = new Path(project);
        for (String location : LimyQalabUtils.getJavaLibraries(getJavaProject())) {
            path.add(new Path(project, location));
        }
        project.addReference("all.classpath", path);
        
        task.setProject(project);
        
        task.setDestdir(getDestFile("javadoc"));
        task.setEncoding(getEncoding());
        task.setCharset(getEncoding());
        task.setDocencoding(getEncoding());
        
        Reference r = new Reference(project, "all.classpath");
        task.setClasspathRef(r);
        
        task.setUseExternalFile(true);
        
        FileSet fs = new FileSet();
        fs.setProject(project);
        fs.setDir(getAllSrcDir());
        fs.setIncludes("**/*.java");
        task.addFileset(fs);
        
        task.execute();
    }

    @Override
    protected File getReportHtml() {
        return getDestFile("javadoc/index.html");
    }

    // ------------------------ Private Methods

}
