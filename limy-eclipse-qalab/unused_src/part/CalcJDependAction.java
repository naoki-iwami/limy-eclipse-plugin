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
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.limy.eclipse.common.LimyEclipseUtils;
import org.limy.eclipse.qalab.LimyQalabConstants;
import org.limy.eclipse.qalab.LimyQalabUtils;
import org.limy.eclipse.qalab.ProcessUtils;
import org.limy.eclipse.qalab.task.DistanceGraphTask;
import org.limy.eclipse.qalab.task.ProjectSupportTask;
import org.limy.eclipse.qalab.task.SimpleParam;
import org.limy.xml.task.VmStyleTask;

/**
 * @author Naoki Iwami
 */
public class CalcJDependAction extends AbstractPartAction {

    @Override
    protected String[] getTargetNames() {
        return new String[] { "jdepend", "jdepend-report-only" };
    }

    @Override
    protected void makeReport() throws CoreException {
        
        try {
            
            List<String> args = new ArrayList<String>();
            args.add("java");
            args.add("-classpath");
            args.add(createClasspath(getFilePrefix("jdepend")));
            args.add("jdepend.xmlui.JDepend");
            args.add(getDestFile("src").getAbsolutePath());
            args.add("-file");
            args.add(getDestFile("jdepend_report.xml").getAbsolutePath());
            for (IPath path : getEnv().getBinPaths(true)) {
                args.add(LimyQalabUtils.createFullPath(getJavaProject(), path));
            }
            
            ProcessUtils.execProgram(getBaseDir(), getWriter(),
                    args.toArray(new String[args.size()]));

            ProjectSupportTask task = new ProjectSupportTask();
            task.setOut(getDestFile("project-all.xml"));

            createGraph(getJavaProject().getProject(), task);
            for (IProject refProject : getEnv().getProject().getReferencedProjects()) {
                createGraph(refProject, task);
            }
            
            task.execute();
            
            VmStyleTask vmTask = new VmStyleTask();
            vmTask.createInfile().setValue(getDestFile("jdepend_report.xml").getAbsolutePath());
            vmTask.createInfile().setValue(getDestFile("project-all.xml").getAbsolutePath());
            vmTask.setOut(getDestFile("jdepend_report.html"));
            vmTask.setStyle(new File(LimyQalabUtils.getResourcePath("jdepend\\index.vm")));
            executeVmTask(vmTask);

            DistanceGraphTask graphTask = new DistanceGraphTask();
            graphTask.setIn(getDestFile("jdepend_report.xml"));
            graphTask.setOut(getDestFile("jdepend_distance_report.png"));
            graphTask.execute();
            
//            FileUtils.writeByteArrayToFile(getDestFile("pmd_report.xml"),
//                    getWriter().toString().getBytes("UTF-8"));
            

        } catch (IOException e) {
            LimyEclipseUtils.log(e);
        }
        
    }

    @Override
    protected File getReportHtml() {
        return getDestFile("jdepend_report.html");
    }

    // ------------------------ Private Methods

    private void createGraph(IProject refProject, ProjectSupportTask task)
            throws IOException, CoreException {

        String projectName = refProject.getName();

        List<String> args = new ArrayList<String>();
        args.add("java");
        args.add("-classpath");
        args.add(createClasspath(getFilePrefix("jdepend")));
        args.add("jdepend.xmlui.JDepend");
        args.add(getDestFile("src").getAbsolutePath());
        args.add("-file");
        args.add(getDestFile("jdepend_report_for_graph_"
                + projectName + ".xml").getAbsolutePath());
        
        // TODO excludeへの対応。JDependのソース修正が必要
//        String resources = getEnv().getStore().getString(LimyQalabConstants.EXCLUDE_JDEPENDS);
//        String[] excludes = resources.split("\n");
//        for (String exclude : excludes) {
//            createElement(mainEl, "exclude", "name", exclude);
//        }
        
        for (IPath path : getEnv().getBinPaths(true)) {
            args.add(LimyQalabUtils.createFullPath(getJavaProject(), path));
        }
                
        ProcessUtils.execProgram(getBaseDir(), getWriter(),
                args.toArray(new String[args.size()]));

        outputReport("jdepend_graph/index.vm",
                getDestFile("jdepend_report_for_graph_" + projectName + ".xml"),
                "jdepend_report_" + projectName + ".dot");
        
        execDot(refProject);
        
        SimpleParam param = task.createProject();
        param.setValue(projectName);

    }

    private void execDot(IProject refProject) throws IOException {
        String projectName = refProject.getName();
        ProcessUtils.execProgram(getBaseDir(), getWriter(),
                getEnv().getStore().getString(LimyQalabConstants.KEY_DOT_EXE),
                "-Tpng",
                "-o" + getDestFile("jdepend_report_" + projectName + ".png").getAbsolutePath(),
                getDestFile("jdepend_report_" + projectName + ".dot").getAbsolutePath()
        );
    }
    
}
