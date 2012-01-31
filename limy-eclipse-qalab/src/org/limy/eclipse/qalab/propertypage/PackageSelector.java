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
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
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

/**
 * Javaパッケージ選択処理を実装したクラスです。
 * @author Naoki Iwami
 */
public class PackageSelector extends SelectionAdapter {

    // ------------------------ Fields

    /** プロジェクト一覧 */
    private final Collection<IProject> projects;
    
    /** 初期値の値が格納されたコントロール */
    private List control;
    
    /** フォルダ選択時に呼び出されるリスナー */
    private PropertyChangeListener listener;

    // ------------------------ Constructors

    /**
     * PackageSelectorインスタンスを構築します。
     * @param control 初期値の値が格納されたコントロール
     * @param project プロジェクト
     * @param listener フォルダ選択時に呼び出されるリスナー（null可）
     */
    public PackageSelector(List control, IProject project, PropertyChangeListener listener) {
        super();
        this.control = control;
        this.projects = new ArrayList<IProject>();
        projects.add(project);
        this.listener = listener;
    }

    /**
     * PackageSelectorインスタンスを構築します。
     * @param control 初期値の値が格納されたコントロール
     * @param projects プロジェクト一覧
     * @param listener フォルダ選択時に呼び出されるリスナー
     */
    public PackageSelector(List control, Collection<IProject> projects,
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
            setFromProject();
        } catch (JavaModelException e) {
            LimyEclipsePluginUtils.log(e);
        }
    }

    // ------------------------ Private Methods

    /**
     * プロジェクト内のファイルから選択します。
     * @throws JavaModelException 
     */
    private void setFromProject() throws JavaModelException {
        
        ElementTreeSelectionDialog dialog = new ElementTreeSelectionDialog(
                new Shell(), new WorkbenchLabelProvider(), new WorkbenchContentProvider());
  
        dialog.setTitle("Package Selection");
        dialog.setMessage("Package を選択して下さい。");
        ViewerFilter filter = new ViewerFilter() {
            @Override
            public boolean select(Viewer viewer, Object parentElement, Object element) {
                if (element instanceof ICompilationUnit) {
                    return false;
                }
                return true;
            }
        };
        dialog.addFilter(filter);
        
        Collection<IPackageFragment> targetElements = new ArrayList<IPackageFragment>();
        for (IProject project : projects) {
            IJavaProject javaProject = JavaCore.create(project);
            for (IPackageFragmentRoot root : javaProject.getAllPackageFragmentRoots()) {
                if (root.getKind() == IPackageFragmentRoot.K_SOURCE) {
                    // jarパッケージは対象外
                    IJavaElement[] children = root.getChildren();
                    for (IJavaElement child : children) {
                        targetElements.add((IPackageFragment)child);
                    }
                }
            }
        }
        
        dialog.setInput(new AdaptableList(targetElements));
        
        if (dialog.open() == Dialog.OK) {
            
            Object[] results = dialog.getResult();
            for (Object result : results) {
                IPackageFragment fragment = (IPackageFragment)result;
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
