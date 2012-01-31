/*
 * Created 2007/07/04
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
import java.util.ArrayList;
import java.util.Collection;

import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.optional.junit.BatchTest;
import org.apache.tools.ant.taskdefs.optional.junit.FormatterElement;
import org.apache.tools.ant.taskdefs.optional.junit.JUnitTask;
import org.apache.tools.ant.taskdefs.optional.junit.JUnitTask.ForkMode;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.types.Environment.Variable;

/**
 * @author Naoki Iwami
 */
public class JUnit4Support {
    
    public static void main(String[] args) throws Exception {
        
        String destDir = null;
        Collection<String> testSrcs = new ArrayList<String>();
        Collection<String> classpathes = new ArrayList<String>();
        
        for (int i = 0; i < args.length; i++) {
            String type = args[i];
            if ("--destdir".equals(type)) {
                destDir = args[++i];
            }
            if ("--testsrc".equals(type)) {
                testSrcs.add(args[++i]);
            }
            if ("--classpath".equals(type)) {
                classpathes.add(args[++i]);
            }
        }
        
        JUnitTask task = new JUnitTask();
        
        Project project = new Project();
        task.setProject(project);

        task.setFork(false);
//        task.setFork(true);
//        task.setForkMode(new ForkMode(ForkMode.ONCE));
        task.setShowOutput(true);
        
//        Variable variable = new Variable();
//        variable.setKey("net.sourceforge.cobertura.datafile");
//        variable.setValue(new File(destDir, "cobertura.ser").getAbsolutePath());
//        task.addSysproperty(variable);
        
        for (String str : classpathes) {
            Path path = task.createClasspath();
            path.setLocation(new File(str));
            System.out.println("classpath : " + str);
        }
//        Path cp = task.createClasspath();
//        cp.setLocation(new File("C:\\var\\prog\\eclipse-plugin\\limy-eclipse-qalab\\resource\\external-lib\\junit-4.1.jar"));
        
        FormatterElement fe = new FormatterElement();
        fe.setClassname(FormatterElement.XML_FORMATTER_CLASS_NAME);
        task.addFormatter(fe);
        
        BatchTest bt = task.createBatchTest();
        bt.setTodir(new File(destDir, "coverage/xml"));
        for (String str : testSrcs) {
            System.out.println("testSrc : " + str);
            FileSet fs = new FileSet();
            fs.setDir(new File(str));
            fs.setIncludes("**/*Test.java");
            fs.setExcludes("**/Abstract*Test.java");
            bt.addFileSet(fs);
        }
        
        task.execute();
    }

}
