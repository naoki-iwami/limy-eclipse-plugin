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
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.limy.eclipse.common.LimyEclipseUtils;
import org.limy.eclipse.qalab.LimyQalabUtils;
import org.limy.eclipse.qalab.ProcessUtils;
import org.limy.xml.VmParam;

import edu.umd.cs.findbugs.Version;

/**
 * @author Naoki Iwami
 */
public class CalcFindbugsAction extends AbstractPartAction {

    @Override
    protected String[] getTargetNames() {
        return new String[] { "findbugs", "findbugs-report-only" };
    }

    @Override
    protected void makeReport() throws CoreException {
        
        try {

            Collection<String> args = new ArrayList<String>();
            args.addAll(Arrays.asList(new String[] {
                    "java",
                    "-Dfindbugs.home=" + LimyQalabUtils.getResourcePath("external-lib/findbugs"),
                    "-classpath", createClasspath(
                            getFile("bcel.jar"), getFile("dom4j-full.jar"),
                            getFilePrefix("asm"), getFilePrefix("asm-tree")
                    ),
                    "-jar", getFile("findbugs.jar"),
                    
                    "-xdocs",
                    "-outputFile", getDestFile("findbugs_report.xml").getAbsolutePath(),
                    "-sourcepath", getDestFile("src").getAbsolutePath(),
            }));
            for (IPath location : getEnv().getBinPaths(true)) {
                String path = LimyQalabUtils.createFullPath(getEnv().getJavaProject(), location);
                args.add(path);
            }
            ProcessUtils.execProgram(getBaseDir(), getWriter(),
                    args.toArray(new String[args.size()]));

        } catch (IOException e) {
            LimyEclipseUtils.log(e);
        }
        
        outputReport("findbugs", new VmParam("version", Version.RELEASE));

    }

    @Override
    protected File getReportHtml() {
        return getDestFile("findbugs_report.html");
    }

    // ------------------------ Private Methods

}
