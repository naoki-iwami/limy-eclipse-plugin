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
package org.limy.eclipse.qalab.propertypage;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.limy.eclipse.common.LimyEclipsePluginUtils;
import org.limy.eclipse.common.swt.FormDataCreater;
import org.limy.eclipse.qalab.common.LimyQalabConstants;
import org.limy.eclipse.qalab.common.LimyQalabEnvironment;

/**
 * UmlGraph関連ページです。
 * @author Naoki Iwami
 */
/* protected */ class PageUmlGraph extends StoredComposite {
    
    // ------------------------ Constructors

    /**
     * PageUmlGraphインスタンスを構築します。
     * @param parent
     * @param style
     * @param env 
     */
    public PageUmlGraph(Composite parent, int style, LimyQalabEnvironment env) {
        super(parent, style, env.getStore());
        try {
            createContents(this);
        } catch (CoreException e) {
            LimyEclipsePluginUtils.log(e);
        }
    }
    
    // ------------------------ Private Methods

    /**
     * @param comp
     * @throws CoreException 
     */
    private void createContents(Composite comp) throws CoreException {
        
        FormLayout layout = new FormLayout();
        layout.marginWidth = 4;
        layout.marginHeight = 4;
        comp.setLayout(layout);

        Button horizontalCheck = new Button(comp, SWT.CHECK);
        horizontalCheck.setText("グラフを横方向に並べる");
        horizontalCheck.setLayoutData(FormDataCreater.maxWidthControlDown(null, 4));
        addField(LimyQalabConstants.UMLGRAPH_HORIZONTAL, horizontalCheck);

        Button javadocCheck = new Button(comp, SWT.CHECK);
        javadocCheck.setText("Javadoc出力にUMLGraphを使用する（多少時間が掛かります）");
        javadocCheck.setLayoutData(FormDataCreater.maxWidthControlDown(horizontalCheck, 4));
        addField(LimyQalabConstants.UMLGRAPH_JAVADOC, javadocCheck);

        Button inferrelCheck = new Button(comp, SWT.CHECK);
        inferrelCheck.setText("クラスが持つフィールドとの関係を矢印で結ぶ");
        inferrelCheck.setLayoutData(FormDataCreater.maxWidthControlDown(javadocCheck, 4));
        addField(LimyQalabConstants.UMLGRAPH_INFERREL, inferrelCheck);

    }

}
