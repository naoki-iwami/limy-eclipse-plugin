/*
 * Created 2007/02/14
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

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jface.action.IAction;
import org.limy.eclipse.common.LimyEclipsePluginUtils;
import org.limy.eclipse.common.ui.AbstractJavaElementAction;
import org.limy.eclipse.qalab.LimyQalabPluginUtils;
import org.limy.eclipse.qalab.common.LimyQalabEnvironment;
import org.limy.eclipse.qalab.outline.PopupImage;
import org.limy.eclipse.qalab.outline.umlimage.UmlImageCreator;

/**
 * クラス図をポップアップ表示するアクションクラスです。
 * @author Naoki Iwami
 */
public class ViewGraphAction extends AbstractJavaElementAction {

    // ------------------------ Fields

    /** イメージ作成担当 */
    private UmlImageCreator imageCreator;
    
    /** 作成されたイメージ */
    private PopupImage image;

    // ------------------------ Override Methods

    @Override
    protected void doAction(IJavaElement javaElement, IProgressMonitor monitor)
            throws CoreException {
        
        LimyQalabEnvironment env = LimyQalabPluginUtils.createEnv(
                javaElement.getResource().getProject());

        imageCreator = new UmlImageCreator(env, javaElement);
        try {
            image = PopupGraphHelper.createImage(imageCreator);
        } catch (IOException e) {
            LimyEclipsePluginUtils.log(e);
        }
    }
    
    @Override
    protected String getJobName(IAction action) {
        return "クラス図作成中...";
    }

    @Override
    protected void execAfter(IProgressMonitor monitor) throws CoreException {
        PopupGraphHelper.openDialog(imageCreator, imageCreator, image);
    }

}
