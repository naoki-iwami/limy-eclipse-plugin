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
package org.limy.eclipse.common.ui;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.progress.UIJob;
import org.limy.eclipse.common.LimyEclipsePluginUtils;

/**
 * リソースに対応したアクション基底クラスです。
 * <p>
 * アクション実行後にUI処理を行うことができます。
 * </p>
 * @author Naoki Iwami
 */
public abstract class AbstractResoueceAction implements IWorkbenchWindowActionDelegate {

    // ------------------------ Fields

    /** WorkbenchWindow */
    private IWorkbenchWindow window;

    /**
     * 選択範囲
     */
    private ISelection selection;

    // ------------------------ Implement Methods

    public void run(IAction action) {
        
        final List<IResource> resources = getSelectedResources(getWindow(), selection);
        
        String jobName = getJobName(action);
        Job job = new Job(jobName) {
            @Override
            protected IStatus run(IProgressMonitor monitor) {
                if (execBeforeUIJob()) {
                    try {
                        doActions(resources, monitor);
                    } catch (CoreException e) {
                        LimyEclipsePluginUtils.log(e);
                        return Status.CANCEL_STATUS;
                    }
                    execAfterUIJob();
                }
                return Status.OK_STATUS;
            }
        };
        job.schedule();
        
    }

    public void selectionChanged(IAction action, ISelection selection) {
        this.selection = selection;
    }

    public void dispose() {
        // no nothing
    }

    public void init(IWorkbenchWindow window) {
        this.window = window;
    }
    
    // ------------------------ Public Methods
    
    /**
     * 選択された全てのリソース要素を返します。
     * <p>
     * エディタ内 : 編集中のファイル<br>
     * エレメント : IResource, IFolder, IProject のインスタンスを選択した分だけ<br>
     * </p>
     * @param window WorkbenchWindow
     * @param selection 選択範囲
     * @return 選択された全てのリソース要素
     */
    public static List<IResource> getSelectedResources(
            IWorkbenchWindow window, ISelection selection) {

        List<IResource> results = new ArrayList<IResource>();

        if (selection instanceof ITextSelection) {
            // エディタ内に選択範囲があった場合、selection は空なので渡しても意味が無い
            
            // エディタで開いているファイルが表現するJavaクラスエレメント
            IEditorPart editor = window.getActivePage().getActiveEditor();
            IEditorInput input = editor.getEditorInput();
            if (input instanceof IFileEditorInput) {
                IFileEditorInput fileInput = (IFileEditorInput)input;
                results.add(fileInput.getFile());
            }
        }

        if (selection instanceof IStructuredSelection) {
            Object[] elements = ((IStructuredSelection)selection).toArray();
            for (Object element : elements) {
                if (element instanceof IResource) {
                    results.add((IResource)element);
                }
                if (element instanceof IJavaElement) {
                    results.add(((IJavaElement)element).getResource());
                }
            }
        }
        
        return results;
    }


    // ------------------------ Protected Methods
    
    protected final IWorkbenchWindow getWindow() {
        if (window == null) {
            IWorkbenchWindow[] windows = PlatformUI.getWorkbench().getWorkbenchWindows();
            if (windows.length > 0) {
                return windows[0];
            }
        }
        return window;
    }

    /**
     * 選択範囲を取得します。
     * @return 選択範囲
     */
    protected final ISelection getSelection() {
        return selection;
    }
    
    /**
     * 複数のリソースに対して処理を実行します。
     * デフォルトでは doAction() を繰り返し呼び出します。
     * 一括処理を行いたいときにはこのメソッドをオーバーライドする必要があります。
     * @param resources リソース
     * @param monitor 遷移モニタ
     * @throws CoreException コア例外
     */
    protected void doActions(List<IResource> resources, IProgressMonitor monitor)
            throws CoreException {
        
        for (IResource resource : resources) {
            doAction(resource, monitor);
        }
    }
    
    /**
     * Job実行前のUI関連処理を実行します。
     * <p>
     * サブクラスでは必要に応じてこのメソッドをOverrideして下さい。
     * </p>
     * @param monitor 
     * @return その後の処理を継続する場合はtrue
     * @throws CoreException 
     */
    protected boolean execBefore(IProgressMonitor monitor) throws CoreException {
        return true; // do nothing
    }

    /**
     * Job実行後のUI関連処理を実行します。
     * <p>
     * サブクラスでは必要に応じてこのメソッドをOverrideして下さい。
     * </p>
     * @param monitor 
     * @throws CoreException 
     */
    protected void execAfter(IProgressMonitor monitor) throws CoreException {
        // do nothing
    }

    protected String getJobName(IAction action) {
        return action != null ? action.getText() : "";
    }

    // ------------------------ Abstract Methods

    /**
     * 単一のリソースに対して処理を実行します。
     * @param resource リソース
     * @param monitor 遷移モニタ
     * @throws CoreException コア例外
     */
    protected abstract void doAction(IResource resource, IProgressMonitor monitor)
            throws CoreException;

    // ------------------------ Private Methods
    
    private boolean execBeforeUIJob() {
        final AtomicBoolean result = new AtomicBoolean();
        UIJob job = new UIJob("UIJob") {
            @Override
            public IStatus runInUIThread(IProgressMonitor monitor) {
                try {
                    result.set(execBefore(monitor));
                } catch (CoreException e) {
                    LimyEclipsePluginUtils.log(e);
                    return Status.CANCEL_STATUS;
                }
                return Status.OK_STATUS;
            }
        };
        job.schedule();
        try {
            job.join();
        } catch (InterruptedException e) {
            LimyEclipsePluginUtils.log(e);
        }
        return result.get();
    }
    
    private void execAfterUIJob() {
        UIJob job = new UIJob("UIJob") {
            @Override
            public IStatus runInUIThread(IProgressMonitor monitor) {
                try {
                    execAfter(monitor);
                } catch (CoreException e) {
                    LimyEclipsePluginUtils.log(e);
                    return Status.CANCEL_STATUS;
                }
                return Status.OK_STATUS;
            }
        };
        job.schedule();
    }

}
