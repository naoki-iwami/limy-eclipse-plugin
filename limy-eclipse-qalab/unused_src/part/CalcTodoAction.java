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

import org.eclipse.core.runtime.CoreException;
import org.limy.eclipse.qalab.task.TodoReportTask;

/**
 * @author Naoki Iwami
 */
public class CalcTodoAction extends AbstractPartAction {

    @Override
    protected String[] getTargetNames() {
        return new String[] { "todo", "todo-report-only" };
    }

    @Override
    protected void makeReport() throws CoreException {
        
        TodoReportTask task = new TodoReportTask();
        task.setSrcDir(getAllSrcDir());
        task.setOutputFile(getDestFile("todo_report.xml"));
        task.setInputCharset(getEncoding());
        task.execute();
        
        outputReport("todo");
    }

    @Override
    protected File getReportHtml() {
        return getDestFile("todo_report.html");
    }

    // ------------------------ Private Methods

}
