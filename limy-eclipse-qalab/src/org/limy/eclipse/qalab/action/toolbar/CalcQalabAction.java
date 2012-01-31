/*
 * Created 2007/06/24
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
package org.limy.eclipse.qalab.action.toolbar;

import java.io.File;
import java.io.IOException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.preference.IPreferenceStore;
import org.limy.common.ProcessUtils;
import org.limy.eclipse.qalab.common.LimyQalabConstants;

/**
 * @author Naoki Iwami
 */
public class CalcQalabAction extends AbstractPartAction {

    @Override
    public String[] getTargetNames() throws IOException {
        
        IProgressMonitor monitor = getProgressMonitor();
        
        exec("clean", monitor);
        exec(LimyQalabConstants.ENABLE_CHECKSTYLE, new CalcCheckstyleAction(), monitor);
        exec(LimyQalabConstants.ENABLE_PMD, new CalcPmdAction(), monitor);
        exec(LimyQalabConstants.ENABLE_FINDBUGS, new CalcFindbugsAction(), monitor);
        exec(LimyQalabConstants.ENABLE_NCSS, new CalcJavancssAction(), monitor);
        exec(LimyQalabConstants.ENABLE_JUNIT, new CalcCoberturaAction(), monitor);
        exec(LimyQalabConstants.ENABLE_JDEPEND, new CalcJDependAction(), monitor);
        exec(LimyQalabConstants.ENABLE_UMLGRAPH, new CalcUmlgraphAction(), monitor);
        exec(new Java2HtmlAction(), monitor);
        exec(LimyQalabConstants.ENABLE_TODO, new CalcTodoAction(), monitor);
        exec(new MakeJavadocAction(), monitor);
        
        exec("qalab", monitor);
        exec("qalab-chart", monitor);
        exec("qalab-chart-coverage", monitor);
        exec("qalab-report-only", monitor);
        return new String[] { "finish" };
    }

    @Override
    protected File getReportHtml() {
        return getDestFile("qalab/index.html");
    }

    // ------------------------ Private Methods
    
    /**
     * @param storeKey このアクションが有効かどうかを示すストアキー
     * @param action アクション実体
     * @param monitor 遷移モニタ
     * @throws IOException I/O例外
     */
    private void exec(String storeKey,
            ToolbarAction action, IProgressMonitor monitor) throws IOException {
        
        IPreferenceStore store = getEnv().getStore();
        if (store.getBoolean(storeKey)) {
            exec(action, monitor);
        }
    }

    private void exec(ToolbarAction action, IProgressMonitor monitor) throws IOException {
        String[] names = action.getTargetNames();
        for (String name : names) {
            exec(name, monitor);
        }
    }

    private void exec(String target, IProgressMonitor monitor) throws IOException {
        monitor.setTaskName(target);
        monitor.worked(1);
        ProcessUtils.execProgram(getBaseDir(), getWriter(),
                getAntPath(), "-f", getBuildXml(), target);
    }
    
}
