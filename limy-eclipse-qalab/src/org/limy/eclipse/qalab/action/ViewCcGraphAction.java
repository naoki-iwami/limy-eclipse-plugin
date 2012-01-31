/*
 * Created 2007/02/27
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
import org.eclipse.jdt.core.IMethod;
import org.limy.eclipse.common.LimyEclipsePluginUtils;
import org.limy.eclipse.common.ui.AbstractJavaElementAction;
import org.limy.eclipse.qalab.LimyQalabPluginUtils;
import org.limy.eclipse.qalab.common.LimyQalabEnvironment;
import org.limy.eclipse.qalab.outline.CcImageCreator;
import org.limy.eclipse.qalab.outline.PopupImage;

/**
 *
 * @author Naoki Iwami
 */
public class ViewCcGraphAction extends AbstractJavaElementAction {


    // ------------------------ Fields

    /** イメージ作成担当 */
    private CcImageCreator imageCreator;
    
    /** 作成されたイメージ */
    private PopupImage image;

    // ------------------------ Override Methods

    @Override
    protected void doAction(IJavaElement javaElement, IProgressMonitor monitor)
            throws CoreException {

        if (javaElement instanceof IMethod) {
            IMethod method = (IMethod)javaElement;
            LimyQalabEnvironment env = LimyQalabPluginUtils.createEnv(
                    javaElement.getResource().getProject());
            
            imageCreator = new CcImageCreator(env, method);
            try {
                image = PopupGraphHelper.createImage(imageCreator);
            } catch (IOException e) {
                LimyEclipsePluginUtils.log(e);
            }
        }
        
    }
    
    @Override
    protected void execAfter(IProgressMonitor monitor) throws CoreException {
        PopupGraphHelper.openDialog(imageCreator, imageCreator, image);
    }

}
