/*
 * Created 2007/08/29
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
package org.limy.eclipse.qalab.action;

import java.io.IOException;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IType;
import org.eclipse.jface.action.IAction;
import org.limy.eclipse.common.LimyEclipsePluginUtils;
import org.limy.eclipse.common.jdt.LimyJavaUtils;
import org.limy.eclipse.common.ui.AbstractJavaElementAction;
import org.limy.eclipse.qalab.LimyQalabPluginUtils;
import org.limy.eclipse.qalab.common.LimyQalabEnvironment;
import org.limy.eclipse.qalab.outline.PopupImage;
import org.limy.eclipse.qalab.outline.jdepend.JDependDialogSupport;
import org.limy.eclipse.qalab.outline.jdepend.JDependListenerCreator;
import org.limy.eclipse.qalab.outline.jdepend.JdependImageCreator;

/**
 * 選択したJava要素以下のパッケージ依存関係をグラフ化して表示するアクションクラスです。
 * @author Naoki Iwami
 */
public class ViewJdependAction extends AbstractJavaElementAction {

    // ------------------------ Fields

    /** イメージ作成担当 */
    private JdependImageCreator imageCreator;
    
    /** 作成されたイメージ */
    private PopupImage image;

    /** 対象要素 */
    private IJavaElement targetElement;

    // ------------------------ Override Methods

    @Override
    protected void doAction(IJavaElement javaElement, IProgressMonitor monitor)
            throws CoreException {
        
        LimyQalabEnvironment env = LimyQalabPluginUtils.createEnv(
                javaElement.getResource().getProject());

        targetElement = javaElement;
        
        if (javaElement.getResource().getType() == IResource.FILE) {
            IType type = LimyJavaUtils.getPrimaryType(javaElement);
            if (type != null) {
                targetElement = type.getCompilationUnit().getParent();
            }
        }
        
        imageCreator = new JdependImageCreator(env, targetElement);
        try {
            image = PopupGraphHelper.createImage(imageCreator);
        } catch (IOException e) {
            LimyEclipsePluginUtils.log(e);
        }
        
    }
    
    @Override
    protected String getJobName(IAction action) {
        return "パッケージ関連図作成中...";
    }

    @Override
    protected void execAfter(IProgressMonitor monitor) throws CoreException {
        JDependDialogSupport support = new JDependDialogSupport(
                targetElement, imageCreator);
        JDependListenerCreator listenerCreator = new JDependListenerCreator(
                imageCreator, image, support);
        PopupGraphHelper.openDialog(support, listenerCreator, image);
    }

}
