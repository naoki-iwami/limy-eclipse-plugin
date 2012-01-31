/*
 * Created 2008/08/23
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
import org.limy.eclipse.qalab.outline.PopupImage;
import org.limy.eclipse.qalab.outline.sequence.SequenceImageCreator;

/**
 * メソッドのシーケンス図を表示
 * @author Naoki Iwami
 */
public class ShowSequenceAction extends AbstractJavaElementAction {

    /** イメージ作成担当 */
    private SequenceImageCreator imageCreator;
    
    /** 作成したイメージ */
    private PopupImage image;

    @Override
    protected void doAction(IJavaElement javaElement, IProgressMonitor monitor)
            throws CoreException {
        
        // メソッド選択時のみ対応
        if (javaElement instanceof IMethod) {
            IMethod method = (IMethod)javaElement;
            LimyQalabEnvironment env = LimyQalabPluginUtils.createEnv(
                    javaElement.getResource().getProject());
            
            imageCreator = new SequenceImageCreator(env, method);
            try {
                image = PopupGraphHelper.createImage(imageCreator);
            } catch (IOException e) {
                LimyEclipsePluginUtils.log(e);
            }
        }
        
    }

    @Override
    protected void execAfter(IProgressMonitor monitor) {
        PopupGraphHelper.openDialog(imageCreator, imageCreator, image);
    }

}
