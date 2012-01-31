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

import org.apache.commons.io.FileUtils;
import org.eclipse.core.runtime.CoreException;
import org.limy.eclipse.common.LimyEclipseUtils;
import org.limy.eclipse.qalab.ProcessUtils;

/**
 * @author Naoki Iwami
 */
public class CalcJavancssAction extends AbstractPartAction {

    @Override
    protected String[] getTargetNames() {
        return new String[] { "javancss", "javancss-report-only" };
    }

    @Override
    protected void makeReport() throws CoreException {
        
        try {
            
            ProcessUtils.execProgram(getBaseDir(), getWriter(),
                    "java",
                    "-classpath", createClasspath(
                            getFile("javancss.jar"),
                            getFile("ccl.jar"),
                            getFile("jhbasic.jar")
                    ),
                    "javancss.Main",
                    "-all",
                    "-xml",
                    "-out", getDestFile("javancss_report.xml").getAbsolutePath(),
                    getDestFile("src").getAbsolutePath() + "\\*.java"
            );

            FileUtils.writeByteArrayToFile(getDestFile("pmd_report.xml"),
                    getWriter().toString().getBytes("UTF-8"));

        } catch (IOException e) {
            LimyEclipseUtils.log(e);
        }
        
        outputReport("javancss");

    }

    @Override
    protected File getReportHtml() {
        return getDestFile("javancss_report.html");
    }

    // ------------------------ Private Methods

}
