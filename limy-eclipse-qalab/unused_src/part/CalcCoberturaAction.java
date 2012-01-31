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
import java.util.Collection;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.SuffixFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.optional.junit.AggregateTransformer;
import org.apache.tools.ant.taskdefs.optional.junit.XMLResultAggregator;
import org.apache.tools.ant.taskdefs.optional.junit.AggregateTransformer.Format;
import org.apache.tools.ant.types.FileSet;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.limy.eclipse.common.LimyEclipseUtils;
import org.limy.eclipse.qalab.LimyQalabUtils;
import org.limy.eclipse.qalab.ProcessUtils;

/**
 * @author Naoki Iwami
 */
public class CalcCoberturaAction extends AbstractPartAction {

    @Override
    protected String[] getTargetNames() {
        return new String[] { "cobertura", "cobertura-report-only" };
    }

    @Override
    protected void makeReport() throws CoreException {
        
        try {
            
            FileUtils.deleteDirectory(getDestFile("instrumented"));
            getDestFile("cobertura.ser").delete();
            
            FileUtils.deleteDirectory(getDestFile("classes"));
            copyAllClassFiles();
            
            ProcessUtils.execProgram(getBaseDir(), getWriter(),
                    "java",
                    "-classpath", createClasspath(
                            getFile("cobertura.jar"), getFilePrefix("asm"),
                            getFilePrefix("asm-tree"), getFilePrefix("log4j"),
                            getFilePrefix("jakarta-oro")
                    ),
                    "net.sourceforge.cobertura.instrument.Main",
                    "--destination",
                    getDestFile("instrumented").getAbsolutePath(),
                    "--datafile",
                    getDestFile("cobertura.ser").getAbsolutePath(),
                    getDestFile("classes").getAbsolutePath()
            );
            
            getDestFile("coverage/xml").mkdirs();
            
            List<String> args = new ArrayList<String>();
            args.add("java");
            args.add("-Dnet.sourceforge.cobertura.datafile="
                    + getDestFile("cobertura.ser").getAbsolutePath());
            args.add("-classpath");
            args.add(createClasspath(
                    LimyQalabUtils.getPath(""),
                    LimyQalabUtils.getPath("bin"),
                    getFile("ant.jar"),
                    getFile("ant-junit.jar"),
                    getFilePrefix("junit")
            ));
            args.add("org.limy.eclipse.qalab.action.part.JUnit4Support");
            args.add("--destdir");
            args.add(getDestDir().getAbsolutePath());
            
            for (IPath path : getEnv().getTestSourcePaths(true)) {
                args.add("--testsrc");
                args.add(LimyQalabUtils.createFullPath(getJavaProject(), path));
            }

            args.add("--classpath");
            args.add(getFilePrefix("junit"));
            args.add("--classpath");
            args.add(getDestFile("instrumented").getAbsolutePath());
            for (IPath path : getEnv().getBinPaths(true)) {
                String dir = LimyQalabUtils.createFullPath(getJavaProject(), path);
                args.add("--classpath");
                args.add(dir);
            }
            args.add("--classpath");
            args.add(getFile("cobertura.jar"));
            args.add("--classpath");
            args.add(getFilePrefix("asm"));
            args.add("--classpath");
            args.add(getFilePrefix("asm-tree"));
            args.add("--classpath");
            args.add(getFilePrefix("log4j"));
            args.add("--classpath");
            args.add(getFilePrefix("jakarta-oro"));
            for (String location : LimyQalabUtils.getJavaLibraries(getJavaProject())) {
                args.add("--classpath");
                args.add(location);
            }

            ProcessUtils.execProgram(getBaseDir(), getWriter(),
                    args.toArray(new String[args.size()]));
            
            ProcessUtils.execProgram(getBaseDir(), getWriter(),
                    "java",
                    "-classpath", createClasspath(
                            getFile("cobertura.jar"), getFilePrefix("asm"),
                            getFilePrefix("asm-tree"), getFilePrefix("log4j"),
                            getFilePrefix("jakarta-oro")
                    ),
                    "net.sourceforge.cobertura.reporting.Main",
                    "--format",
                    "xml",
                    "--destination",
                    getDestDir().getAbsolutePath(),
                    "--datafile",
                    getDestFile("cobertura.ser").getAbsolutePath(),
                    "--charset",
                    getEncoding(),
                    getAllSrcDir().getAbsolutePath()
            );

            FileUtils.writeByteArrayToFile(getDestFile("test.txt"),
                    getWriter().toString().getBytes("UTF-8"));
            
            getDestFile("junit").mkdirs();
            
            XMLResultAggregator reportTask = new XMLResultAggregator();
            reportTask.setTodir(getDestFile("junit"));
            FileSet fs = new FileSet();
            fs.setDir(getDestFile("coverage/xml"));
            fs.setIncludes("TEST-*.xml");
            AggregateTransformer report = reportTask.createReport();
            report.setFormat((Format)Format.getInstance(
                    Format.class, AggregateTransformer.FRAMES));
            report.setTodir(getDestFile("junit/html"));
            reportTask.addFileSet(fs);
            
            Project project = new Project();
            project.setProperty("java.io.tmpdir", System.getProperty("java.io.tmpdir"));
            reportTask.setProject(project);
            
            reportTask.execute();
            
            ProcessUtils.execProgram(getBaseDir(), getWriter(),
                    "java",
                    "-classpath", createClasspath(
                            getFile("cobertura.jar"), getFilePrefix("asm"),
                            getFilePrefix("asm-tree"), getFilePrefix("log4j"),
                            getFilePrefix("jakarta-oro")
                    ),
                    "net.sourceforge.cobertura.reporting.Main",
                    "--format",
                    "html",
                    "--destination",
                    getDestFile("coverage/html").getAbsolutePath(),
                    "--datafile",
                    getDestFile("cobertura.ser").getAbsolutePath(),
                    "--charset",
                    getEncoding(),
                    getAllSrcDir().getAbsolutePath()
            );


        } catch (IOException e) {
            LimyEclipseUtils.log(e);
        }
        
//        outputReport("pmd");

    }

    @Override
    protected File getReportHtml() {
        return getDestFile("coverage/html/index.html");
    }

    // ------------------------ Private Methods

    /**
     * instrument対象の全クラスファイルをdest/classes以下にコピーします。
     * @throws IOException 
     * @throws CoreException 
     */
    private void copyAllClassFiles() throws CoreException, IOException {
        
        for (IPath path : getEnv().getBinPaths(true)) {
            String dir = LimyQalabUtils.createFullPath(getJavaProject(), path);
            FileUtils.copyDirectory(new File(dir), getDestFile("classes"));
        }
        Collection<Object> files = FileUtils.listFiles(
                getDestFile("classes"), new SuffixFileFilter("Test.class"),
                TrueFileFilter.INSTANCE);
        for (Object file : files) {
            ((File)file).delete();
        }
        
        
    }
}
