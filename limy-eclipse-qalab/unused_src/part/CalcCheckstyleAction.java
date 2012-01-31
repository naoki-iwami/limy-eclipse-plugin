/*
 * Created 2007/06/23
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

import org.eclipse.core.runtime.CoreException;
import org.limy.eclipse.common.LimyEclipseUtils;
import org.limy.eclipse.qalab.LimyQalabConstants;
import org.limy.eclipse.qalab.LimyQalabUtils;
import org.limy.eclipse.qalab.ProcessUtils;
import org.limy.eclipse.qalab.task.CheckStyleSupportTask;

/**
 * Checkstyleを計測するアクションクラスです。
 * @author Naoki Iwami
 */
public class CalcCheckstyleAction extends AbstractPartAction {

    // ------------------------ Implement Methods

    @Override
    protected String[] getTargetNames() {
        return new String[] { "checkstyle", "checkstyle-report-only" };
    }
    
    @Override
    protected void makeReport() throws CoreException {

        createSupportFiles();
        
        try {
            ProcessUtils.execProgram(getBaseDir(), getWriter(),
                    "java",
                    "-classpath", createClasspath(getFilePrefix("checkstyle-all")),
                    "com.puppycrawl.tools.checkstyle.Main",
                    "-c", getDestFile("checkstyle.xml").getAbsolutePath(),
                    "-p", getDestFile("checkstyle.properties").getAbsolutePath(),
                    "-f", "xml",
                    "-o", getDestFile("checkstyle_report.xml").getAbsolutePath(),
                    "-r", getAllSrcDir().getAbsolutePath()
            );
        } catch (IOException e) {
            LimyEclipseUtils.log(e);
        }
        
        outputReport("checkstyle");
    }


    @Override
    protected File getReportHtml() {
        return getDestFile("checkstyle_report.html");
    }

    // ------------------------ Private Methods

    private void createSupportFiles() throws CoreException {
        CheckStyleSupportTask task = new CheckStyleSupportTask();
        
        File configFile = LimyQalabUtils.getConfigFile(getEnv(),
                "sun_checks.xml",
                LimyQalabConstants.KEY_CHK_TYPE, LimyQalabConstants.KEY_CHK_CFG);
        task.setConfigFile(configFile);
        task.setDestDir(getDestDir());
        task.setEncoding(getEncoding());
        
        task.execute();
    }


    
}
