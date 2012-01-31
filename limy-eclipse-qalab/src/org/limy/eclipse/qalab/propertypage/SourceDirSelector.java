/*
 * Created 2007/01/08
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
package org.limy.eclipse.qalab.propertypage;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.core.resources.IProject;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.ElementTreeSelectionDialog;
import org.eclipse.ui.model.AdaptableList;
import org.eclipse.ui.model.WorkbenchContentProvider;
import org.eclipse.ui.model.WorkbenchLabelProvider;
import org.limy.eclipse.common.LimyEclipsePluginUtils;
import org.limy.eclipse.qalab.Messages;

/**
 * Javaソースディレクトリ選択処理を実装したクラスです。
 * @author Naoki Iwami
 */
public class SourceDirSelector extends SelectionAdapter {

    // ------------------------ Fields

    /** プロジェクト一覧 */
    private final Collection<IProject> projects;
    
    /** 初期値の値が格納されたコントロール */
    private List control;
    
    /** フォルダ選択時に呼び出されるリスナー */
    private PropertyChangeListener listener;

    // ------------------------ Constructors

    /**
     * SourceDirSelectorインスタンスを構築します。
     * @param control 初期値の値が格納されたコントロール
     * @param project プロジェクト
     * @param listener フォルダ選択時に呼び出されるリスナー（null可）
     */
    public SourceDirSelector(List control, IProject project, PropertyChangeListener listener) {
        super();
        this.control = control;
        this.projects = new ArrayList<IProject>();
        projects.add(project);
        this.listener = listener;
    }

    /**
     * SourceDirSelectorインスタンスを構築します。
     * @param control 初期値の値が格納されたコントロール
     * @param projects プロジェクト一覧
     * @param listener フォルダ選択時に呼び出されるリスナー
     */
    public SourceDirSelector(List control, Collection<IProject> projects,
            PropertyChangeListener listener) {
        
        super();
        this.control = control;
        this.projects = projects;
        this.listener = listener;
    }

    // ------------------------ Override Methods

    @Override
    public void widgetSelected(SelectionEvent evt) {
        try {
            setPackageDir();
        } catch (JavaModelException e) {
            LimyEclipsePluginUtils.log(e);
        }
    }

    // ------------------------ Private Methods

    /**
     * パッケージディレクトリを選択したときの処理を行います。
     * @throws JavaModelException 
     */
    private void setPackageDir() throws JavaModelException {
        
        ElementTreeSelectionDialog dialog = new ElementTreeSelectionDialog(
                new Shell(), new WorkbenchLabelProvider(), new WorkbenchContentProvider());
  
        dialog.setTitle(Messages.TITLE_SELECT_SOURCE_DIR);
        dialog.setMessage(Messages.MES_SELECT_SOURCE_DIR);
        ViewerFilter filter = new ViewerFilter() {
            @Override
            public boolean select(Viewer viewer, Object parentElement, Object element) {
                if (element instanceof IPackageFragmentRoot) {
                    return true;
                }
                return false;
            }
        };
        dialog.addFilter(filter);
        
        Collection<IPackageFragmentRoot> targetElements = new ArrayList<IPackageFragmentRoot>();
        for (IProject project : projects) {
            IJavaProject javaProject = JavaCore.create(project);
            for (IPackageFragmentRoot root : javaProject.getAllPackageFragmentRoots()) {
                if (root.getKind() == IPackageFragmentRoot.K_SOURCE) {
                    // jarパッケージは対象外
                    targetElements.add(root);
                }
            }
        }
        
        dialog.setInput(new AdaptableList(targetElements));
        
        if (dialog.open() == Dialog.OK) {
            
            Object[] results = dialog.getResult();
            for (Object result : results) {
                IPackageFragmentRoot fragment = (IPackageFragmentRoot)result;
                control.add(fragment.getElementName());
                if (listener != null) {
                    PropertyChangeEvent evt = new PropertyChangeEvent(
                            control, "resultPath", null, fragment.getPath()
                    );
                    listener.propertyChange(evt);
                }
            }
        }
    }

}
