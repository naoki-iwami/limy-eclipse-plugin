/*
 * Created 2007/01/11
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
package org.limy.eclipse.common.jdt;

import java.util.Collection;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.progress.UIJob;
import org.limy.eclipse.common.LimyEclipsePluginUtils;

/**
 * UI処理を伴う基底アクションクラスです。
 * <p>
 * このクラスを継承して doRun() メソッドを実装します。
 * </p>
 * @author Naoki Iwami
 */
public abstract class AbstractUIAction implements IWorkbenchWindowActionDelegate {

    // ------------------------ Classes

    /**
     * 内部的に使用するUIJobクラスです。
     * @author Naoki Iwami
     */
    private class LimyUIActionJob extends UIJob {

        /**
         * LimyUIActionJobインスタンスを構築します。
         * @param name
         */
        public LimyUIActionJob(String name) {
            super(name);
        }

        @Override
        public IStatus runInUIThread(IProgressMonitor monitor) {
            try {
                doRun(selection, monitor);
            } catch (CoreException e) {
                LimyEclipsePluginUtils.log(e);
            }
            return Status.OK_STATUS;
        }
        
    }

    // ------------------------ Fields

    /** WorkbenchWindow */
    private IWorkbenchWindow window;
    
    /**
     * 選択範囲
     * <p>
     * エレメントであれば IStructuredSelection,
     * エディタ内であれば　ITextSelection （ただしオフセット情報は無し）
     * </p>
     */
    private ISelection selection;
    
    // ------------------------ Abstract Methods

    /**
     * 処理を実行します。
     * @param selection 
     * @param monitor 
     * @throws CoreException 
     */
    public abstract void doRun(ISelection selection, IProgressMonitor monitor)
            throws CoreException;

    // ------------------------ Implement Methods

    public void init(IWorkbenchWindow window) {
        this.window = window;
    }

    public void dispose() {
        // do nothing
    }

    public void run(IAction action) {
        String jobName = action != null ? action.getText() : "";
        LimyUIActionJob job = new LimyUIActionJob(jobName);
        job.schedule();
    }

    public void selectionChanged(IAction action, ISelection selection) {
        this.selection = selection;
    }
    
    // ------------------------ Protected Methods

    protected IWorkbenchWindow getWindow() {
        if (window == null) {
            // ポップアップメニュー時のアクション対応
            IWorkbenchWindow[] windows = PlatformUI.getWorkbench().getWorkbenchWindows();
            if (windows.length > 0) {
                return windows[0];
            }
        }
        return window;
    }
    
    /**
     * 選択された全てのJava要素を返します。
     * @return 選択された全てのJava要素
     * @throws JavaModelException Javaモデル例外
     */
    protected Collection<IJavaElement> getSelectedJavaElements() throws JavaModelException {
        return LimyJavaUtils.getSelectedJavaElements(getWindow(), getSelection());
    }
    
    protected ISelection getSelection() {
        return selection;
    }
    
}
